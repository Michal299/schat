package pl.edu.pg.eti.backend.secure;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import pl.edu.pg.eti.ApplicationSettings;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

class SessionKeyUtilsTest {

    @Test
    void testEncryptDecryptWithGeneratedKeyInECBMode()
            throws NoSuchAlgorithmException, IllegalBlockSizeException, InvalidKeyException,
            BadPaddingException, InvalidAlgorithmParameterException, NoSuchPaddingException {

        ApplicationSettings.initialize("src/test/resources/test.properties");
        String input = "baeldung";
        SessionKey sessionKey = SessionKeyUtils.generateSessionKey();
        IvParameterSpec ivParameterSpec = SessionKeyUtils.generateIv();

        String algorithm = String.format("%s/%s/PKCS5Padding", sessionKey.getAlgorithmType(), sessionKey.getCipherMode());

        Cipher cipher = Cipher.getInstance(algorithm);
        if (sessionKey.getCipherMode().equals("ECB")) {
            cipher.init(Cipher.ENCRYPT_MODE, sessionKey.getKey());
        }
        else {
            cipher.init(Cipher.ENCRYPT_MODE, sessionKey.getKey(), ivParameterSpec);
        }
        String cipherText = Base64.getEncoder().encodeToString(cipher.doFinal(input.getBytes()));

        Cipher decipher = Cipher.getInstance(algorithm);
        if (sessionKey.getCipherMode().equals("ECB")) {
            decipher.init(Cipher.DECRYPT_MODE, sessionKey.getKey());
        }
        else {
            decipher.init(Cipher.DECRYPT_MODE, sessionKey.getKey(), ivParameterSpec);
        }
        String plainText = new String(decipher.doFinal(Base64.getDecoder().decode(cipherText)));
        Assertions.assertEquals(input, plainText);
    }

    @Test
    void testEncryptDecryptWithGeneratedKeyInCBCMode()
            throws NoSuchAlgorithmException, IllegalBlockSizeException, InvalidKeyException,
            BadPaddingException, InvalidAlgorithmParameterException, NoSuchPaddingException {

        ApplicationSettings.initialize("src/test/resources/test.properties");
        ApplicationSettings.getInstance().setProperty("session.key.cipher.mode", "CBC");
        String input = "baeldung";
        SessionKey sessionKey = SessionKeyUtils.generateSessionKey();
        IvParameterSpec ivParameterSpec = SessionKeyUtils.generateIv();

        String algorithm = String.format("%s/%s/PKCS5Padding", sessionKey.getAlgorithmType(), sessionKey.getCipherMode());

        Cipher cipher = Cipher.getInstance(algorithm);
        if (sessionKey.getCipherMode().equals("ECB")) {
            cipher.init(Cipher.ENCRYPT_MODE, sessionKey.getKey());
        }
        else {
            cipher.init(Cipher.ENCRYPT_MODE, sessionKey.getKey(), ivParameterSpec);
        }
        String cipherText = Base64.getEncoder().encodeToString(cipher.doFinal(input.getBytes()));

        Cipher decipher = Cipher.getInstance(algorithm);
        if (sessionKey.getCipherMode().equals("ECB")) {
            decipher.init(Cipher.DECRYPT_MODE, sessionKey.getKey());
        }
        else {
            decipher.init(Cipher.DECRYPT_MODE, sessionKey.getKey(), ivParameterSpec);
        }
        String plainText = new String(decipher.doFinal(Base64.getDecoder().decode(cipherText)));
        Assertions.assertEquals(input, plainText);
    }

}