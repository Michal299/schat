package pl.edu.pg.eti.backend.connection;

import java.io.IOException;
import java.net.Socket;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

public class ConnectionService {

    final Map<String, Connection> connections;

    public ConnectionService() {
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
            e.printStackTrace();
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
}
