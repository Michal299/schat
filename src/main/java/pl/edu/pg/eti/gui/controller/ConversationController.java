package pl.edu.pg.eti.gui.controller;

import io.vavr.control.Either;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import pl.edu.pg.eti.backend.connection.ConnectionService;
import pl.edu.pg.eti.backend.contact.ContactsService;
import pl.edu.pg.eti.backend.container.BackendContainer;
import pl.edu.pg.eti.backend.message.handler.IncomingMessageHandler;
import pl.edu.pg.eti.backend.message.handler.MessageHandlerUtils;
import pl.edu.pg.eti.backend.message.handler.OutgoingMessageHandler;
import pl.edu.pg.eti.backend.secure.LoginService;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class ConversationController implements Initializable {

    private final ContactsService contactsRepository = BackendContainer.getInstance().getComponent(ContactsService.class);
    private final LoginService loginService = BackendContainer.getInstance().getComponent(LoginService.class);
    private final ConnectionService connectionService = BackendContainer.getInstance().getComponent(ConnectionService.class);
    public Label chosenFile;
    public ProgressBar sendingProgress;
    public Label sendingStatus;
    private IncomingMessageHandler incomingMessageHandler;
    private OutgoingMessageHandler outgoingMessageHandler;

    public HBox selectContact;
    public ListView<String> messagesList;
    public TextArea messageContent;
    public VBox container;
    public ComboBox<String> contactsBox;
    public Label statusInfo;

    private String conversationAddress;
    private boolean connectionCompleted = false;
    private FileChooser fileChooser;

    public void handleSendButtonClick(ActionEvent actionEvent) {
        if (conversationAddress == null) {
            statusInfo.setText("Please select contact");
            actionEvent.consume();
            return;
        }

        if (!chosenFile.getText().equals("")) {
            sendFile();
        } else {
            sendTextMessage();
        }

        actionEvent.consume();
    }

    private void sendTextMessage() {
        final String message = messageContent.getText();
        messageContent.setText("");
        outgoingMessageHandler.sendMessage(Either.left(message), (a, b) -> {
            Platform.runLater(() -> {
                sendingStatus.setText(a);
                sendingProgress.setProgress(b/100.0);
                if ("Sent".equals(a)) {
                    messagesList.getItems().add("(self | text)\n" + message);
                }
            });
        });
    }

    private void sendFile() {
        final String filePath = chosenFile.getText();
        messageContent.setDisable(false);
        chosenFile.setText("");
        final File file = new File(filePath);
        outgoingMessageHandler.sendMessage(Either.right(file), (a, b) -> {
            Platform.runLater(() -> {
                sendingStatus.setText(a);
                sendingProgress.setProgress(b/100.0);
                if ("Sent".equals(a)) {
                    messagesList.getItems().add("(self | file)\n" + filePath);
                }
            });
        });
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        fileChooser = new FileChooser();
        sendingProgress.setProgress(0.0);
        contactsBox.getItems().addAll(contactsRepository.getContacts());
    }

    public void handleAcceptContactButton(ActionEvent actionEvent) {
        var selected = contactsBox.getSelectionModel().getSelectedItem();
        if (selected == null) {
            statusInfo.setText("Please select contact");
            actionEvent.consume();
            return;
        }

        conversationAddress = contactsRepository.getContactAddress(selected);
        initializeNewConversation();
    }
    
    void initializeIncomingConversation(final String address) {
        if (!connectionCompleted) {
            if (!connectionService.completeConnectionParameters(address.split(":")[0], Integer.parseInt(address.split(":")[1]), false)) {
                return;
            }
        }

        conversationAddress = address;
        container.getChildren().remove(selectContact);
        incomingMessageHandler = MessageHandlerUtils.getIncomingMessageHandler(address);
        outgoingMessageHandler = MessageHandlerUtils.getOutgoingMessageHandler(address, loginService.getLoggedUser());


        final Thread receiverThread = new Thread(() -> {
            while (true) {
                incomingMessageHandler.getMessage((message, progress) -> {
                    Platform.runLater(() -> {
                        final var isFile = message.getContent().isRight();

                        final var header = isFile ? "file" : "text";
                        final var messageText = isFile ? message.getContent().get().getAbsolutePath() : message.getContent().getLeft();
                        messagesList.getItems().add(String.format("(%s -> %s | %s)\n%s",
                                conversationAddress,
                                message.getAuthor(),
                                header,
                                messageText
                        ));
                        messagesList.refresh();
                    });
                });
            }
        });
        receiverThread.start();
        statusInfo.setText("Connected");
    }

    void initializeNewConversation() {
        statusInfo.setText("Connecting...");
        var connection = connectionService.createNewConnection(
                conversationAddress.split(":")[0],
                Integer.parseInt(conversationAddress.split(":")[1])
        );
        if (connection == null) {
            statusInfo.setText("Given contact is currently unavailable.");
            return;
        }
        var connectionCompleted = connectionService.completeConnectionParameters(
                conversationAddress.split(":")[0],
                Integer.parseInt(conversationAddress.split(":")[1]),
                true);
        this.connectionCompleted = connectionCompleted;
        if (!connectionCompleted) {
            statusInfo.setText("Connection interrupted. Cannot connect with user.");
            return;
        }
        initializeIncomingConversation(conversationAddress);
    }


    public void selectFileToSend(ActionEvent actionEvent) {
        final var selectedFile = fileChooser.showOpenDialog((Stage)((Node) actionEvent.getSource()).getScene().getWindow());
        if (selectedFile != null) {
            messageContent.setDisable(true);
            chosenFile.setText(selectedFile.getAbsolutePath());
        }
        actionEvent.consume();
    }

    public void removeChosenFile(ActionEvent actionEvent) {
        messageContent.setDisable(false);
        chosenFile.setText("");
        actionEvent.consume();
    }
}
