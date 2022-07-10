package pl.edu.pg.eti.backend.message.handler;

import pl.edu.pg.eti.gui.control.Message;

import java.util.function.BiConsumer;

public interface IncomingMessageHandler {
    void getMessage(BiConsumer<Message, Double> callback);
}
