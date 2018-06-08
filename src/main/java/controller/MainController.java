package controller;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import model.BookstoreUser;
import service.BackendServices;

import java.io.IOException;
import java.sql.SQLException;

public class MainController {

    private static MainController instance;

    private Stage primaryStage;
    private BackendServices backendServices;
    private BookstoreUser currentUser;

    private SignIn signIn;
    private SignUp signUp;
    private NavigationPanel navigationPanel;

    private MainController() {
        primaryStage = null;
        currentUser = null;
        try {
            backendServices = new BackendServices();
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Couldn't establish connection");
        }
    }

    public static MainController getInstance() {
        if (instance == null) {
            instance = new MainController();
        }
        return instance;
    }

    public void init () {
        loadSignInScene(false);
    }

    BookstoreUser getCurrentUser() {
        return this.currentUser;
    }

    void setCurrentUser(BookstoreUser user) {
        currentUser = user;
    }

    Stage getPrimaryStage() {
        return this.primaryStage;
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    BackendServices getBackendService() {
        return this.backendServices;
    }

    void setBackendServices(BackendServices backendServices) {
        this.backendServices = backendServices;
    }

    void loadSignInScene(boolean displayRegistrationMessage) {
        if (signIn == null) {
            signIn = new SignIn();
        }
        signIn.clearInputFields();
        if (primaryStage.getScene() == null) {
            Scene scene = new Scene(signIn.getParent());
            primaryStage.setScene(scene);
        } else {
            primaryStage.getScene().setRoot(signIn.getParent());
        }
        primaryStage.setTitle("Library Bookstores | Sign In");
        primaryStage.show();
        if (displayRegistrationMessage) {
            signIn.displayRegMessage();
        }
    }

    void loadSignUpScene() {
        if (signUp == null) {
            signUp = new SignUp();
        }
        signUp.clearInputFields();
        if (primaryStage.getScene() == null) {
            Scene scene = new Scene(signUp.getParent());
            primaryStage.setScene(scene);
        } else {
            primaryStage.getScene().setRoot(signUp.getParent());
        }
        primaryStage.setTitle("Library Bookstores | Sign Up");
        primaryStage.show();
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
//            initProfileController(loader.getController());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



}
