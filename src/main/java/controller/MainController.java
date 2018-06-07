package controller;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import model.BookstoreUser;
import service.BackendServices;
import javafx.geometry.Rectangle2D;

import java.io.IOException;
import java.sql.SQLException;

public class MainController {

    private Stage primaryStage;
    private BackendServices sys;
    private BookstoreUser currentUser;

    public MainController(Stage primaryStage) throws IOException{
        this.primaryStage = primaryStage;
        this.currentUser = null;
        try {
            // Establish connection with database
            sys = new BackendServices();

            loadSignInScene();
        } catch (SQLException e) {
            e.printStackTrace();
            // Log error
            System.out.println("Couldn't establish connection");
        }
    }

    private void initShoppingCartController(ShoppingCart shoppingCart) {
        shoppingCart.setBackEndService(sys);
        shoppingCart.setMainController(this);
    }

    private void initHomeController(Home home) {
        home.setBackEndService(sys);
        home.setMainController(this);
    }

    private void initProfileController(Profile profile) {
        profile.setBackEndService(sys);
        profile.setMainController(this);
    }

    private void initSignUpController(SignUp signUp) {
        signUp.setBackEndService(sys);
        signUp.setMainController(this);
    }

    private void initSignInController(SignIn signIn) {
        signIn.setBackEndService(sys);
        signIn.setMainController(this);
    }

    public void loadSignInScene() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/signIn.fxml"));
            Scene scene = new Scene(loader.load());
            scene.getStylesheets().add(getClass().getResource("/css/signIn.css").toExternalForm());
            primaryStage.setScene(scene);
            primaryStage.setTitle("Library Bookstores!");
//            primaryStage.setMaximized(true);
            Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();
            System.out.println(primaryScreenBounds.getWidth());
            System.out.println(primaryScreenBounds.getHeight());
            primaryStage.show();
            initSignInController(loader.getController());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void loadSignUpScene() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/signUp.fxml"));
            primaryStage.setScene(new Scene(loader.load()));
            primaryStage.setTitle("Register");
            primaryStage.show();
            initSignUpController(loader.getController());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadProfileScene() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/profile.fxml"));
            primaryStage.setScene(new Scene(loader.load()));
            primaryStage.setTitle("Profile Page");
            primaryStage.show();
            initProfileController(loader.getController());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
