package controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXSnackbar;
import com.jfoenix.controls.JFXTextField;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

import java.sql.SQLException;

public class UserPromotion {

    @FXML
    private JFXButton promoteUserButton;

    @FXML
    private JFXTextField userNameTextField;

    @FXML
    private AnchorPane promoteUserRootPane;

    @FXML
    private Label failLabel;

    @FXML
    public void initialize() {
        initButtons();
    }

    private void initButtons() {

        promoteUserButton.setOnMouseClicked(mouseEvent -> promoteUser());

    }

    private void promoteUser() {
        System.out.println(userNameTextField.getText());
        if (!userNameTextField.getText().trim().isEmpty()) {
            try {
                boolean success = MainController.getInstance().getBackendService().
                        promote(userNameTextField.getText().trim(), "manager");
                if (success) {
                    JFXSnackbar bar = new JFXSnackbar(promoteUserRootPane);
                    bar.enqueue(new JFXSnackbar.SnackbarEvent("User Promoted Successfully"));
                    userNameTextField.clear();
                    failLabel.setVisible(false);
                } else {
                    failLabel.setText("Failed to promote user!");
                    failLabel.setVisible(true);
                }

            } catch (SQLException e) {
                e.printStackTrace();
                failLabel.setText("Failed to promote user!");
                failLabel.setVisible(true);
            }
        } else {
            failLabel.setText("User name is required!");
            failLabel.setVisible(true);
        }

    }

}