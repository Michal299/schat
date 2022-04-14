package pl.edu.pg.eti.gui.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class ControlPaneController {

    @FXML
    public AppController controller;

    @FXML
    public void initialize() {
    }

    public void injectMainSceneController(AppController controller) {
        this.controller = controller;
    }

    public void handleCloseButtonClick(ActionEvent actionEvent) {
        Platform.exit();
        actionEvent.consume();
    }

    public void handleShowContactsButtonClick(ActionEvent actionEvent) {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ContactsView.fxml"));
        try {
            StackPane pane = loader.load();
            controller.setMainPane(pane);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        actionEvent.consume();
    }
}
