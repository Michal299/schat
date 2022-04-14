package pl.edu.pg.eti.gui.controller;

import javafx.fxml.FXML;
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
    public void initialize() {
        controlPaneController.injectMainSceneController(this);
    }

    public void setMainPane(final Pane pane) {
        mainPane.getChildren().add(pane);
    }
}
