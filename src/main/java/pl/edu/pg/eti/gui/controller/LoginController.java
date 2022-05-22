package pl.edu.pg.eti.gui.controller;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.edu.pg.eti.backend.container.BackendContainer;
import pl.edu.pg.eti.backend.secure.LoginService;
import pl.edu.pg.eti.gui.ChatApplication;

import java.io.IOException;

public class LoginController {
    public Button loginButton;
    public TextField loginField;
    public PasswordField passwordField;
    public Label statusLabel;

    private final Logger logger = LoggerFactory.getLogger(LoginController.class);
    private final LoginService loginService = BackendContainer.getInstance().getComponent(LoginService.class);
    public Button initButton;

    public void loginButton(ActionEvent actionEvent) {

        if (!validateFields()) {
            actionEvent.consume();
            return;
        }

        if (!loginService.loginUser(loginField.getText(), passwordField.getText())) {
            statusLabel.setText("Cannot log in. Check password or create new user");
            actionEvent.consume();
            return;
        }

        final String redirectView = "/fxml/AppView.fxml";
        try {
            new ChatApplication().changeScene(redirectView);
        } catch (IOException exception) {
            statusLabel.setText("Error: internal error");
            logger.error("Cannot load {} view.", redirectView);
        }

        actionEvent.consume();
    }

    private boolean validateFields() {
        if (loginField.getText().isEmpty()) {
            statusLabel.setText("Please provide login");
            return false;
        }

        if (passwordField.getText().isEmpty()) {
            statusLabel.setText("Please provide password");
            return false;
        }

        return true;
    }

    public void initButton(ActionEvent actionEvent) {
        if (!validateFields()) {
            statusLabel.setText("Please provide login and password");
        } else {
            loginService.initializeUser(passwordField.getText());
        }
        statusLabel.setText("User initialized");
        actionEvent.consume();
    }
}
