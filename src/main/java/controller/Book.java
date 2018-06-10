package controller;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;

import java.io.IOException;

class Book {

    private Node node;

    Book () {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/book.fxml"));
        fxmlLoader.setController(this);
        try {
            node = fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        initFields();
    }

    Node getNode() {
        return node;
    }

    private void initFields() {

    }
}
