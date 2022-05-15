package pl.edu.pg.eti.backend.message.handler;

import io.vavr.control.Either;

import java.io.File;
import java.util.function.BiConsumer;

public interface IncomingMessageHandler {
    void getMessage(BiConsumer<Either<String, File>, Double> callback);
}
