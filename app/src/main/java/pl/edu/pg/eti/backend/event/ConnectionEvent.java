package pl.edu.pg.eti.backend.event;

import pl.edu.pg.eti.backend.connection.Connection;

public class ConnectionEvent implements Event {

    private final Connection createdConnection;

    public ConnectionEvent(final Connection connection) {
        createdConnection = connection;
    }

    public Connection getConnection() {
        return createdConnection;
    }

    @Override
    public EventType getType() {
        return EventType.NEW_CONNECTION;
    }
}
