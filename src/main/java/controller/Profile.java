package controller;

import com.jfoenix.controls.*;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

import javafx.event.ActionEvent;
import model.BookstoreUser;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.LinkedHashMap;

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

    private MainController mainController;


    private void clearPasswordFields() {
        oldPassword.clear();
        newPassword.clear();
    }

    @FXML
    private void handleUpdateProfileAction(ActionEvent actionEvent) {
        LinkedHashMap<String, String> colsValues = new LinkedHashMap<>();
        colsValues.put(BookstoreUser.UserProfile.FIRST_NAME_COLNAME, firstName.getText().trim());
        colsValues.put(BookstoreUser.UserProfile.LAST_NAME_COLNAME, lastName.getText().trim());
//        colsValues.put(this.mainController.getCurrentUser().getProfile().BIRTH_DATE_COLNAME, lastName.getText().trim());
        try {
            this.mainController.getBackendService().updateUser(this.mainController.getCurrentUser().getUserName(), colsValues);
            updateProfileErrorLabel.setVisible(false);
            JFXSnackbar bar = new JFXSnackbar(rootPane);
            bar.enqueue(new JFXSnackbar.SnackbarEvent("Profile Update Successfully"));
        } catch (SQLException e) {
            e.printStackTrace();
            updateProfileErrorLabel.setText("Could not update profile!");
            updateProfileErrorLabel.setVisible(true);
        }

    }

    @FXML
    private void handleChangePasswordAction(ActionEvent actionEvent) {
        if (oldPassword.getText().length() < 6 ) {
            passwordErrorLabel.setText("Old Password is too short!");
            passwordErrorLabel.setVisible(true);
        } else if (newPassword.getText().length() < 6) {
            passwordErrorLabel.setText("New Password is too short!");
            passwordErrorLabel.setVisible(true);
        } else {
            try {
                boolean success = this.mainController.getBackendService().updatePassword(mainController.getCurrentUser().
                        getUserName(), oldPassword.getText().trim(), newPassword.getText().trim());
                if (success) {
                    passwordErrorLabel.setVisible(false);
                    JFXSnackbar bar = new JFXSnackbar(rootPane);
                    bar.enqueue(new JFXSnackbar.SnackbarEvent("Password Changed Successfully!"));
                } else {
                    passwordErrorLabel.setText("Old Password is invalid!");
                    passwordErrorLabel.setVisible(true);
                }
            } catch (SQLException e) {
                e.printStackTrace();
                passwordErrorLabel.setText("Password change failed!");
                passwordErrorLabel.setVisible(true);
            }
        }
        clearPasswordFields();
    }

    public void initProfile(MainController mainController) {
        this.mainController = mainController;
        initFields();
    }

    private void initFields() {
        this.firstName.setText(this.mainController.getCurrentUser().getProfile().getFirstName());
        this.lastName.setText(this.mainController.getCurrentUser().getProfile().getLastName());
//        LocalDate date = this.mainController.getCurrentUser().getProfile().getBirthDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
//        this.birthdate.setValue(date);
        // Also set phone number and shipping address.
    }
}
