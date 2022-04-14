package pl.edu.pg.eti.backend.message;

import java.util.Map;
import java.util.concurrent.*;

public class DefaultMessageBroker implements MessageBroker {
    private final Map<String, BlockingQueue<String>> incomingMessages;
    private final Map<String, BlockingQueue<String>> outgoingMessages;
    
    public DefaultMessageBroker() {
        incomingMessages = new ConcurrentHashMap<>();
        outgoingMessages = new ConcurrentHashMap<>();
    }

    @Override
    public BlockingQueue<String> getReceivedMessagesQueue(String address) {
        return getQueueForAddress(incomingMessages, address);
    }

    @Override
    public BlockingQueue<String> getMessagesToSendQueue(String address) {
        return getQueueForAddress(outgoingMessages, address);
    }

    @Override
    public void publishReceivedMessage(String address, String message) {
        publishMessage(address, message, incomingMessages);
    }

    @Override
    public void publishMessageToSend(final String address, final String message) {
        publishMessage(address, message, outgoingMessages);
    }

    private void publishMessage(final String address, final String message, final Map<String, BlockingQueue<String>> target) {
        if (!target.containsKey(address)) {
            target.put(address, new LinkedBlockingDeque<>());
        }

        target.get(address).add(message);
    }

    private BlockingQueue<String> getQueueForAddress(final Map<String, BlockingQueue<String>> source, final String address) {
        if (!source.containsKey(address)) {
            source.put(address, new LinkedBlockingQueue<>());
        }

        return source.get(address);
    }
}
