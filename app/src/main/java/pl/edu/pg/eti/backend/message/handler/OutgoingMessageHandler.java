package pl.edu.pg.eti.backend.message.handler;

import io.vavr.control.Either;

import java.io.File;
import java.util.function.BiConsumer;

public interface OutgoingMessageHandler {
    void sendMessage(Either<String, File> content, BiConsumer<String, Double> callback);
    void initializeConnection(BiConsumer<String, Double> callback);
}
