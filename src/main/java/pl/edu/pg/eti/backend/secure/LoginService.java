package pl.edu.pg.eti.backend.secure;

import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.edu.pg.eti.ApplicationSettings;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import java.io.*;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;

public class LoginService {

    private String loggedUser;
    private KeyPair keyPair;

    private final static String ASYMMETRIC_ALGORITHM = "RSA";
    private final static String SYMMETRIC_ALGORITHM_TRANSFORMATION = "AES/CBC/PKCS5Padding";
    private final String privateKeyDir = ApplicationSettings.getInstance().getProperty("secure.private.key.dir", "keys/private");
    private final String publicKeyDir = ApplicationSettings.getInstance().getProperty("secure.public.key.dir", "keys/public");
    private final String publicKeyName = ApplicationSettings.getInstance().getProperty("secure.public.key.name", "public_key");
    private final String privateKeyName = ApplicationSettings.getInstance().getProperty("secure.private.key.name", "private_key");

    private final Logger log = LoggerFactory.getLogger(LoginService.class);

    public LoginService() {
        log.info("LoginService initialization");
    }

    public boolean loginUser(String name, String password) {
        final String publicKeyPath = Path.of(publicKeyDir, publicKeyName).toString();
        final String privateKeyPath = Path.of(privateKeyDir, privateKeyName).toString();
        final Pair<BigInteger, BigInteger> publicKeyValues = readKeyValuesFromFile(password, publicKeyPath);
        final Pair<BigInteger, BigInteger> privateKeyValues = readKeyValuesFromFile(password, privateKeyPath);

        if (publicKeyValues == null || privateKeyValues == null) {
            log.info("Cannot login user, user not initialized or wrong password");
            return false;
        }

        try {
            final KeyFactory keyFactory = KeyFactory.getInstance(ASYMMETRIC_ALGORITHM);
            final PublicKey publicKey = keyFactory.generatePublic(new RSAPublicKeySpec(publicKeyValues.getLeft(), publicKeyValues.getRight()));
            final PrivateKey privateKey = keyFactory.generatePrivate(new RSAPrivateKeySpec(privateKeyValues.getLeft(), privateKeyValues.getRight()));

            keyPair = new KeyPair(publicKey, privateKey);
            loggedUser = name;
            return true;
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            log.info("Cannot login user, corrupted files with keys.");
            return false;
        }
    }

    public void initializeUser(String password) {
        final KeyPair keyPair = generateRsaKeys();
        if (keyPair == null) {
            return;
        }
        saveKeyPair(keyPair, password);
    }

    public String getLoggedUser() {
        return loggedUser;
    }

    public KeyPair getKeyPair() {
        return keyPair;
    }

    KeyPair generateRsaKeys() {
        try {
            final int keySize = ApplicationSettings.getInstance().getProperty("secure.rsa.key.length", 2048);
            KeyPairGenerator keyGenerator = KeyPairGenerator.getInstance(ASYMMETRIC_ALGORITHM);
            keyGenerator.initialize(keySize);
            return keyGenerator.generateKeyPair();
        } catch (NoSuchAlgorithmException e) {
            log.error("Invalid algorithm for key generation.\n{}", e.getMessage());
        }
        return null;
    }

    void saveKeyPair(final KeyPair keyPair, final String password) {
        try {
            final KeyFactory keyFactory = KeyFactory.getInstance(ASYMMETRIC_ALGORITHM);
            final RSAPublicKeySpec publicKeySpec = keyFactory.getKeySpec(keyPair.getPublic(), RSAPublicKeySpec.class);
            final RSAPrivateKeySpec privateKeySpec = keyFactory.getKeySpec(keyPair.getPrivate(), RSAPrivateKeySpec.class);

            final Path publicKeyPath = Path.of(publicKeyDir, publicKeyName);
            final Path privateKeyPath = Path.of(privateKeyDir, privateKeyName);
            Files.deleteIfExists(publicKeyPath);
            Files.deleteIfExists(privateKeyPath);
            saveKeyToFile(password, publicKeyPath.toString(), publicKeySpec.getModulus(), publicKeySpec.getPublicExponent());
            saveKeyToFile(password, privateKeyPath.toString(), privateKeySpec.getModulus(), privateKeySpec.getPrivateExponent());

            log.info("{} keys save successfully.", ASYMMETRIC_ALGORITHM);
        } catch (NoSuchAlgorithmException e) {
            log.error("Invalid algorithm for key saving.\n{}", e.getMessage());
        } catch (InvalidKeySpecException e) {
            log.error("Error when trying to save key to file. Invalid key.", e);
        } catch (IOException ignored) {

        }
    }

    private Pair<BigInteger, BigInteger> readKeyValuesFromFile(final String password, final String path) {

        try {
            final Cipher cipher = Cipher.getInstance(SYMMETRIC_ALGORITHM_TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, PasswordUtil.getPasswordHash(password, "AES"), new IvParameterSpec(new byte[16]));

            try (final ObjectInputStream cipheredFileIn = new ObjectInputStream(
                    new CipherInputStream(new FileInputStream(path), cipher))
            ) {
                final BigInteger modulus = (BigInteger) cipheredFileIn.readObject();
                final BigInteger exponent = (BigInteger) cipheredFileIn.readObject();

                return Pair.of(modulus, exponent);
            } catch (FileNotFoundException ignore) {
            } catch (IOException e) {
                log.error("Issue when reading key from file", e);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

        } catch (NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeySpecException | InvalidKeyException | InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void saveKeyToFile(final String password, final String path, final BigInteger modulus, final BigInteger exponent) {
        try {
            final Cipher cipher = Cipher.getInstance(SYMMETRIC_ALGORITHM_TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, PasswordUtil.getPasswordHash(password, "AES"), new IvParameterSpec(new byte[16]));

            try (final ObjectOutputStream cipheredFileOut = new ObjectOutputStream(
                    new CipherOutputStream(new FileOutputStream(path), cipher))
            ) {
                cipheredFileOut.writeObject(modulus);
                cipheredFileOut.writeObject(exponent);
            } catch (IOException e) {
                log.error("Issue when saving key to file", e);
            }

        } catch (NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeySpecException | InvalidKeyException | InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }
    }
}
