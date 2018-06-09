package controller;

import javafx.fxml.FXML;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.layout.AnchorPane;

public class BookOrders {

    @FXML
    private AnchorPane ordersRootPane;

    @FXML
    private TreeTableColumn<?, ?> orderNumberCol, isbnCol, publisherCol, quantityCol, confirmCol;

    @FXML
    public void initialize() {
        loadOrders();
    }

    private void loadOrders() {

    }
}
