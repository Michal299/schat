package pl.edu.pg.eti.backend.message.handler;

import pl.edu.pg.eti.backend.connection.ConnectionService;
import pl.edu.pg.eti.backend.container.BackendContainer;

public final class MessageHandlerUtils {

    private MessageHandlerUtils() {
    }

    public static IncomingMessageHandler getIncomingMessageHandler(String address) {
        final var connectionService = BackendContainer.getInstance().getComponent(ConnectionService.class);
        final var connection = connectionService.getConnection(address.split(":")[0], Integer.parseInt(address.split(":")[1]));
        return connection.map(DefaultIncomingMessageHandler::new).orElse(null);
    }

    public static OutgoingMessageHandler getOutgoingMessageHandler(String address) {
        final var connectionService = BackendContainer.getInstance().getComponent(ConnectionService.class);
        final var connection = connectionService.getConnection(address.split(":")[0], Integer.parseInt(address.split(":")[1]));
        return connection.map(DefaultOutgoingMessageHandler::new).orElse(null);
    }
}
