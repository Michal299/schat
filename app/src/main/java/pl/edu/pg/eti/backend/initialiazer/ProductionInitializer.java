package pl.edu.pg.eti.backend.initialiazer;

import pl.edu.pg.eti.ApplicationSettings;
import pl.edu.pg.eti.backend.connection.ConnectionService;
import pl.edu.pg.eti.backend.connection.Server;
import pl.edu.pg.eti.backend.container.BackendContainer;
import pl.edu.pg.eti.backend.contact.ContactsService;
import pl.edu.pg.eti.backend.contact.DefaultContactsService;
import pl.edu.pg.eti.backend.event.EventsBroker;

import java.io.IOException;

public class ProductionInitializer {

    /**
     * Initialize app by creating all necessary objects and threads.
     * @param container where objects and thread should be stored
     */
    public static void initialize(BackendContainer container) throws IOException {
        final ContactsService contactsRepository = new DefaultContactsService("contacts.json");
        container.register(ContactsService.class, contactsRepository);

        final ConnectionService connectionService = new ConnectionService();
        container.register(ConnectionService.class, connectionService);

        final EventsBroker eventsBroker = new EventsBroker();
        container.register(EventsBroker.class, eventsBroker);

        final Server server = new Server(
                Integer.parseInt(ApplicationSettings.getInstance().getProperty("port", "8080")),
                connectionService,
                eventsBroker);
        container.register(Server.class, server);
    }
}
