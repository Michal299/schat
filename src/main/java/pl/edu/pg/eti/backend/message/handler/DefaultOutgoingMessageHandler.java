package pl.edu.pg.eti.backend.message.handler;

import io.vavr.control.Either;
import org.apache.commons.lang3.tuple.Pair;
import pl.edu.pg.eti.ApplicationSettings;
import pl.edu.pg.eti.backend.connection.Connection;
import pl.edu.pg.eti.backend.message.entity.Message;
import pl.edu.pg.eti.backend.message.entity.MessageType;
import pl.edu.pg.eti.backend.secure.CipherUtils;
import pl.edu.pg.eti.backend.secure.SessionKeyUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;

public class DefaultOutgoingMessageHandler implements OutgoingMessageHandler {

    private final Worker worker;
    private final Thread workerThread;
    private BlockingQueue<Pair<Either<String, File>, BiConsumer<String, Double>>> messageQueue;

    public DefaultOutgoingMessageHandler(final Connection connection, final String loggedUser) {
        messageQueue = new LinkedBlockingQueue<>();
        worker = new Worker(messageQueue, connection, loggedUser);
        workerThread = new Thread(worker);
        workerThread.start();
        worker.start();
    }

    @Override
    public void sendMessage(Either<String, File> content, BiConsumer<String, Double> callback) {
        try {
            messageQueue.put(Pair.of(content, callback));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initializeConnection(BiConsumer<String, Double> callback) {

    }

    private static class Worker implements Runnable {

        private final BlockingQueue<Pair<Either<String, File>, BiConsumer<String, Double>>> messageQueue;
        private final Connection connection;
        private final String loggedUser;
        private boolean isRunning;

        Worker(BlockingQueue<Pair<Either<String, File>, BiConsumer<String, Double>>>  messageQueue, Connection connection, String loggedUser) {
            this.messageQueue = messageQueue;
            this.connection = connection;
            this.loggedUser = loggedUser;
        }

        @Override
        public void run() {
            while (isRunning) {
                try {
                    var pair = messageQueue.poll(100, TimeUnit.MILLISECONDS);
                    if (pair == null) continue;
                    sendMessage(pair.getLeft(), pair.getRight());
                } catch (InterruptedException ignored) {
                }
            }
        }

        void start() {
            isRunning = true;
        }

        void stop() {
            isRunning = false;
        }

        private byte[] encode(String message) {
            return message.getBytes(StandardCharsets.UTF_8);
        }

        private void sendMessage(Either<String, File> content, BiConsumer<String, Double> callback) {
            if (content.isLeft()) {
                callback.accept("Encoding...", 0.0);
                final byte[] message = encode(content.left().get());
                final byte[] iv = connection.getSessionKey().getCipherMode().equals("ECB") ?
                        new byte[0] : SessionKeyUtils.generateIv().getIV();

                final Message messageDto = new Message(
                        MessageType.TEXT,
                        loggedUser.getBytes(StandardCharsets.UTF_8),
                        message,
                        -1,
                        iv
                );
                final var sessionKey = connection.getSessionKey();
                final var encodedMessage = CipherUtils.encodeWithKey(
                        messageDto,
                        sessionKey.getKey(),
                        String.format("%s/%s/PKCS5Padding", sessionKey.getAlgorithmType(), sessionKey.getCipherMode()),
                        iv
                );
                callback.accept("Sending...", 0.0);

                if (!connection.isClosed() && connection.send(encodedMessage)) {
                    callback.accept("Sent", 100.0);
                }  else {
                    callback.accept("Connection closed", 100.0);
                }

            } else {
                sendFile(content.get(), callback);
            }
        }

        private void sendFile(File file, BiConsumer<String, Double> callback) {
            byte[] iv = connection.getSessionKey().getCipherMode().equals("ECB") ?
                    new byte[0] : SessionKeyUtils.generateIv().getIV();

            connection.send(CipherUtils.encodeWithKey(
                    new Message(
                            MessageType.FILE,
                            loggedUser.getBytes(StandardCharsets.UTF_8),
                            file.getName().getBytes(StandardCharsets.UTF_8),
                            -1,
                            iv
                    ),
                    connection.getSessionKey().getKey(),
                    String.format("%s/%s/PKCS5Padding", connection.getSessionKey().getAlgorithmType(), connection.getSessionKey().getCipherMode()),
                    iv
            ));

            final int blockSize = ApplicationSettings.getInstance().getProperty("send.file.block.size", 1024);
            final long messagesNumber = file.length() / blockSize + (file.length() % blockSize == 0 ? 0 : 1);

            callback.accept("Sending the file...", 0.0);
            double progressIncrement = 100.0 / messagesNumber;
            double currentProgress = 0.0;
            try {
                byte[] content = new byte[blockSize];
                final var inputStream = new FileInputStream(file);
                int messageIndex = 0;
                while (inputStream.read(content) != -1) {
                    iv = connection.getSessionKey().getCipherMode().equals("ECB") ?
                            new byte[0] : SessionKeyUtils.generateIv().getIV();
                    final var message = new Message(
                            MessageType.FILE,
                            loggedUser.getBytes(StandardCharsets.UTF_8),
                            content,
                            messagesNumber == messageIndex + 1 ? -2 : messageIndex,
                            iv
                    );
                    connection.send(CipherUtils.encodeWithKey(
                            message,
                            connection.getSessionKey().getKey(),
                            String.format("%s/%s/PKCS5Padding", connection.getSessionKey().getAlgorithmType(), connection.getSessionKey().getCipherMode()),
                            iv
                    ));
                    messageIndex++;
                    currentProgress += progressIncrement;
                    callback.accept("Sending the file...", currentProgress);
                }
            } catch (IOException e) {
                callback.accept("File load failed.", 100.0);
                return;
            }
            callback.accept("Sent", 100.0);
        }
    }
}
