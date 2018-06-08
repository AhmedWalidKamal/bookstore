package controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXSnackbar;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.validation.RequiredFieldValidator;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import model.BookstoreUser;

import java.io.IOException;
import java.sql.SQLException;

class SignIn {

    @FXML
    private JFXButton signUpButton;

    @FXML
    private JFXButton signInButton;

    @FXML
    private JFXTextField userNameTextField;

    @FXML
    private JFXPasswordField passwordField;

    @FXML
    private Label loginErrorLabel;

    @FXML
    private AnchorPane rootPane;

    private Parent parent;

    SignIn() {
        if (parent == null) {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/signIn.fxml"));
            fxmlLoader.setController(this);
            try {
                parent = fxmlLoader.load();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        initSignInButton();
        initSignUpButton();

        initUserNameTextField();
        initPassTextField();
    }

    Parent getParent() {
        return parent;
    }

    private void initSignInButton() {
        signInButton.setOnMouseClicked(mouseEvent -> handleSignInButtonAction());
    }

    private void initSignUpButton() {
        signUpButton.setOnMouseClicked(mouseEvent -> handleSignUpButtonAction());
    }

    private void initUserNameTextField() {
        RequiredFieldValidator validator = new RequiredFieldValidator();
        validator.setMessage("Username Required");
        userNameTextField.getValidators().add(validator);
        userNameTextField.focusedProperty().addListener((o, oldVal, newVal) -> {
            if (!newVal) userNameTextField.validate();
        });
    }

    private void initPassTextField() {
        RequiredFieldValidator validator = new RequiredFieldValidator();
        validator.setMessage("Password Required");
        passwordField.getValidators().add(validator);
        passwordField.focusedProperty().addListener((o, oldVal, newVal) -> {
            if (!newVal) passwordField.validate();
        });
    }

    private void handleSignInButtonAction() {
        try {
            BookstoreUser user = MainController.getInstance().getBackendService().login(
                                            userNameTextField.getText().trim(), passwordField.getText());
            if (user == null) {
                // Sign in failed.
                clearInputFields();
                loginErrorLabel.setVisible(true);
            } else {
                // Sign in succeeded.
                JFXSnackbar bar = new JFXSnackbar(rootPane);
                bar.enqueue(new JFXSnackbar.SnackbarEvent("Login Successful!"));
                clearInputFields();
                loginErrorLabel.setVisible(false);
                MainController.getInstance().setCurrentUser(user);
                MainController.getInstance().loadProfileScene(); // Should be changed to load home when home is ready.
            }
        } catch (SQLException e) {
            clearInputFields();
            loginErrorLabel.setVisible(true);
        }
    }

    private void handleSignUpButtonAction() {
        MainController.getInstance().loadSignUpScene();
    }

    void clearInputFields() {
        userNameTextField.clear();
        passwordField.clear();
    }

    void displayRegMessage() {
        JFXSnackbar bar = new JFXSnackbar(rootPane);
        bar.enqueue(new JFXSnackbar.SnackbarEvent("Registration Successful!"));
    }
}
