package pl.edu.pg.eti.backend.network;

import pl.edu.pg.eti.backend.message.MessageBroker;
import pl.edu.pg.eti.backend.network.connection.ConnectionManager;

import java.io.IOException;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.BlockingQueue;

public class DefaultNetworkService implements NetworkService {

    private final Map<String, ConnectionManager> connections;
    private final MessageBroker messageBroker;

    public DefaultNetworkService(final MessageBroker messageBroker) {
        connections = new HashMap<>();
        this.messageBroker = messageBroker;
    }

    @Override
    public void createNewConnection(String address, int port) {
        try (Socket clientSocket = new Socket(address, port)) {
            addConnection(clientSocket);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void closeConnection(String address) {
        if (connections.containsKey(address)) {
            connections.get(address).closeConnection();
            connections.remove(address);
        }
    }

    @Override
    public void addConnection(Socket connectionSocket) {
        try {
            final String hostAddress = connectionSocket.getInetAddress().getHostAddress();
            final BlockingQueue<String> outgoingMessages = messageBroker.getMessagesToSendQueue(hostAddress);
            final BlockingQueue<String> incomingMessages = messageBroker.getReceivedMessagesQueue(hostAddress);
            final ConnectionManager connectionManager = new ConnectionManager(connectionSocket, outgoingMessages, incomingMessages);
            new Thread(connectionManager).start();
            connections.put(connectionSocket.getInetAddress().getHostAddress(), connectionManager);
        } catch (IOException exception) {
            exception.printStackTrace();
            System.out.println("Connection with " + connectionSocket.getInetAddress().getHostAddress() + " cannot be made");
        }

    }

    @Override
    public Map<String, ConnectionManager> getAllConnections() {
        return connections;
    }

    @Override
    public Set<String> getAllConnectionAddresses() {
        return connections.keySet();
    }
}
