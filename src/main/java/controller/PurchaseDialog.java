package controller;

import com.gluonhq.charm.glisten.control.Icon;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.validation.RequiredFieldValidator;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;

import java.io.IOException;
import java.sql.SQLException;
import java.util.LinkedHashMap;

class PurchaseDialog {

    @FXML
    private JFXButton confirmButton;

    @FXML
    private JFXButton cancelButton;

    @FXML
    private JFXTextField creditCardNumberField;

    @FXML
    private JFXTextField expirationDateField;

    @FXML
    private JFXTextField shippingAddressField;

    private JFXDialog dialog;

    private AnchorPane dialogContentRootPane;

    private ShoppingCart parentController;

    PurchaseDialog(StackPane rootPane, ShoppingCart parent) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/purchaseDialog.fxml"));
        fxmlLoader.setController(this);
        try {
            dialogContentRootPane = fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        shippingAddressField.setText(MainController.getInstance().getCurrentUser().getProfile().getShippingAddress());

        dialog = new JFXDialog(rootPane, dialogContentRootPane, JFXDialog.DialogTransition.TOP);

        init();
        this.parentController = parent;
    }

    void show() {
        dialog.show();
    }

    private void init() {
        confirmButton.setOnMouseClicked(mouseEvent -> handleConfirmButtonAction());
        cancelButton.setOnMouseClicked(mouseEvent -> handleCancelButtonAction());

        initCreditCardNumberField();
        initExpirationDateField();
        initShippingAddressField();
    }

    private void handleConfirmButtonAction() {
        LinkedHashMap<String, Integer> booksPurchased
                = MainController.getInstance().getCurrentUser().getCart().getCart();
        try {
            if (MainController.getInstance().getBackendService().buyBooks(
                    MainController.getInstance().getCurrentUser().getUserID(),
                    booksPurchased)) {
                parentController.clear();
                parentController.showMessage("Purchased Successfully");
            } else {
                parentController.showError("Failed to buy books");
            }
        } catch (SQLException e) {
            parentController.showError("Failed to buy books");
        }

        close();
    }

    private void handleCancelButtonAction() {
        close();
    }

    private void initCreditCardNumberField() {
        RequiredFieldValidator validator = new RequiredFieldValidator();
        validator.setMessage("Credit Card Number Required");
        creditCardNumberField.getValidators().add(validator);
        creditCardNumberField.focusedProperty().addListener((o, oldVal, newVal) -> {
            if (!newVal) creditCardNumberField.validate();
        });
    }

    private void initExpirationDateField() {
        RequiredFieldValidator validator = new RequiredFieldValidator();
        validator.setMessage("Expiration Date Required");
        expirationDateField.getValidators().add(validator);
        expirationDateField.focusedProperty().addListener((o, oldVal, newVal) -> {
            if (!newVal) expirationDateField.validate();
        });
    }

    private void initShippingAddressField() {
        RequiredFieldValidator validator = new RequiredFieldValidator();
        validator.setMessage("Shipping Address Required");
        shippingAddressField.getValidators().add(validator);
        shippingAddressField.focusedProperty().addListener((o, oldVal, newVal) -> {
            if (!newVal) shippingAddressField.validate();
        });
    }

    private void close() {
        dialog.close();
    }

    // To be used on clearing the user session data.
    private void clearInputFields() {
        creditCardNumberField.clear();
        expirationDateField.clear();
        shippingAddressField.clear();
    }
}