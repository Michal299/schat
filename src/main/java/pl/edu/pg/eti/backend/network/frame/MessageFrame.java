package pl.edu.pg.eti.backend.network.frame;

import java.io.Serializable;

public class MessageFrame implements Serializable {
    private int frameNumber;
    private boolean isFile;
    private byte[] content;

    public MessageFrame(int frameNumber, boolean isFile, byte[] content) {
        this.frameNumber = frameNumber;
        this.isFile = isFile;
        this.content = content;
    }

    public MessageFrame() {
    }

    public int getFrameNumber() {
        return frameNumber;
    }

    public void setFrameNumber(int frameNumber) {
        this.frameNumber = frameNumber;
    }

    public boolean isFile() {
        return isFile;
    }

    public void setFile(boolean file) {
        isFile = file;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }
}
