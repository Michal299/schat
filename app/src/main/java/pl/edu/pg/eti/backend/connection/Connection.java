package pl.edu.pg.eti.backend.connection;

import pl.edu.pg.eti.backend.message.entity.Message;

import java.io.IOException;
import java.net.Socket;

public class Connection {
    private final Socket socket;
    private final Sender sender;
    private final Receiver receiver;
    private boolean isClosed;

    public Connection(final Socket socket, final Sender sender, final Receiver receiver) {
        this.socket = socket;
        this.sender = sender;
        this.receiver = receiver;
        isClosed = false;
    }

    public Socket getSocket() {
        return socket;
    }

    public Sender getSender() {
        return sender;
    }

    public Receiver getReceiver() {
        return receiver;
    }

    public String getAddress() {
        return socket.getInetAddress().getHostAddress() + ":" + socket.getPort();
    }

    public boolean isClosed() {
        return isClosed;
    }

    public boolean send(final Message message) {
        try {
            sender.send(message);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            close();
            return false;
        }
    }

    public Message getMessage() {
        try {
            return receiver.get();
        } catch (IOException e) {
            e.printStackTrace();
            close();
            return null;
        }
    }

    void close() {
        isClosed = true;
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
