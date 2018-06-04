package controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.validation.RequiredFieldValidator;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import service.BackendServices;
import java.sql.SQLException;

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

    private BackendServices sys;
    private MainController mainController;

    @FXML
    private void handleSignUpButtonAction(ActionEvent event) {
        try {
            sys.register(userNameTextField.getText().trim(), emailTextField.getText().trim(),
                    passwordField.getText(), "customer");
            // Successfuly registration
            clearInputFields();
            goToSignIn();
        } catch (SQLException e) {
//            e.printStackTrace();
            // Log error instead of printing to console
            clearInputFields();
            registrationErrorLabel.setVisible(true);
            System.out.println("Registration error!");
        }
    }
    @FXML
    private void handleSignInButtonAction(ActionEvent event) {
        goToSignIn();
    }

    private void clearInputFields() {
        emailTextField.clear();
        userNameTextField.clear();
        passwordField.clear();
    }

    private void goToSignIn() {
        mainController.loadSignInScene();
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
