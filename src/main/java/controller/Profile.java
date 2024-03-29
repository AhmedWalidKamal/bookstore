package controller;

import com.jfoenix.controls.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import model.BookstoreUser;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.LinkedHashMap;

class Profile {

    @FXML
    private JFXTextField firstName, lastName, phoneNumber, shippingAddress;

    @FXML
    private ImageView userAvatar;

    @FXML
    private JFXPasswordField oldPassword, newPassword;

    @FXML
    private JFXDatePicker birthDate;

    @FXML
    private AnchorPane rootPane;

    @FXML
    private Label passwordErrorLabel, updateProfileErrorLabel;

    @FXML
    private JFXButton updateProfile, changePassword, upload;

    private Node node;

    Profile() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/profile.fxml"));
        fxmlLoader.setController(this);
        try {
            node = fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        init();
    }

    Node getNode() {
        return node;
    }

    private void init() {
        upload.setOnMouseClicked(mouseEvent -> handleUpdateAvatarButton());
        changePassword.setOnMouseClicked(mouseEvent -> handleChangePasswordAction());
        updateProfile.setOnMouseClicked(mouseEvent -> handleUpdateProfileAction());

        shippingAddress.setMinWidth(200);
        shippingAddress.prefColumnCountProperty().bind(shippingAddress.textProperty().length().add(1));
        initFields();
    }

    private void initFields() {
        if (MainController.getInstance()
                .getCurrentUser().getProfile().getFirstName() != null) {
            this.firstName.setText(MainController.getInstance()
                    .getCurrentUser().getProfile().getFirstName());
        }
        if (MainController.getInstance()
                .getCurrentUser().getProfile().getLastName() != null) {
            this.lastName.setText(MainController.getInstance()
                    .getCurrentUser().getProfile().getLastName());
        }
        LocalDate localDate = MainController.getInstance()
                .getCurrentUser().getProfile().getBirthDate();
        if (localDate != null) {
            System.out.println(localDate.toString());
            this.birthDate.setValue(localDate);
        }
        if (MainController.getInstance()
                .getCurrentUser().getProfile().getPhoneNumber() != null) {
            this.phoneNumber.setText(MainController.getInstance()
                    .getCurrentUser().getProfile().getPhoneNumber());
        }
        if (MainController.getInstance()
                .getCurrentUser().getProfile().getShippingAddress() != null) {
            this.shippingAddress.setText(MainController.getInstance()
                    .getCurrentUser().getProfile().getShippingAddress());
        }
        // Added the default user photo.
        Image image = new Image(getClass().getResource("/view/images/user/user-default-photo.png").toExternalForm());
        userAvatar.setImage(image);
        //TODO: upload the user photo from DB if there is one.
    }

    private void handleUpdateProfileAction() {
        if (validPhoneNumber()) {
            LinkedHashMap<String, String> colsValues = new LinkedHashMap<>();
            colsValues.put(BookstoreUser.UserProfile.FIRST_NAME_COLNAME, firstName.getText());
            colsValues.put(BookstoreUser.UserProfile.LAST_NAME_COLNAME, lastName.getText());
            colsValues.put(BookstoreUser.UserProfile.SHIPPING_ADDRESS_COLNAME, shippingAddress.getText());
            if (birthDate.getValue() == null) {
                colsValues.put(BookstoreUser.UserProfile.BIRTH_DATE_COLNAME, null);
            } else {
                colsValues.put(BookstoreUser.UserProfile.BIRTH_DATE_COLNAME, birthDate.getValue().toString());
            }
            colsValues.put(BookstoreUser.UserProfile.PHONE_NUMBER_COLNAME, phoneNumber.getText());
            try {
                MainController.getInstance().getBackendService().updateUser(
                        MainController.getInstance().getCurrentUser().getUserID(), colsValues);
                updateProfileErrorLabel.setVisible(false);
                JFXSnackbar bar = new JFXSnackbar(rootPane);
                bar.enqueue(new JFXSnackbar.SnackbarEvent("Profile Updated Successfully"));
            } catch (SQLException e) {
                e.printStackTrace();
                updateProfileErrorLabel.setText("Could not update profile!");
                updateProfileErrorLabel.setVisible(true);
            }
        } else {
            updateProfileErrorLabel.setText("Invalid Phone Number!");
            updateProfileErrorLabel.setVisible(true);
        }
    }

    private void handleChangePasswordAction() {
        if (oldPassword.getText().length() < 6 ) {
            passwordErrorLabel.setText("Old Password is too short!");
            passwordErrorLabel.setVisible(true);
        } else if (newPassword.getText().length() < 6) {
            passwordErrorLabel.setText("New Password is too short!");
            passwordErrorLabel.setVisible(true);
        } else {
            try {
                boolean success = MainController.getInstance().getBackendService().updatePassword(
                        MainController.getInstance().getCurrentUser().getUserID()
                        , oldPassword.getText().trim(), newPassword.getText().trim());
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

    private void handleUpdateAvatarButton() {

    }

    private void clearPasswordFields() {
        oldPassword.clear();
        newPassword.clear();
    }

    private boolean validPhoneNumber() {
        return phoneNumber.getText() == null || phoneNumber.getText().isEmpty() ||
                phoneNumber.getText().matches("[0-9]+") && phoneNumber.getText().length() == 11;
    }
}
