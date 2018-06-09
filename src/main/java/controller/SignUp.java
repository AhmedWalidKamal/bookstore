package controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.validation.RequiredFieldValidator;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class SignUp {

    @FXML
    private JFXButton signUpButton;

    @FXML
    private JFXButton signInButton;

    @FXML
    private JFXTextField emailTextField;

    @FXML
    private JFXTextField userNameTextField;

    @FXML
    private JFXPasswordField passwordField;

    @FXML
    private Label registrationErrorLabel;

    @FXML
    private AnchorPane rootPane;

    private Parent parent;

    private static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    SignUp() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/signUp.fxml"));
        fxmlLoader.setController(this);
        try {
            parent = fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        init();
    }

    Parent getParent() {
        return parent;
    }

    void clearInputFields() {
        emailTextField.clear();
        userNameTextField.clear();
        passwordField.clear();
    }

    private void init() {
        signUpButton.setOnMouseClicked(mouseEvent -> handleSignUpButtonAction());
        signInButton.setOnMouseClicked(mouseEvent -> handleSignInButtonAction());

        initEmailTextField();
        initUserNameTextField();
        initPassTextField();
    }

    private void handleSignUpButtonAction() {
        try {
            if (validateFields()) {
                MainController.getInstance().getBackendService().register(userNameTextField.getText().trim()
                                                                                    , emailTextField.getText().trim(),
                        passwordField.getText(), "customer");
                // Successfully registration
                registrationErrorLabel.setVisible(false);
                clearInputFields();
                goToSignIn(true);
            } else {
                passwordField.clear();
                registrationErrorLabel.setVisible(true);
            }
        } catch (SQLException e) {
            passwordField.clear();
            registrationErrorLabel.setText("Username already exists!");
            registrationErrorLabel.setVisible(true);
        }
    }

    private void handleSignInButtonAction() {
        clearInputFields();
        registrationErrorLabel.setVisible(false);
        goToSignIn(false);
    }

    private void goToSignIn(boolean displayRegistrationMessage) {
        MainController.getInstance().loadSignIn(displayRegistrationMessage);
    }

    private boolean validateFields() {
        if (!validateEmail()) {
            registrationErrorLabel.setText("Invalid Email!");
            return false;
        } else if (!validateUsername()) {
            registrationErrorLabel.setText("Invalid Username!");
            return false;
        } else if (!validatePassword()) {
            registrationErrorLabel.setText("Invalid Password!");
            return false;
        }
        return true;
    }

    private boolean validateUsername() {
        return !userNameTextField.getText().isEmpty();
    }

    private boolean validatePassword() {
        return passwordField.getText().length() >= 6;
    }

    private boolean validateEmail() {
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX .matcher(emailTextField.getText());
        return matcher.find();
    }

    private void initEmailTextField() {
        RequiredFieldValidator validator = new RequiredFieldValidator();
        validator.setMessage("Email Required");
        emailTextField.getValidators().add(validator);
        emailTextField.focusedProperty().addListener((o, oldVal, newVal) -> {
            if(!newVal) emailTextField.validate();
        });
        emailTextField.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ENTER) {
                userNameTextField.requestFocus();
            }
        });
    }

    private void initUserNameTextField() {
        RequiredFieldValidator validator = new RequiredFieldValidator();
        validator.setMessage("User Name Required");
        userNameTextField.getValidators().add(validator);
        userNameTextField.focusedProperty().addListener((o, oldVal, newVal) -> {
            if(!newVal) userNameTextField.validate();
        });
        userNameTextField.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ENTER) {
                passwordField.requestFocus();
            }
        });
    }

    private void initPassTextField() {
        RequiredFieldValidator validator = new RequiredFieldValidator();
        validator.setMessage("Password Required");
        passwordField.getValidators().add(validator);
        passwordField.focusedProperty().addListener((o, oldVal, newVal) -> {
            if(!newVal) passwordField.validate();
        });
        passwordField.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ENTER) {
                handleSignUpButtonAction();
            }
        });
    }
}
