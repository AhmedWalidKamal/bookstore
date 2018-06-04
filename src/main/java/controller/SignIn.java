package controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.validation.RequiredFieldValidator;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import model.BookstoreUser;
import service.BackendServices;

import java.sql.SQLException;


public class SignIn {

    private MainController mainController;
    private BackendServices sys;

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
    private void handleSignUpButtonAction(ActionEvent event) {
        mainController.loadSignUpScene();
    }

    @FXML
    private void handleSignInButtonAction(ActionEvent event) {
        try {
            BookstoreUser user = sys.login(userNameTextField.getText().trim(), passwordField.getText());
            if (user == null) {
                clearInputFields();
                loginErrorLabel.setVisible(true);
                System.out.println("LOGIN FAILED");
            } else {
                // Successfuly registration
                clearInputFields();
                loginErrorLabel.setVisible(false);
                System.out.println("LOGIN SUCCESSFUL");
//                goToHome();
            }
        } catch (SQLException e) {
//            e.printStackTrace();
            // Log error instead of printing to console
            clearInputFields();
            loginErrorLabel.setVisible(true);
            System.out.println("LOGIN FAILED");
        }
    }

    @FXML
    public void initialize() {
        initUserNameTextField();
        initPassTextField();
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

    private void clearInputFields() {
        userNameTextField.clear();
        passwordField.clear();
    }

    public void setBackEndService(BackendServices sys) {
        this.sys = sys;
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }
}
