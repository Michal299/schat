package pl.edu.pg.eti.backend.message;

import com.google.common.primitives.Bytes;
import pl.edu.pg.eti.backend.message.entity.Message;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MessageUtils {

    public static List<Message> splitMessage(Message message, int maxSizeOfMessageContent) {
        final List<Message> messageList = new ArrayList<>();
        final int messagesTotalNumber = message.getMessageContent().length / maxSizeOfMessageContent +
                (message.getMessageContent().length % maxSizeOfMessageContent == 0 ? 0 : 1);

        for (int i = 0, messageNumber = 0; i < message.getMessageContent().length; i += maxSizeOfMessageContent, messageNumber++) {
            final var messageContent = Arrays.copyOfRange(
                message.getMessageContent(),
                i,
                Math.min(message.getMessageContent().length, i + maxSizeOfMessageContent)
            );
            messageList.add(new Message(
                    message.getMessageType(),
                    message.getAuthor(),
                    messageContent,
                    messageNumber == messagesTotalNumber - 1 ? -2 : messageNumber,
                    message.getIv()
            ));
        }
        return messageList;
    }

    public static Message concatMessage(List<Message> messageList) {
        final var messageContent = messageList.stream()
                .map(Message::getMessageContent)
                .reduce(new byte[0], Bytes::concat);

        final var firstMessage = messageList.get(0);
        return new Message(
                firstMessage.getMessageType(),
                firstMessage.getAuthor(),
                messageContent,
                -1,
                firstMessage.getIv()
        );
    }
}
