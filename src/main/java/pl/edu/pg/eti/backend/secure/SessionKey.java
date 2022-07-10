package pl.edu.pg.eti.backend.secure;

import javax.crypto.SecretKey;
import java.io.Serializable;
import java.security.Key;

public class SessionKey implements Serializable {

    private final SecretKey key;
    private final String algorithmType;
    private final int blockSize;
    private final String cipherMode;

    public SessionKey(final SecretKey key, final String algorithmType, final int blockSize, final String cipherMode) {
        this.algorithmType = algorithmType;
        this.blockSize = blockSize;
        this.cipherMode = cipherMode;
        this.key = key;
    }

    public Key getKey() {
        return key;
    }

    public String getAlgorithmType() {
        return algorithmType;
    }

    public int getBlockSize() {
        return blockSize;
    }

    public String getCipherMode() {
        return cipherMode;
    }

}
