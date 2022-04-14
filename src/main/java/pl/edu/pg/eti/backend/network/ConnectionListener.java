package pl.edu.pg.eti.backend.network;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ConnectionListener implements Runnable {

    private final ServerSocket serverSocket;
    private final NetworkService networkManager;

    private boolean isRunning;

    public ConnectionListener(final int port, final NetworkService networkManager) throws IOException {
        serverSocket = new ServerSocket(port);
        this.networkManager = networkManager;
    }

    @Override
    public void run() {
        isRunning = true;
        while (isRunning) {
            try {
                final Socket incomingConnectionSocket = serverSocket.accept();
                networkManager.addConnection(incomingConnectionSocket);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void stopListener() throws IOException {
        isRunning = false;
        serverSocket.close();
    }
}
