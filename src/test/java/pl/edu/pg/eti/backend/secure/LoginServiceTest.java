package pl.edu.pg.eti.backend.secure;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.edu.pg.eti.ApplicationSettings;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class LoginServiceTest {

    private final String privateKeyDir = ".";
    private final String publicKeyDir = ".";
    private final String privateKeyFile = "private_key";
    private final String publicKeyFile = "public_key";

    @BeforeEach
    public void setUp() {
        ApplicationSettings.initialize("src/test/resources/test.properties");
        ApplicationSettings.getInstance().setProperty("secure.private.key.dir", privateKeyDir);
        ApplicationSettings.getInstance().setProperty("secure.public.key.dir", publicKeyDir);
        ApplicationSettings.getInstance().setProperty("secure.private.key.file", privateKeyFile);
        ApplicationSettings.getInstance().setProperty("secure.public.key.file", publicKeyFile);
    }

    @AfterEach
    public void tearDown() throws IOException {
        Files.deleteIfExists(Path.of(privateKeyDir + "/" + privateKeyFile));
        Files.deleteIfExists(Path.of(publicKeyDir + "/" + publicKeyFile));
    }

    @Test
    void testInitializeUser() {
        LoginService loginService = new LoginService();
        loginService.initializeUser("password");

        assertThat(Files.exists(Path.of(privateKeyDir + "/" + privateKeyFile)), is(true));
        assertThat(Files.exists(Path.of(publicKeyDir + "/" + publicKeyFile)), is(true));
    }

    @Test
    void testLoginUser() throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, NoSuchProviderException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        final LoginService loginService = new LoginService();
        final KeyPair keyPair = loginService.generateRsaKeys();
        final KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        final RSAPublicKeySpec publicKeySpec = keyFactory.getKeySpec(keyPair.getPublic(), RSAPublicKeySpec.class);
        final RSAPrivateKeySpec privateKeySpec = keyFactory.getKeySpec(keyPair.getPrivate(), RSAPrivateKeySpec.class);
        final PublicKey publicKey = keyFactory.generatePublic(new RSAPublicKeySpec(publicKeySpec.getModulus(), publicKeySpec.getPublicExponent()));
        final PrivateKey privateKey = keyFactory.generatePrivate(new RSAPrivateKeySpec(privateKeySpec.getModulus(), privateKeySpec.getPrivateExponent()));
        loginService.saveKeyPair(keyPair, "password");

        final boolean result = loginService.loginUser("name", "password");
        final KeyPair keyPairFromFile = loginService.getKeyPair();

        assertThat(result, is(true));
        assertThat(keyPairFromFile.getPublic(), is(publicKey));
        assertThat(keyPairFromFile.getPrivate(), is(privateKey));

        assertEncryptDecrypt(keyPairFromFile.getPublic(), privateKey, "some super important message");
        assertEncryptDecrypt(publicKey, keyPairFromFile.getPrivate(), "some super important message");

        assertEncryptDecrypt(keyPairFromFile.getPrivate(), publicKey, "some super important message");
        assertEncryptDecrypt(privateKey, keyPairFromFile.getPublic(), "some super important message");
    }

    private void assertEncryptDecrypt(final Key encryptKey, final Key decryptKey, final String message) throws NoSuchPaddingException, NoSuchAlgorithmException, NoSuchProviderException, InvalidKeySpecException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        final Cipher encryptCipher = Cipher.getInstance("RSA");
        encryptCipher.init(Cipher.ENCRYPT_MODE, encryptKey);

        final Cipher decryptCipher = Cipher.getInstance("RSA");
        decryptCipher.init(Cipher.DECRYPT_MODE, decryptKey);

        final byte[] decodedMessage = message.getBytes(StandardCharsets.UTF_8);
        final byte[] encryptedMessage = encryptCipher.doFinal(decodedMessage);
        final byte[] decryptedMessage = decryptCipher.doFinal(encryptedMessage);

        assertThat(decryptedMessage, is(decryptedMessage));
    }
}