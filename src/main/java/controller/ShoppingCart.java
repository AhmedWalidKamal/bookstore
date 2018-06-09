package controller;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;

import java.io.IOException;

class ShoppingCart {

    private Node node;

    ShoppingCart() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/shoppingCart.fxml"));
        fxmlLoader.setController(this);
        try {
            node = fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    Node getNode() {
        return node;
    }
}
