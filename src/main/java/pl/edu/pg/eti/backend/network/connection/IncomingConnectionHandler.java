package pl.edu.pg.eti.backend.network.connection;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.concurrent.BlockingQueue;

public class IncomingConnectionHandler implements Runnable {

    private final BlockingQueue<String> messagesQueue;
    private final ObjectInputStream in;
    private boolean isRunning;

    public IncomingConnectionHandler(final ObjectInputStream in, final BlockingQueue<String> messageQueue) {
        this. in = in;
        this.messagesQueue = messageQueue;
    }

    @Override
    public void run() {
        isRunning = true;
        while(isRunning) {
            String inputLine;
            try {
                while ((inputLine = in.readLine()) != null) {
                    messagesQueue.add(inputLine);
                }
            } catch (IOException e) {
                isRunning = false;
            }
        }
    }

    void stop() throws IOException {
        isRunning = false;
        in.close();
    }
}
