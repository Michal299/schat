package pl.edu.pg.eti.gui.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.StackPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class ControlPaneController {

    private final Logger log = LoggerFactory.getLogger(ControlPaneController.class);

    @FXML
    public AppController controller;

    public void injectMainSceneController(AppController controller) {
        this.controller = controller;
    }

    public void closeApp() {
        Platform.exit();
    }

    public void handleCloseButtonClick(ActionEvent actionEvent) {
        log.info("Close app button clicked");
        actionEvent.consume();
        closeApp();
    }

    public void handleShowContactsButtonClick(ActionEvent actionEvent) {
        log.info("Show contacts button clicked");
        loadView("/fxml/ContactsView.fxml");
        actionEvent.consume();
    }

    public void handleNewMessageButtonClick(ActionEvent actionEvent) {
        log.info("New message button clicked");
        loadView("/fxml/ConversationView.fxml");
        actionEvent.consume();
    }

    public void handleSettingsButtonClick(ActionEvent actionEvent) {
        log.info("Settings button clicked");
        loadView("/fxml/SettingsView.fxml");
        actionEvent.consume();
    }

    private void loadView(final String pathToView) {
        try {
            final FXMLLoader loader = new FXMLLoader(getClass().getResource(pathToView));
            final StackPane pane = loader.load();
            controller.setMainPane(pane);
        } catch (IOException e) {
            log.error("Cannot open view: {}. Error: ", pathToView, e);
            throw new RuntimeException(e);
        }
    }
}
