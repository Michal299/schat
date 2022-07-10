package pl.edu.pg.eti.backend.secure;

import pl.edu.pg.eti.backend.message.entity.Message;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;

public final class CipherUtils {

    private CipherUtils() {

    }

    public static Message encodeWithKey(Message message, Key key, String cipherTransformation, byte[] iv) {
        try {
            final var cipher = Cipher.getInstance(cipherTransformation);
            if (iv != null && Arrays.compare(new byte[0], iv) != 0) {
                cipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(iv));
            } else {
                cipher.init(Cipher.ENCRYPT_MODE, key);
            }

            final var encryptedAuthor = Base64.getEncoder().encode(cipher.doFinal(message.getAuthor()));
            final var encryptedContent = Base64.getEncoder().encode(cipher.doFinal(message.getMessageContent()));

            return new Message(
                    message.getMessageType(),
                    encryptedAuthor,
                    encryptedContent,
                    message.getMessageNumber(),
                    message.getIv()
            );
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException | InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static Message decodeWithKey(Message message, Key key, String cipherTransformation, byte[] iv) {
        try {
            final var cipher = Cipher.getInstance(cipherTransformation);
            if (iv != null && Arrays.compare(new byte[0], iv) != 0) {
                cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(iv));
            } else {
                cipher.init(Cipher.DECRYPT_MODE, key);
            }

            final var decryptedAuthor = cipher.doFinal(
                    Base64.getDecoder().decode(message.getAuthor())
            );
            final var decryptedContent = cipher.doFinal(
                    Base64.getDecoder().decode(message.getMessageContent())
            );

            return new Message(
                    message.getMessageType(),
                    decryptedAuthor,
                    decryptedContent,
                    message.getMessageNumber(),
                    message.getIv()
            );
        } catch (NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException | InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Message encodeWithKey(Message message, Key key, String cipherTransformation) {
        return encodeWithKey(message, key, cipherTransformation, null);
    }

    public static Message decodeWithKey(Message message, Key key, String cipherTransformation) {
        return decodeWithKey(message, key, cipherTransformation, null);
    }
}
