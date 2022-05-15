package pl.edu.pg.eti.gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.edu.pg.eti.ApplicationSettings;

import java.net.URL;


public class ChatApplication extends Application {

    private static final Logger logger = LoggerFactory.getLogger(ChatApplication.class);

    public ChatApplication() {
        super();
    }
    @Override
    public void start(Stage stage) throws Exception {

        final URL mainView = getClass().getResource("/fxml/AppView.fxml");
        if (mainView == null) {
            logger.error("/fxml/AppView.fxml hasn't been found.");
            return;
        }

        final Parent root = FXMLLoader.load(mainView);
        stage.setScene(new Scene(root, Color.TRANSPARENT));
        stage.setTitle("SChat " + ApplicationSettings.getInstance().getProperty("port", "8080"));
        stage.show();

        logger.info("App has been started successfully");
    }

}
