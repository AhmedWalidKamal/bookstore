package app;

import controller.MainController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import static javafx.application.Application.launch;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        primaryStage.getIcons().add(new Image(getClass().getResource("/app/icon.png").toExternalForm()));
        MainController.getInstance().init(primaryStage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
