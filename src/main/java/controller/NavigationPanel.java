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
        initNavPanel();
    }

    private void initNavPanel() {
        setUserAvatar(MainController.getInstance().getCurrentUser().getProfile().getUserAvatarPath());
        setUserName(MainController.getInstance().getCurrentUser().getUserName());
        setUserEmail(MainController.getInstance().getCurrentUser().getEmail());
    }

    Image getUserAvatar() {
        return userAvatar.getImage();
    }

    void setUserAvatar(Image userImage) {
        userAvatar.setImage(userImage);
    }

    void setUserAvatar(String userImagePath) {
        Image image = new Image(userImagePath);
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
}
