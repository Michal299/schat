package pl.edu.pg.eti.backend.message.handler;

import io.vavr.control.Either;
import pl.edu.pg.eti.backend.connection.Connection;

import java.io.File;
import java.util.function.BiConsumer;

public class DefaultIncomingMessageHandler implements IncomingMessageHandler {

    private final Connection connection;

    public DefaultIncomingMessageHandler(final Connection connection) {
        this.connection = connection;
    }

    @Override
    public void getMessage(BiConsumer<Either<String, File>, Double> callback) {
        callback.accept(Either.left(new String(connection.getMessage().getMessageContent())), 100.0);
    }
}
