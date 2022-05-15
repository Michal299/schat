package pl.edu.pg.eti.backend.connection;

import pl.edu.pg.eti.backend.message.entity.Message;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;

public class Receiver {

    final ObjectInputStream inputStream;

    public Receiver(final InputStream inputStream) throws IOException {
        this.inputStream = new ObjectInputStream(inputStream);
    }

     Message get() throws IOException {
        try {
            final Message message = (Message) inputStream.readObject();
            return message;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}
