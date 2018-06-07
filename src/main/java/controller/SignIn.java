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
    private AnchorPane rootPane;

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
            } else {
                // Successfuly sign in
                JFXSnackbar bar = new JFXSnackbar(rootPane);
                bar.enqueue(new JFXSnackbar.SnackbarEvent("Login Successful!"));
                clearInputFields();
                loginErrorLabel.setVisible(false);
                mainController.setCurrentUser(user);
                mainController.loadProfileScene(); // Should be changed to load home when home is ready.
            }
        } catch (SQLException e) {
//            e.printStackTrace();
            // Log error instead of printing to console
            clearInputFields();
            loginErrorLabel.setVisible(true);
        }
    }

    @FXML
    public void initialize() {
        initUserNameTextField();
        initPassTextField();
    }

    private void initUserNameTextField() {
        RequiredFieldValidator validator = new RequiredFieldValidator();
        validator.setMessage("Username Required");
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

    public void dispRegMessage() {
        JFXSnackbar bar = new JFXSnackbar(rootPane);
        bar.enqueue(new JFXSnackbar.SnackbarEvent("Registration Successful!"));
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
