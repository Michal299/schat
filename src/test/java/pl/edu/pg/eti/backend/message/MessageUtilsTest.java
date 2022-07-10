package pl.edu.pg.eti.backend.message;

import org.junit.jupiter.api.Test;
import pl.edu.pg.eti.backend.message.entity.Message;
import pl.edu.pg.eti.backend.message.entity.MessageType;

import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

class MessageUtilsTest {

    @Test
    void testMessageSplit() {
        final Message sampleMessage = new Message(
                MessageType.TEXT,
                "John Doe".getBytes(StandardCharsets.UTF_8),
                "AAABBBCCCDD".getBytes(StandardCharsets.UTF_8),
                -1,
                new byte[0]
        );

        final var chunkedMessages = MessageUtils.splitMessage(sampleMessage, "A".getBytes(StandardCharsets.UTF_8).length * 3);
        assertThat(chunkedMessages, hasSize(4));
        for (var message : chunkedMessages) {
            System.out.println(message);
        }
    }

    @Test
    void testMessageConcat() {
        final var messageList = IntStream.range(0, 5)
            .mapToObj(i -> new Message(
                    MessageType.TEXT,
                    "John Doe".getBytes(StandardCharsets.UTF_8),
                    "A".getBytes(StandardCharsets.UTF_8),
                    i,
                    new byte[0]
            ))
            .collect(Collectors.toList());

        final Message concatenatedMessage = MessageUtils.concatMessage(messageList);
        assertThat(concatenatedMessage.getMessageContent().length, is("A".getBytes(StandardCharsets.UTF_8).length * 5));
        System.out.println(concatenatedMessage);
    }
}