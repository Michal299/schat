package pl.edu.pg.eti.backend.connection;

import org.apache.commons.lang3.SerializationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.edu.pg.eti.backend.message.MessageUtils;
import pl.edu.pg.eti.backend.message.entity.Message;
import pl.edu.pg.eti.backend.message.entity.MessageType;
import pl.edu.pg.eti.backend.secure.CipherUtils;
import pl.edu.pg.eti.backend.secure.LoginService;
import pl.edu.pg.eti.backend.secure.SessionKey;
import pl.edu.pg.eti.backend.secure.SessionKeyUtils;

import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

public class ConnectionService {

    private final Logger log = LoggerFactory.getLogger(ConnectionService.class);
    private final LoginService loginService;

    final Map<String, Connection> connections;

    public ConnectionService(final LoginService loginService) {
        this.loginService = loginService;
        connections = new TreeMap<>();
    }

    public Connection createNewConnection(final String address, int port) {
        try {
            final Socket socket = new Socket(address, port);
            final Sender sender = new Sender(socket.getOutputStream());
            final Receiver receiver = new Receiver(socket.getInputStream());

            final Connection connection = new Connection(socket, sender, receiver);
            connections.put(String.format("%s:%d", address,port), connection);
            return connection;
        } catch (IOException e) {
            log.warn("Cannot connect to client {}:{}.", address, port);
        }
        return null;
    }

    public Connection addExistingConnection(final Socket socket) {
        try {
            final String address = socket.getInetAddress().getHostAddress();
            final int port = socket.getPort();

            final Sender sender = new Sender(socket.getOutputStream());
            final Receiver receiver = new Receiver(socket.getInputStream());
            final Connection connection = new Connection(socket, sender, receiver);
            connections.put(String.format("%s:%d", address,port), connection);
            return connection;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Optional<Connection> getConnection(String address, final int port) {
        return Optional.ofNullable(connections.get(String.format("%s:%d", address,port)));
    }

    public synchronized void closeConnection(String address, final int port) {
        final var key = String.format("%s:%d", address, port);
        final var connection = connections.get(key);
        connection.close();
        connections.remove(key);
    }

    public boolean completeConnectionParameters(final String address, final int port, final boolean isInitializer) {
        final var connection = connections.get(String.format("%s:%d", address,port));

        return isInitializer ? completeConnectionParametersAsInitializer(connection)
                : completeConnectionParametersAsReceiver(connection);
    }

    private boolean completeConnectionParametersAsInitializer(Connection connection) {
        final var publicKeyRequestMessage = new Message(
                MessageType.PUBLIC_KEY_REQUEST,
                loginService.getLoggedUser().getBytes(StandardCharsets.UTF_8),
                SerializationUtils.serialize(loginService.getKeyPair().getPublic()),
                -1,
                new byte[0]
        );
        connection.send(publicKeyRequestMessage);
        log.info("Public key has been sent.");

        final var publicKeyResponseMessage = connection.getMessage();
        if (publicKeyResponseMessage == null || publicKeyResponseMessage.getMessageType() != MessageType.PUBLIC_KEY_RESPONSE) {
            return false;
        }
        log.info("Public key has been received.");
        final var publicKey = (PublicKey) SerializationUtils.deserialize(publicKeyResponseMessage.getMessageContent());
        connection.setPublicKey(publicKey);

        try {
            final var sessionKey = SessionKeyUtils.generateSessionKey();
            final var sessionKeyMessage = new Message(
                    MessageType.SESSION_KEY,
                    loginService.getLoggedUser().getBytes(StandardCharsets.UTF_8),
                    SerializationUtils.serialize(sessionKey),
                    -1,
                    new byte[0]
            );
            if (sessionKeyMessage.getMessageContent().length > 245) {
                final var sessionKeyMessages = MessageUtils.splitMessage(sessionKeyMessage, 245);
                for (var message : sessionKeyMessages) {
                    connection.send(CipherUtils.encodeWithKey(message, loginService.getKeyPair().getPrivate(), "RSA"));
                }
            } else {
                connection.send(CipherUtils.encodeWithKey(sessionKeyMessage, loginService.getKeyPair().getPrivate(), "RSA"));
            }
            log.info("Session key has been send.");

            final var sessionKeyAccepted = connection.getMessage();
            if (sessionKeyAccepted == null || sessionKeyAccepted.getMessageType() != MessageType.SESSION_KEY_ACKNOWLEDGE) {
                return false;
            }
            log.info("Session key has been confirmed.");
            connection.setSessionKey(sessionKey);
        } catch (NoSuchAlgorithmException e) {
            log.warn("Cannot generate session key. Wrong algorithm.");
            return false;
        }

        return true;
    }

    private boolean completeConnectionParametersAsReceiver(Connection connection) {

        final var publicKeyMessage = connection.getMessage();
        if (publicKeyMessage == null || publicKeyMessage.getMessageType() != MessageType.PUBLIC_KEY_REQUEST) {
            log.warn("Don't receive public key from user");
            return false;
        }
        final var publicKey = (PublicKey) SerializationUtils.deserialize(publicKeyMessage.getMessageContent());
        connection.setPublicKey(publicKey);
        log.info("Public key has been received.");

        final var publicKeyResponseMessage = new Message(
                MessageType.PUBLIC_KEY_RESPONSE,
                loginService.getLoggedUser().getBytes(StandardCharsets.UTF_8),
                SerializationUtils.serialize(loginService.getKeyPair().getPublic()),
                -1,
                new byte[0]
        );
        connection.send(publicKeyResponseMessage);
        log.info("Public key has been send.");

        Message sessionKeyMessage;
        final var sessionKeyMessages = new ArrayList<Message>();
        do {
            sessionKeyMessage = connection.getMessage();
            if (sessionKeyMessage == null || sessionKeyMessage.getMessageType() != MessageType.SESSION_KEY) {
                log.warn("Don't receive session key from user");
                return false;
            }
            sessionKeyMessages.add(CipherUtils.decodeWithKey(sessionKeyMessage, publicKey, "RSA"));
            log.info("Part of session key has been received.");
        } while (sessionKeyMessage.getMessageNumber() != -1 && sessionKeyMessage.getMessageNumber() != -2);
        final var completedSessionKeyMessage = MessageUtils.concatMessage(sessionKeyMessages);
        final var sessionKey = (SessionKey) SerializationUtils.deserialize(completedSessionKeyMessage.getMessageContent());
        log.info("Session key has been received completely.");

        final var acknowledgmentMessage = new Message(
                MessageType.SESSION_KEY_ACKNOWLEDGE,
                loginService.getLoggedUser().getBytes(StandardCharsets.UTF_8),
                new byte[0],
                -1,
                new byte[0]
        );
        connection.send(acknowledgmentMessage);
        log.info("Session key confirmation has been received completely.");

        connection.setPublicKey(publicKey);
        connection.setSessionKey(sessionKey);
        return true;
    }
}
