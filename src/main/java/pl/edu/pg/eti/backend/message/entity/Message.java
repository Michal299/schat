package pl.edu.pg.eti.backend.message.entity;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Objects;

public class Message implements Serializable {
    private final MessageType messageType;
    private final byte[] authorName;
    private final byte[] messageContent;
    private final byte[] iv;
    private final int messageNumber;

    public Message(final MessageType messageType, final byte[] authorName, final byte[] messageContent, final int messageNumber, final byte[] iv) {
        this.authorName = authorName;
        this.messageContent = messageContent;
        this.messageNumber = messageNumber;
        this.messageType = messageType;
        this.iv = iv;
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

    public byte[] getAuthor() {
        return authorName;
    }

    public byte[] getIv() {
        return iv;
    }

    @Override
    public String toString() {
        return "Message{" +
                "messageType=" + messageType +
                ", messageContent=" + new String(messageContent) +
                ", messageNumber=" + messageNumber +
                ", messageAuthor=" + new String(authorName) +
                ", iv=" + Arrays.toString(iv) +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Message)) return false;
        Message message = (Message) o;
        return messageNumber == message.messageNumber &&
                messageType == message.messageType &&
                Arrays.equals(authorName, message.authorName) &&
                Arrays.equals(messageContent, message.messageContent) &&
                Arrays.equals(iv, message.iv);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(messageType, messageNumber);
        result = 31 * result + Arrays.hashCode(authorName);
        result = 31 * result + Arrays.hashCode(messageContent);
        result = 31 * result + Arrays.hashCode(iv);
        return result;
    }
}
