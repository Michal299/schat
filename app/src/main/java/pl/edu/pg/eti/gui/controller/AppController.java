package pl.edu.pg.eti.gui.controller;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

public class AppController {

    @FXML
    public Pane mainPane;

    @FXML
    public VBox controlPane;

    @FXML
    public ControlPaneController controlPaneController;

    @FXML
    public ChatsPaneController chatsPaneController;

    @FXML
    public VBox chatsPane;

    @FXML
    public void initialize() {
        controlPaneController.injectMainSceneController(this);
        chatsPaneController.injectMainAppController(this);
    }

    public void setMainPane(final Pane pane) {
        if (mainPane.getChildren().size() != 0) {
            final Node currentContent = mainPane.getChildren().get(0);
            mainPane.getChildren().remove(currentContent);
        }
        mainPane.getChildren().add(pane);

    }
}
