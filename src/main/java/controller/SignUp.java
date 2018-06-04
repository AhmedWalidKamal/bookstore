package controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.validation.RequiredFieldValidator;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import service.BackendServices;
import java.io.IOException;
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

    @FXML
    private void handleSignUpButtonAction(ActionEvent event) {
        try {
            sys.register(userNameTextField.getText(), emailTextField.getText(),
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

    private void closeStage() {
        ((Stage) signInButton.getScene().getWindow()).close();
    }

    private void clearInputFields() {
        emailTextField.clear();
        userNameTextField.clear();
        passwordField.clear();
    }

    private void goToSignIn() {
        closeStage();
        loadNewStage();
    }
    private void loadNewStage() {
        try {
            Parent parent = FXMLLoader.load(getClass().getResource("/view/signIn.fxml"));
            Stage stage = new Stage(StageStyle.DECORATED);
            stage.setTitle("Sign In");
            stage.setScene(new Scene(parent));
            stage.show();
        } catch (IOException ex) {
            // Log Exception
        }
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
        try {
            sys = new BackendServices();
        } catch (SQLException e) {
            e.printStackTrace();
            // Log error
            System.out.println("Couldn't establish connection");
        }
        initEmailTextField();
        initUserNameTextField();
        initPassTextField();
    }

}
