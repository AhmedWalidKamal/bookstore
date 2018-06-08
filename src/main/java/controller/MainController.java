package controller;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import model.BookstoreUser;
import service.BackendServices;

import java.io.IOException;
import java.sql.SQLException;

public class MainController {

    private Stage primaryStage;
    private BackendServices sys;
    private BookstoreUser currentUser;

    public MainController(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.currentUser = null;
        try {
            // Establish connection with database
            sys = new BackendServices();
            loadSignInScene(false);
//            sys.showTopSellingBooks(primaryStage);
        } catch (SQLException e) {
            e.printStackTrace();
            // Log error
            System.out.println("Couldn't establish connection");
        }
    }

    public void setCurrentUser(BookstoreUser user) {
        currentUser = user;
    }

    public BookstoreUser getCurrentUser() {
        return this.currentUser;
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
        profile.initProfile(this);
    }

    private void initSignUpController(SignUp signUp) {
        signUp.setBackEndService(sys);
        signUp.setMainController(this);
    }

    private void initSignInController(SignIn signIn, boolean displayRegistrationMessage) {
        signIn.setBackEndService(sys);
        signIn.setMainController(this);
        if (displayRegistrationMessage) {
            signIn.dispRegMessage();
        }
    }

    public void loadSignInScene(boolean displayRegistrationMessage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/signIn.fxml"));
            Scene scene = new Scene(loader.load());
            scene.getStylesheets().add(getClass().getResource("/view/css/signIn.css").toExternalForm());
            primaryStage.setScene(scene);
            primaryStage.setTitle("Library Bookstores");
//            primaryStage.setMaximized(true);
            primaryStage.show();
            initSignInController(loader.getController(), displayRegistrationMessage);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void loadSignUpScene() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/signUp.fxml"));
            Scene scene = new Scene(loader.load());
            scene.getStylesheets().add(getClass().getResource("/view/css/signUp.css").toExternalForm());
            primaryStage.setScene(scene);
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
            Scene scene = new Scene(loader.load());
            scene.getStylesheets().add(getClass().getResource("/view/css/profile.css").toExternalForm());
            primaryStage.setScene(scene);
            primaryStage.setTitle("Manage Profile");
//            primaryStage.setMaximized(true);
            primaryStage.show();
            initProfileController(loader.getController());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public BackendServices getBackendService() {
        return this.sys;
    }
}
