package pl.edu.pg.eti.backend.connection;

import pl.edu.pg.eti.backend.event.ConnectionEvent;
import pl.edu.pg.eti.backend.event.EventsBroker;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    private final int port;
    private final ServerSocket serverSocket;
    private final ConnectionService connectionService;
    private boolean isRunning;
    private final EventsBroker eventsBroker;

    public Server(final int port,
                  final ConnectionService connectionService,
                  final EventsBroker eventsBroker) throws IOException {
        this.port = port;
        serverSocket = new ServerSocket(port);
        this.connectionService = connectionService;
        this.eventsBroker = eventsBroker;
    }
    public void run() {
        isRunning = true;
        try {
            while (isRunning) {
                Socket socket = serverSocket.accept();
                var connection = connectionService.addExistingConnection(socket);
                eventsBroker.publishEvent(new ConnectionEvent(connection));
            }
        }
        catch(IOException e) {
            System.out.println(e.getMessage());
            isRunning = false;
        }
    }

    public void stop() {
        try {
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
