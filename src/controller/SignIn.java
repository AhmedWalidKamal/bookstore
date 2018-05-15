package controller;

import com.jfoenix.controls.JFXButton;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class SignIn {

    @FXML
    private JFXButton signUpButton;

    @FXML
    private JFXButton signInButton;


    @FXML
    private void handleSignUpButtonAction(ActionEvent event) {
        closeStage();
        loadMain();
    }

    @FXML
    private void handleSignInButtonAction(ActionEvent event) {
    }

    private void closeStage() {
        ((Stage) signUpButton.getScene().getWindow()).close();
    }

    private void loadMain() {
        try {
            Parent parent = FXMLLoader.load(getClass().getResource("/view/signUp.fxml"));
            Stage stage = new Stage(StageStyle.DECORATED);
            stage.setTitle("Register");
            stage.setScene(new Scene(parent));
            stage.show();
        } catch (IOException ex) {
            // Log Exception
        }
    }
}
