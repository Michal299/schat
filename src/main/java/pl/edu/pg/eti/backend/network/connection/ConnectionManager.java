package pl.edu.pg.eti.backend.network.connection;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;

public class ConnectionManager implements Runnable {
    private final Socket clientSocket;

    private final IncomingConnectionHandler incomingConnection;
    private final OutgoingConnectionHandler outgoingConnection;

    private final Thread incomingThread;
    private final Thread outgoingThread;

    public ConnectionManager(final Socket clientSocket,
                             final BlockingQueue<String> incomingMessages,
                             final BlockingQueue<String> outgoingMessages) throws IOException {
        this.clientSocket = clientSocket;



        final ObjectInputStream inputStream = new ObjectInputStream(clientSocket.getInputStream());
        final ObjectOutputStream outputStream = new ObjectOutputStream(clientSocket.getOutputStream());
        outputStream.flush();

        this.incomingConnection = new IncomingConnectionHandler(inputStream, incomingMessages);
        this.outgoingConnection = new OutgoingConnectionHandler(outputStream, outgoingMessages);

        incomingThread = new Thread(incomingConnection);
        outgoingThread = new Thread(outgoingConnection);
    }

    @Override
    public void run() {
        incomingThread.start();
        outgoingThread.start();
    }

    public void closeConnection() {
        try {
            incomingConnection.stop();
            outgoingConnection.stop();
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        incomingThread.interrupt();
        outgoingThread.interrupt();
    }
}
