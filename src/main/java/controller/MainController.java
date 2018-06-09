package controller;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import model.BookstoreUser;
import service.BackendServices;

import java.io.IOException;
import java.sql.SQLException;

public class MainController {

    private static final int SCENE_WIDTH = 1000;
    private static final int SCENE_HEIGHT= 600;

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

    public void init (Stage primaryStage) {
        this.primaryStage = primaryStage;
        loadSignInScene(false);
    }

    BookstoreUser getCurrentUser() {
        return currentUser;
    }

    void setCurrentUser(BookstoreUser currentUser) {
        this.currentUser = currentUser;
    }

    BackendServices getBackendService() {
        return backendServices;
    }

    void loadSignInScene(boolean displayRegistrationMessage) {
        if (signIn == null) {
            signIn = new SignIn();
        }
        signIn.clearInputFields();
        if (primaryStage.getScene() == null) {
            Scene scene = new Scene(signIn.getParent(), SCENE_WIDTH, SCENE_HEIGHT);
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
            Scene scene = new Scene(signUp.getParent(), SCENE_WIDTH, SCENE_HEIGHT);
            primaryStage.setScene(scene);
        } else {
            primaryStage.getScene().setRoot(signUp.getParent());
        }
        primaryStage.setTitle("Library Bookstores | Sign Up");
        primaryStage.show();
    }

    void loadNavigationPanelScene() {
        if (navigationPanel == null) {
            navigationPanel = new NavigationPanel();
        }
        navigationPanel.setUserInfo(getCurrentUser().getUserName(), getCurrentUser().getEmail()
                                                    , getCurrentUser().getProfile().getUserPhotoPath());
        // TODO: Set the default scene to home on loading the navigation panel.
        if (primaryStage.getScene() == null) {
            Scene scene = new Scene(navigationPanel.getParent(), SCENE_WIDTH, SCENE_HEIGHT);
            primaryStage.setScene(scene);
        } else {
            primaryStage.getScene().setRoot(navigationPanel.getParent());
        }
        primaryStage.setTitle("Library Bookstores");
        primaryStage.show();
    }

    void loadProfileScene() {
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
