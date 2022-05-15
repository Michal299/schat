package pl.edu.pg.eti.backend.message.handler;

public interface MessageHandlerManager {
    IncomingMessageHandler getIncomingMessageHandler(String address);
    OutgoingMessageHandler getOutgoingMessageHandler(String address);
}
