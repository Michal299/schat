package pl.edu.pg.eti;

import javafx.application.Application;
import pl.edu.pg.eti.gui.ChatApplication;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {

        ApplicationSettings.initialize("schat-application.properties");
//        ProductionInitializer.initialize(BackendContainer.getInstance());
        Application.launch(ChatApplication.class);
        ApplicationSettings.flushSettings();
    }
}
