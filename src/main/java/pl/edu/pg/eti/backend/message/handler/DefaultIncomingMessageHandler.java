package pl.edu.pg.eti.backend.message.handler;

import pl.edu.pg.eti.ApplicationSettings;
import pl.edu.pg.eti.backend.connection.Connection;
import pl.edu.pg.eti.backend.message.MessageUtils;
import pl.edu.pg.eti.backend.message.entity.MessageType;
import pl.edu.pg.eti.backend.secure.CipherUtils;
import pl.edu.pg.eti.gui.control.Message;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

public class DefaultIncomingMessageHandler implements IncomingMessageHandler {

    private final Connection connection;

    public DefaultIncomingMessageHandler(final Connection connection) {
        this.connection = connection;
    }

    @Override
    public void getMessage(BiConsumer<Message, Double> callback) {
        final var sessionKey = connection.getSessionKey();

        pl.edu.pg.eti.backend.message.entity.Message message = connection.getMessage();
        var encodedMessage = CipherUtils.decodeWithKey(
            message,
            connection.getSessionKey().getKey(),
            String.format("%s/%s/PKCS5Padding", sessionKey.getAlgorithmType(), sessionKey.getCipherMode()),
            message.getIv()
        );

        if (message.getMessageType() == MessageType.FILE) {
            receiveFile(new String(encodedMessage.getMessageContent()), new String(encodedMessage.getAuthor()), callback);
            return;
        }

        callback.accept(new Message(
            new String(encodedMessage.getAuthor()),
            new String(encodedMessage.getMessageContent())
            ),
            100.0
        );
    }

    private File createFile(String fileName, String authorName) {
        int i = 0;
        final String filesLocation = ApplicationSettings.getInstance().getProperty("send.file.location", System.getProperty("user.home"));
        Path filePath = Path.of(filesLocation, authorName + fileName);
        while(Files.exists(filePath)) {
            filePath = Path.of(filesLocation, authorName + i + fileName);
        }
        try {
            Files.createFile(filePath);
        } catch (IOException ignored) {
        }

        return new File(filePath.toUri());
    }

    private void receiveFile2(String fileName, String author, BiConsumer<Message, Double> callback) {
        pl.edu.pg.eti.backend.message.entity.Message message;
        final var sessionKey = connection.getSessionKey();
        List<pl.edu.pg.eti.backend.message.entity.Message> messages = new ArrayList<>();
        do {
            message = connection.getMessage();
            messages.add(CipherUtils.decodeWithKey(
                    message,
                    connection.getSessionKey().getKey(),
                    String.format("%s/%s/PKCS5Padding", sessionKey.getAlgorithmType(), sessionKey.getCipherMode()),
                    message.getIv()
            ));
        } while (message.getMessageNumber() != -2);
        message = MessageUtils.concatMessage(messages);

        final var file = createFile(fileName, new String(message.getAuthor()));
        try {
            final var outputStream = new FileOutputStream(file);
            outputStream.write(message.getMessageContent());
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        callback.accept(new Message(
                    new String(message.getAuthor()),
                    file.getAbsolutePath()
            ),
            100.0
        );
    }

    private void receiveFile(String fileName, String author, BiConsumer<Message, Double> callback) {
        pl.edu.pg.eti.backend.message.entity.Message message;
        final var file = createFile(fileName, author);
        final var sessionKey = connection.getSessionKey();
        try (final var outputStream = new FileOutputStream(file)){
            do {
                message = connection.getMessage();
                message = CipherUtils.decodeWithKey(
                        message,
                        connection.getSessionKey().getKey(),
                        String.format("%s/%s/PKCS5Padding", sessionKey.getAlgorithmType(), sessionKey.getCipherMode()),
                        message.getIv()
                );
                outputStream.write(message.getMessageContent());
            } while (message.getMessageNumber() != -2);
            callback.accept(new Message(
                            new String(message.getAuthor()),
                            file
                    ),
                    100.0
            );
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
