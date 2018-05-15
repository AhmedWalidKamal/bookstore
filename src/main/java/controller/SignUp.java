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

public class SignUp {
    @FXML
    private JFXButton signUpButton;

    @FXML
    private JFXButton signInButton;

    @FXML
    private void handleSignInButtonAction(ActionEvent event) {
        closeStage();
        loadMain();
    }

    private void closeStage() {
        ((Stage) signInButton.getScene().getWindow()).close();
    }

    private void loadMain() {
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
}
