package pl.edu.pg.eti.backend.initialiazer;

import pl.edu.pg.eti.backend.container.BackendContainer;
import pl.edu.pg.eti.backend.message.DefaultMessageBroker;
import pl.edu.pg.eti.backend.message.MessageBroker;
import pl.edu.pg.eti.backend.network.ConnectionListener;
import pl.edu.pg.eti.backend.network.DefaultNetworkService;
import pl.edu.pg.eti.backend.network.NetworkService;

import java.io.IOException;

public class ProductionInitializer {

    /**
     * Initialize app by creating all necessary objects and threads.
     * @param container where objects and thread should be stored
     */
    public static void initialize(BackendContainer container) throws IOException {
        final MessageBroker messageBroker = new DefaultMessageBroker();
        container.register(MessageBroker.class, messageBroker);

        final NetworkService networkService = new DefaultNetworkService(messageBroker);
        container.register(NetworkService.class, networkService);

        final ConnectionListener connectionListener = new ConnectionListener(6666, networkService);
        container.register(ConnectionListener.class, connectionListener);
    }
}
