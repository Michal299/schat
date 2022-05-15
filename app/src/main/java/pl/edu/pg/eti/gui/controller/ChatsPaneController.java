package pl.edu.pg.eti.gui.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.StackPane;
import pl.edu.pg.eti.backend.connection.ConnectionService;
import pl.edu.pg.eti.backend.container.BackendContainer;
import pl.edu.pg.eti.backend.event.ConnectionEvent;
import pl.edu.pg.eti.backend.event.Event;
import pl.edu.pg.eti.backend.event.EventType;
import pl.edu.pg.eti.backend.event.EventsBroker;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Consumer;

public class ChatsPaneController implements Initializable {

    @FXML
    public ListView<String> chatsList;
    public Button openButton;
    public Button closeButton;
    public AppController appController;

    private ConnectionService connectionService;

    private String selectedAddress;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        BackendContainer.getInstance().getComponent(EventsBroker.class).addListener(getNewConnectionListener(), EventType.NEW_CONNECTION);
        connectionService = BackendContainer.getInstance().getComponent(ConnectionService.class);
        chatsList.getSelectionModel().selectedItemProperty().addListener((observableValue, s, t1) -> {
            selectedAddress = observableValue.getValue();
        });
    }

    public Consumer<Event> getNewConnectionListener() {
        return event -> {
            if (event.getType().equals(EventType.NEW_CONNECTION)) {
                var connectionEvent = (ConnectionEvent) event;
                Platform.runLater(() -> {
                    chatsList.getItems().add(connectionEvent.getConnection().getAddress());
                });
            }
        };
    }

    public void handleOpenConnection(ActionEvent actionEvent) {

        if (selectedAddress == null) {
            actionEvent.consume();
            return;
        }

        try {
            final FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ConversationView.fxml"));
            final StackPane pane = loader.load();
            ConversationController conversationController = loader.getController();
            conversationController.initializeIncomingConversation(selectedAddress);
            appController.setMainPane(pane);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        actionEvent.consume();
    }

    public void injectMainAppController(AppController appController) {
        this.appController = appController;
    }

    public void handleCloseConnection(ActionEvent actionEvent) {
        if (selectedAddress == null) {
            actionEvent.consume();
            return;
        }

        connectionService.closeConnection(selectedAddress.split(":")[0],
                Integer.parseInt(selectedAddress.split(":")[1]));

        chatsList.getItems().remove(selectedAddress);
        selectedAddress = null;
    }
}
