package pl.edu.pg.eti.backend.secure;

import pl.edu.pg.eti.ApplicationSettings;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public final class SessionKeyUtils {

    private static final String CIPHER = "AES";
    private static final int KEY_SIZE = ApplicationSettings.getInstance().getProperty("session.key.size", 128);

    private SessionKeyUtils() {

    }

    public static SessionKey generateSessionKey() throws NoSuchAlgorithmException {
        final KeyGenerator keyGenerator = KeyGenerator.getInstance(CIPHER);
        keyGenerator.init(KEY_SIZE);

        final SecretKey key = keyGenerator.generateKey();
        final var cipherMode = ApplicationSettings.getInstance().getProperty("session.key.cipher.mode", "ECB");
        return new SessionKey(key, CIPHER, 128, cipherMode);
    }

    public static IvParameterSpec generateIv() {
        final byte[] iv = new byte[16];
        new SecureRandom().nextBytes(iv);
        return new IvParameterSpec(iv);
    }
}
