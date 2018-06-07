package controller;

import com.jfoenix.controls.*;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import service.BackendServices;

import java.awt.event.ActionEvent;
import java.sql.SQLException;

public class Profile {

    @FXML
    private JFXTextField firstName, lastName, phoneNumber, shippingAddress;

    @FXML
    private JFXPasswordField oldPassword, newPassword;

    @FXML
    private JFXDatePicker birthdate;

    @FXML
    private AnchorPane rootPane;

    @FXML
    private Label passwordErrorLabel, updateProfileErrorLabel;

    private BackendServices sys;
    private MainController mainController;

    public void setBackEndService(BackendServices sys) {
        this.sys = sys;
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }


    private void clearPasswordFields() {
        oldPassword.clear();
        newPassword.clear();
    }

    @FXML
    private void handleUpdateProfileAction(javafx.event.ActionEvent actionEvent) {
    }

    @FXML
    private void handleChangePasswordAction(javafx.event.ActionEvent actionEvent) {
        if (oldPassword.getText().length() < 6 ) {
            passwordErrorLabel.setText("Old Password is too short!");
            passwordErrorLabel.setVisible(true);
        } else if (newPassword.getText().length() < 6) {
            passwordErrorLabel.setText("New Password is too short!");
            passwordErrorLabel.setVisible(true);
        } else {
            passwordErrorLabel.setVisible(false);
            try {
                sys.updatePassword(mainController.getCurrentUser().getUserName(), oldPassword.getText().trim(), newPassword.getText().trim());
                JFXSnackbar bar = new JFXSnackbar(rootPane);
                bar.enqueue(new JFXSnackbar.SnackbarEvent("Password Changed Successfully!"));
            } catch (SQLException e) {
                e.printStackTrace();
                passwordErrorLabel.setText("Old Password is invalid!");
                passwordErrorLabel.setVisible(true);
            }
        }
        clearPasswordFields();
    }
}
