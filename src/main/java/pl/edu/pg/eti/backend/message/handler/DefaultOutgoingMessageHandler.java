package pl.edu.pg.eti.backend.message.handler;

import io.vavr.control.Either;
import pl.edu.pg.eti.backend.connection.Connection;
import pl.edu.pg.eti.backend.message.entity.Message;
import pl.edu.pg.eti.backend.message.entity.MessageType;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.function.BiConsumer;

public class DefaultOutgoingMessageHandler implements OutgoingMessageHandler {
    private final Connection connection;

    public DefaultOutgoingMessageHandler(final Connection connection) {
        this.connection = connection;
    }

    @Override
    public void sendMessage(Either<String, File> content, BiConsumer<String, Double> callback) {
        if (content.isLeft()) {
            callback.accept("Encoding...", 0.0);
            final byte[] message = encode(content.left().get());
            final Message messageDto = new Message(MessageType.TEXT, message, -1);
            callback.accept("Sending...", 0.0);

            if (!connection.isClosed() && connection.send(messageDto)) {
                callback.accept("Sent", 100.0);
            }  else {
                callback.accept("Connection closed", 100.0);
            }

        } else {
            callback.accept("File not supported yet.", 100.0);
        }
    }

    @Override
    public void initializeConnection(BiConsumer<String, Double> callback) {

    }

    private byte[] encode(String message) {
        return message.getBytes(StandardCharsets.UTF_8);
    }
}
