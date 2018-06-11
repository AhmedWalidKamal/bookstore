package controller;

import com.gluonhq.charm.glisten.control.Avatar;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import model.CartListener;

import java.io.IOException;

class NavigationPanel implements CartListener {

    @FXML
    private StackPane navigationPanelRootPane;

    @FXML
    private BorderPane borderPane;

    @FXML
    private Avatar userAvatar;

    @FXML
    private Label userName;

    @FXML
    private Label userEmail;

    @FXML
    private Label cartLabel;

    @FXML
    private HBox homeButton;

    @FXML
    private HBox shoppingCartButton;

    @FXML
    private HBox profileButton;

    @FXML
    private HBox administrationButton;

    @FXML
    private HBox signOutButton;

    private Parent parent;

    private Home home;
    private ShoppingCart shoppingCart;
    private Profile profile;
    private Administration administration;

    NavigationPanel () {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/navigationPanel.fxml"));
        fxmlLoader.setController(this);
        try {
            parent = fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        init();
    }

    Image getUserPhoto() {
        return userAvatar.getImage();
    }

    void setUserPhoto(String userPhotoPath) {
        if (userPhotoPath == null) {
            userPhotoPath = getClass().getResource("/view/images/user/user-default-photo.png").toExternalForm();
        }
        Image image = new Image(userPhotoPath);
        userAvatar.setImage(image);
    }

    String getUserName() {
        return userName.getText();
    }

    void setUserName(String userName) {
        this.userName.setText(userName);
    }

    String getUserEmail() {
        return userEmail.getText();
    }

    void setUserEmail(String userEmail) {
        this.userEmail.setText(userEmail);
    }

    void setUserInfo(String userName, String userEmail,  String userPhotoPath) {
        setUserName(userName);
        setUserEmail(userEmail);
        setUserPhoto(userPhotoPath);
    }

    Parent getParent() {
        return parent;
    }

    Home getHomeController() {
        return home;
    }

    ShoppingCart getShoppingCartController() {
        return shoppingCart;
    }

    Profile getProfileController() {
        return profile;
    }

    Administration getAdministrationController() {
        return administration;
    }

    private void init() {
        homeButton.setOnMouseClicked(mouseEvent -> loadHome());
        shoppingCartButton.setOnMouseClicked(mouseEvent -> loadShoppingCart());
        profileButton.setOnMouseClicked(mouseEvent -> loadProfile());
        administrationButton.setOnMouseClicked(mouseEvent -> loadAdministration());
        signOutButton.setOnMouseClicked(mouseEvent -> signOut());

        MainController.getInstance().getCurrentUser().getCart().addListener(this);
    }

    void loadHome() {
        if (home == null) {
            home = new Home();
        } else {
            home.handleRefresh();
        }
        borderPane.getChildren().remove(borderPane.getCenter());
        borderPane.setCenter(home.getNode());
        MainController.getInstance().getPrimaryStage().setTitle("Library Bookstores | Home");
    }

    private void loadShoppingCart() {
        if (shoppingCart == null) {
            shoppingCart = new ShoppingCart(navigationPanelRootPane);
        } else {
            shoppingCart.refresh();
        }
        // TODO: Make sure to keep the current state of the shopping cart for each user (session).
        borderPane.getChildren().remove(borderPane.getCenter());
        borderPane.setCenter(shoppingCart.getNode());
        MainController.getInstance().getPrimaryStage().setTitle("Library Bookstores | Shopping Cart");
    }

    private void loadProfile() {
        if (profile == null) {
            profile = new Profile();
        }
        // TODO: Makes sure once the user sign in his available data fill the profile fields.
        borderPane.getChildren().remove(borderPane.getCenter());
        borderPane.setCenter(profile.getNode());
        MainController.getInstance().getPrimaryStage().setTitle("Library Bookstores | Profile");
    }

    private void loadAdministration() {
        if (administration == null) {
            administration = new Administration();
        }
        borderPane.getChildren().remove(borderPane.getCenter());
        borderPane.setCenter(administration.getNode());
        MainController.getInstance().getPrimaryStage().setTitle("Library Bookstores | Administration");
    }

    private void signOut() {
        // Clear the status of the current session.
        profile = null;
        shoppingCart = null;
        home = null;
        administration = null;
        MainController.getInstance().loadSignIn(false);
    }

    void disableAdminPrivileges() {
        administrationButton.setDisable(true);
    }

    void enableAdminPrivileges() {
        administrationButton.setDisable(false);
    }

    @Override
    public void cartSizeHasChanged() {
        cartLabel.setText(Integer.toString(MainController.getInstance().getCurrentUser().getCart().size()));
    }
}
