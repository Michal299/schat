package pl.edu.pg.eti.backend.secure;


import org.junit.jupiter.api.Test;
import pl.edu.pg.eti.backend.message.entity.Message;
import pl.edu.pg.eti.backend.message.entity.MessageType;

import javax.crypto.KeyGenerator;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
class CipherUtilsTest {

    @Test
    void testEncryptDecryptWithAsymmetricKey() throws NoSuchAlgorithmException {
        final KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(2048);
        KeyPair pair = generator.generateKeyPair();

        final var sampleMessage = new Message(
                MessageType.TEXT,
                "John Doe".getBytes(StandardCharsets.UTF_8),
                "Some fancy message".getBytes(StandardCharsets.UTF_8),
                -1,
                new byte[0]
        );

        final var encodedMessage = CipherUtils.encodeWithKey(sampleMessage, pair.getPrivate(), "RSA");
        assertThat(encodedMessage, notNullValue());

        final var decodedMessage = CipherUtils.decodeWithKey(encodedMessage, pair.getPublic(), "RSA");
        assertThat(decodedMessage, notNullValue());
        assertThat(decodedMessage, is(sampleMessage));
    }

    @Test
    void testEncryptDecryptWithSymmetricKeyInECBMode() throws NoSuchAlgorithmException {
        final KeyGenerator generator = KeyGenerator.getInstance("AES");
        Key key = generator.generateKey();

        final var sampleMessage = new Message(
                MessageType.TEXT,
                "John Doe".getBytes(StandardCharsets.UTF_8),
                "Some fancy message".getBytes(StandardCharsets.UTF_8),
                -1,
                new byte[0]
        );

        final var encodedMessage = CipherUtils.encodeWithKey(sampleMessage, key, "AES/ECB/PKCS5Padding");
        assertThat(encodedMessage, notNullValue());

        final var decodedMessage = CipherUtils.decodeWithKey(encodedMessage, key, "AES/ECB/PKCS5Padding");
        assertThat(decodedMessage, notNullValue());
        assertThat(decodedMessage, is(sampleMessage));
    }
}