package pl.edu.pg.eti.backend.connection;

import pl.edu.pg.eti.backend.message.entity.Message;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

public class Sender {
    final ObjectOutputStream outputStream;

    public Sender(final OutputStream outputStream) throws IOException {
        this.outputStream = new ObjectOutputStream(outputStream);
    }

    void send(final Message message) throws IOException {
        outputStream.writeObject(message);
    }
}
