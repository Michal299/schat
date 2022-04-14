package pl.edu.pg.eti.backend.network.connection;

import pl.edu.pg.eti.backend.network.frame.MessageFrame;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.BlockingQueue;

public class OutgoingConnectionHandler implements Runnable {
    private final ObjectOutputStream out;
    private final BlockingQueue<String> messagesQueue;
    private boolean isRunning;

    public OutgoingConnectionHandler(final ObjectOutputStream out, final BlockingQueue<String> messagesQueue) {
        this.out = out;
        this.messagesQueue = messagesQueue;
    }

    @Override
    public void run() {
        isRunning = true;
        while (isRunning) {
            final String message;
            try {
                message = messagesQueue.take();
                final MessageFrame frame = new MessageFrame();
                frame.setFrameNumber(-1);
                frame.setFile(false);
                frame.setContent(message.getBytes(StandardCharsets.UTF_8));
                out.writeObject(frame);
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    void stop() throws IOException {
        isRunning = false;
        out.close();
    }
}
