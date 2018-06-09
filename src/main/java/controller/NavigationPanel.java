package controller;

import com.gluonhq.charm.glisten.control.Avatar;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.image.Image;

import java.io.IOException;

class NavigationPanel {

    @FXML
    private Avatar userAvatar;

    @FXML
    private Label userName;

    @FXML
    private Label userEmail;

    private Parent parent;

    private Home home;
    private ShoppingCart shoppingCart;
    private Profile profile;
    private Administration administration;

    NavigationPanel () {
        if (parent == null) {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/navigationPanel.fxml"));
            fxmlLoader.setController(this);
            try {
                parent = fxmlLoader.load();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    Image getUserPhoto() {
        return userAvatar.getImage();
    }

    void setUserAvatar(String userPhotoPath) {
        if (userPhotoPath == null) {
            userPhotoPath = getClass().getResource("/view/images/user/user-default-photo.png").toExternalForm();
            System.out.println(userPhotoPath);
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
        setUserAvatar(userPhotoPath);
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
}
