package controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXSnackbar;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.validation.RequiredFieldValidator;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import service.BackendServices;
import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignUp {
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

    private BackendServices sys;
    private MainController mainController;

    private static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    @FXML
    private void handleSignUpButtonAction(ActionEvent event) {
        try {
            if (validateFields()) {
                sys.register(userNameTextField.getText().trim(), emailTextField.getText().trim(),
                        passwordField.getText(), "customer");
                // Successfuly registration
                registrationErrorLabel.setVisible(false);
                clearInputFields();
                goToSignIn(true);
            } else {
                passwordField.clear();
                registrationErrorLabel.setVisible(true);
            }
        } catch (SQLException e) {
//            e.printStackTrace();
            passwordField.clear();
            registrationErrorLabel.setText("Registration Error!");
            registrationErrorLabel.setVisible(true);
        }
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

    @FXML
    private void handleSignInButtonAction(ActionEvent event) {
        goToSignIn(false);
    }

    private void clearInputFields() {
        emailTextField.clear();
        userNameTextField.clear();
        passwordField.clear();
    }

    private void goToSignIn(boolean dispRegistrationMessage) {
        mainController.loadSignInScene(dispRegistrationMessage);
    }

    private void initEmailTextField() {
        RequiredFieldValidator validator = new RequiredFieldValidator();
        validator.setMessage("Email Required");
//            validator.setAwsomeIcon(new Icon(AwesomeIcon.WARNING,"2em",";","error"));
        emailTextField.getValidators().add(validator);
        emailTextField.focusedProperty().addListener((o,oldVal,newVal)->{
            if(!newVal) emailTextField.validate();
        });
    }

    private void initUserNameTextField() {
        RequiredFieldValidator validator = new RequiredFieldValidator();
        validator.setMessage("User Name Required");
//            validator.setAwsomeIcon(new Icon(AwesomeIcon.WARNING,"2em",";","error"));
        userNameTextField.getValidators().add(validator);
        userNameTextField.focusedProperty().addListener((o,oldVal,newVal)->{
            if(!newVal) userNameTextField.validate();
        });
    }

    private void initPassTextField() {
        RequiredFieldValidator validator = new RequiredFieldValidator();
        validator.setMessage("Password Required");
//        validator.setAwsomeIcon(new Icon(AwesomeIcon.WARNING,"2em",";","error"));
        passwordField.getValidators().add(validator);
        passwordField.focusedProperty().addListener((o,oldVal,newVal)->{
            if(!newVal) passwordField.validate();
        });
    }

    @FXML
    public void initialize() {
        initEmailTextField();
        initUserNameTextField();
        initPassTextField();
    }

    public void setBackEndService(BackendServices sys) {
        this.sys = sys;
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }
}
