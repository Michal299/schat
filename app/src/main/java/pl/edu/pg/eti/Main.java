package pl.edu.pg.eti;

import javafx.application.Application;
import pl.edu.pg.eti.backend.connection.Server;
import pl.edu.pg.eti.backend.container.BackendContainer;
import pl.edu.pg.eti.backend.initialiazer.ProductionInitializer;
import pl.edu.pg.eti.gui.ChatApplication;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {

        ApplicationSettings.initialize("schat-application.properties");
        if (args.length > 0) {
            ApplicationSettings.getInstance().setProperty("port", args[0]);
        }

        ProductionInitializer.initialize(BackendContainer.getInstance());

        final var server = BackendContainer.getInstance().getComponent(Server.class);
        var serverThread = new Thread(server::run);
        serverThread.start();

        Application.launch(ChatApplication.class);
        ApplicationSettings.flushSettings();
    }
}
