package controller;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;

import java.io.IOException;

class Home {

    private Node node;

    Home() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/home.fxml"));
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
