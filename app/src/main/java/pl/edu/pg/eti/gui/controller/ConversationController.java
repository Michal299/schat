package pl.edu.pg.eti.gui.controller;

import io.vavr.control.Either;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import pl.edu.pg.eti.backend.connection.ConnectionService;
import pl.edu.pg.eti.backend.contact.ContactsService;
import pl.edu.pg.eti.backend.container.BackendContainer;
import pl.edu.pg.eti.backend.message.handler.IncomingMessageHandler;
import pl.edu.pg.eti.backend.message.handler.MessageHandlerUtils;
import pl.edu.pg.eti.backend.message.handler.OutgoingMessageHandler;

import java.net.URL;
import java.util.ResourceBundle;

public class ConversationController implements Initializable {

    private final ContactsService contactsRepository = BackendContainer.getInstance().getComponent(ContactsService.class);

    private IncomingMessageHandler incomingMessageHandler;
    private OutgoingMessageHandler outgoingMessageHandler;

    public HBox selectContact;
    public ListView<String> messagesList;
    public TextArea messageContent;
    public VBox container;
    public ComboBox<String> contactsBox;
    public Label statusInfo;

    private String conversationAddress;
    private String conversationName;

    public void handleSendButtonClick(ActionEvent actionEvent) {
        if (conversationAddress == null) {
            statusInfo.setText("Please select contact");
            actionEvent.consume();
            return;
        }

        final String message = messageContent.getText();
        messageContent.setText("");
        outgoingMessageHandler.sendMessage(Either.left(message), (a, b) -> {
            statusInfo.setText(a);
            if ("Sent".equals(a)) {
                messagesList.getItems().add("(self) " + message);
            }
        });
        actionEvent.consume();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

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
        conversationName = contactsRepository.getContactByAddress(conversationAddress);
        initializeNewConversation();
    }
    
    void initializeIncomingConversation(final String address) {
        conversationAddress = address;
        container.getChildren().remove(selectContact);
        incomingMessageHandler = MessageHandlerUtils.getIncomingMessageHandler(address);
        outgoingMessageHandler = MessageHandlerUtils.getOutgoingMessageHandler(address);


        final Thread receiverThread = new Thread(() -> {
            while (true) {
                incomingMessageHandler.getMessage((a, b) -> {
                    if (a.isLeft()) {
                        Platform.runLater(() -> {
                            messagesList.getItems().add("(" + conversationAddress + ") " + a.getLeft());
                            messagesList.refresh();
                        });

                    }
                });
            }
        });
        receiverThread.start();
    }

    void initializeNewConversation() {
        BackendContainer.getInstance().getComponent(ConnectionService.class).createNewConnection(
                conversationAddress.split(":")[0],
                Integer.parseInt(conversationAddress.split(":")[1])
        );
        initializeIncomingConversation(conversationAddress);
    }


}
