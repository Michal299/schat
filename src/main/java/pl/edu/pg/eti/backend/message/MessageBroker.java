package pl.edu.pg.eti.backend.message;

import java.util.Optional;
import java.util.concurrent.BlockingQueue;

public interface MessageBroker {
    BlockingQueue<String> getReceivedMessagesQueue(String address);
    BlockingQueue<String> getMessagesToSendQueue(String address);
    void publishReceivedMessage(String address, String message);
    void publishMessageToSend(String address, String message);
}
