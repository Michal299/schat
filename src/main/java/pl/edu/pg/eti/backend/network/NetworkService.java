package pl.edu.pg.eti.backend.network;

import pl.edu.pg.eti.backend.network.connection.ConnectionManager;

import java.net.Socket;
import java.util.Map;
import java.util.Set;

public interface NetworkService {
    void createNewConnection(String address, int port);
    void closeConnection(String address);
    void addConnection(Socket connectionSocket);
    Map<String, ConnectionManager> getAllConnections();
    Set<String> getAllConnectionAddresses();
}
