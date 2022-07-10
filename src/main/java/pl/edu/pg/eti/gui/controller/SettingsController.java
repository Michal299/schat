package pl.edu.pg.eti.gui.controller;

import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import pl.edu.pg.eti.ApplicationSettings;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ResourceBundle;

public class SettingsController implements Initializable {
    public Label cipherMode;
    public RadioButton ecbMode;
    public RadioButton cbcMode;
    public Button saveButton;
    public Label downloadLocation;
    public TextField downloadLocationPath;
    public Label blockSize;
    public TextField blockSizeValue;
    public Label publicKeyDir;
    public TextField publicKeyDirValue;
    public Label privateKeyDir;
    public TextField privateKeyDirValue;
    public Label publicKeyName;
    public TextField publicKeyNameValue;
    public Label privateKeyName;
    public TextField privateKeyNameValue;
    public Label status;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        final var toggleGroup = new ToggleGroup();
        ecbMode.setToggleGroup(toggleGroup);
        cbcMode.setToggleGroup(toggleGroup);

        final var settings = ApplicationSettings.getInstance();
        if (settings.getProperty("session.key.cipher.mode", "ECB").equals("ECB")) {
            ecbMode.setSelected(true);
        } else {
            cbcMode.setSelected(true);
        }

        downloadLocationPath.setText(settings.getProperty("send.file.location", System.getProperty("user.home")));
        blockSizeValue.setText(settings.getProperty("send.file.block.size", "1024"));
        publicKeyDirValue.setText(settings.getProperty("secure.public.key.dir", "keys/private"));
        privateKeyDirValue.setText(settings.getProperty("secure.private.key.dir", "keys/public"));
        publicKeyNameValue.setText(settings.getProperty("secure.public.key.name", "public_key"));
        privateKeyNameValue.setText(settings.getProperty("secure.private.key.name", "private_key"));
    }

    public void saveSettings(ActionEvent actionEvent) {

        // validate fields
        if (!Files.exists(Path.of(downloadLocationPath.getText()))) {
            status.setText("Wrong download location path. Directory doesn't exist");
            downloadLocation.setTextFill(Color.RED);
            return;
        }
        downloadLocation.setTextFill(Color.BLACK);

        int blockSizeValue = 0;
        try {
            blockSizeValue = Integer.parseInt(this.blockSizeValue.getText());
        } catch (NumberFormatException e) {
            status.setText("Wrong block size format. Please enter a valid integer value.");
            blockSize.setTextFill(Color.RED);
            return;
        }
        blockSize.setTextFill(Color.BLACK);

        String publicKeyPath = "";
        if (Files.exists(Paths.get(publicKeyDirValue.getText()))) {
            publicKeyPath = Paths.get(publicKeyDirValue.getText()).toAbsolutePath().toString();
        } else {
            status.setText("Wrong public key directory path.");
            publicKeyDir.setTextFill(Color.RED);
            return;
        }
        publicKeyDir.setTextFill(Color.BLACK);

        String privateKeyPath = "";
        if (Files.exists(Paths.get(privateKeyDirValue.getText()))) {
            privateKeyPath = privateKeyDirValue.getText();
        } else {
            status.setText("Wrong private key directory path.");
            privateKeyDir.setTextFill(Color.RED);
            return;
        }
        privateKeyDir.setTextFill(Color.BLACK);

        if (publicKeyNameValue.getText().isEmpty()) {
            status.setText("Please provide correct name for public key file");
            publicKeyName.setTextFill(Color.RED);
            return;
        }
        publicKeyName.setTextFill(Color.BLACK);

        if (privateKeyNameValue.getText().isEmpty()) {
            status.setText("Please provide correct name for private key file");
            privateKeyName.setTextFill(Color.RED);
            return;
        }
        privateKeyName.setTextFill(Color.BLACK);

        final var settings = ApplicationSettings.getInstance();
        settings.setProperty("send.file.location", downloadLocationPath.getText());
        settings.setProperty("send.file.block.size", "" + blockSizeValue);
        settings.setProperty("secure.public.key.dir", publicKeyPath);
        settings.setProperty("secure.private.key.dir", privateKeyPath);
        settings.setProperty("secure.public.key.name", publicKeyNameValue.getText());
        settings.setProperty("secure.private.key.name", privateKeyNameValue.getText());
        ApplicationSettings.flushSettings();
        status.setText("Settings saved");
        actionEvent.consume();
    }
}
