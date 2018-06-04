package controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import service.BackendServices;


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
    private void handleSignUpButtonAction(ActionEvent event) {
        mainController.loadSignUpScene();
    }

    @FXML
    private void handleSignInButtonAction(ActionEvent event) {
    }

    public void setBackEndService(BackendServices sys) {
        this.sys = sys;
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }
}
