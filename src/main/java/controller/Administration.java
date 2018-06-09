package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;

class Administration {

    @FXML
    private AnchorPane rootPane;

    private AddBook addBookController;
    private BookOrders bookOrdersController;
    private Reports reportsController;
    private UserPromotion userPromotionController;
    private Node node;

    Administration() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/administration.fxml"));
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
