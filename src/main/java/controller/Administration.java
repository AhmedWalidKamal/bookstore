package controller;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;

import java.io.IOException;

class Administration {

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
