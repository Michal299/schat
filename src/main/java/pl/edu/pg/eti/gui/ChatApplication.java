package pl.edu.pg.eti.gui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.edu.pg.eti.ApplicationSettings;
import pl.edu.pg.eti.Main;

import java.io.IOException;
import java.util.Objects;


public class ChatApplication extends Application {

    private static final Logger logger = LoggerFactory.getLogger(ChatApplication.class);
    private static Stage stage;

    public ChatApplication() {
        super();
    }

    @Override
    public void start(Stage stage) throws Exception {
        ChatApplication.stage = stage;
        stage.setOnCloseRequest((event) -> {
            Platform.exit();
            Main.stop();
        });
        changeScene("/fxml/Login.fxml");
        stage.show();
    }

    public void changeScene(String fxmlFile) throws IOException {
        final Parent pane = FXMLLoader.load(Objects.requireNonNull(getClass().getResource(fxmlFile)));
        final Stage stage = ChatApplication.stage;
        stage.setScene(new Scene(pane, Color.TRANSPARENT));
        stage.setTitle("SChat " + ApplicationSettings.getInstance().getProperty("port", "8080"));
        logger.info("Stage scene set to {}", fxmlFile);
    }
}
