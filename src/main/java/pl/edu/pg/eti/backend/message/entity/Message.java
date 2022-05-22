package pl.edu.pg.eti.backend.message.entity;

import java.io.Serializable;

public class Message implements Serializable {
    private final MessageType messageType;
    private final byte[] authorName;
    private final byte[] messageContent;
    private final int messageNumber;

    public Message(final MessageType messageType, final byte[] authorName, final byte[] messageContent, final int messageNumber) {
        this.authorName = authorName;
        this.messageContent = messageContent;
        this.messageNumber = messageNumber;
        this.messageType = messageType;
    }

    public MessageType getMessageType() {
        return messageType;
    }

    public byte[] getMessageContent() {
        return messageContent;
    }

    public int getMessageNumber() {
        return messageNumber;
    }

    @Override
    public String toString() {
        return "Message{" +
                "messageType=" + messageType +
                ", messageContent=" + new String(messageContent) +
                ", messageNumber=" + messageNumber +
                '}';
    }
}
