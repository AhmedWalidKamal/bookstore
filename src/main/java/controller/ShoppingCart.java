package controller;

import com.gluonhq.charm.glisten.control.CardPane;
import com.jfoenix.controls.JFXButton;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

import java.io.IOException;

class ShoppingCart {

    @FXML
    private Label numberOfItems;

    @FXML
    private Label totalPriceLabel;

    @FXML
    private JFXButton purchaseButton;

    @FXML
    private CardPane cartItemCardPane;

    private StackPane rootPane;

    private PurchaseDialog purchaseDialog;

    private Node node;

    ShoppingCart(StackPane rootPane) {
        this.rootPane = rootPane;
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/shoppingCart.fxml"));
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
        purchaseButton.setOnMouseClicked(mouseEvent -> handlePurchaseButtonAction());
    }

    private void handlePurchaseButtonAction() {
        if (purchaseDialog == null) {
            purchaseDialog = new PurchaseDialog(rootPane);
        }
        purchaseDialog.show();
    }
}
