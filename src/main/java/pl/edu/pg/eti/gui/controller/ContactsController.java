package pl.edu.pg.eti.gui.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.edu.pg.eti.backend.container.BackendContainer;
import pl.edu.pg.eti.backend.contact.ContactsService;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class ContactsController implements Initializable {

    private final Logger log = LoggerFactory.getLogger(ContactsController.class);
    private final ContactsService contactsRepository = (ContactsService) BackendContainer.getInstance()
            .getObject(ContactsService.class);

    @FXML
    public ListView<String> contactsList;
    public TextField nameInput;
    public TextField addressInput;
    public Label infoLabel;

    private String selectedContactName;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadContacts();

        contactsList.getSelectionModel().selectedItemProperty()
                .addListener((observableValue, s, t1) -> {
                    selectedContactName = observableValue.getValue();
                    infoLabel.setText(getContactDetails());
                });
    }

    public void addNewContact(ActionEvent actionEvent) {
        final String enteredName = nameInput.getText();
        final String enteredAddress = addressInput.getText();

        infoLabel.setText("");
        if (enteredName.isEmpty() || enteredAddress.isEmpty()) {
            infoLabel.setText("Please enter both name and address to add contact");
            return;
        }

        if (contactsRepository.hasContactWithName(enteredName)) {
            infoLabel.setText("Contact with given name already exist");
            return;
        }

        if (contactsRepository.hasContactWithAddress(enteredAddress)) {
            infoLabel.setText("Contact with given address already exist");
            return;
        }

        contactsRepository.addContact(enteredName, enteredAddress);
        loadContacts();
        actionEvent.consume();
    }

    public void deleteContact(ActionEvent actionEvent) {
        if (selectedContactName == null) {
            infoLabel.setText("Please select contact you want to delete first.");
            return;
        }
        final String contactName = selectedContactName;
        final String contactAddress = contactsRepository.getContactAddress(contactName);
        contactsRepository.removeContact(selectedContactName);

        infoLabel.setText(String.format("Contact (name: %s address: %s) has been removed.", contactName, contactAddress));
        loadContacts();
        actionEvent.consume();
    }

    private void loadContacts() {
        final List<String> contactsName = contactsRepository.getContacts();
        contactsList.getItems().removeAll(contactsList.getItems());
        contactsList.refresh();
        contactsList.getItems().addAll(contactsName);
    }

    private @NotNull String getContactDetails() {
        if (selectedContactName == null) {
            return "";
        }
        return selectedContactName +
                ": " +
                contactsRepository.getContactAddress(selectedContactName);
    }
}
