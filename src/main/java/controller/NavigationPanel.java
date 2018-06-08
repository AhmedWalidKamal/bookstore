package controller;

import com.gluonhq.charm.glisten.control.Avatar;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;

public class NavigationPanel {

    @FXML
    private Avatar userAvatar;

    @FXML
    private Label userName;

    @FXML
    private Label userEmail;

    private MainController mainController;

    public NavigationPanel () {

    }

    public Image getUserAvatar() {
        return this.userAvatar.getImage();
    }

    public void setUserAvatar(Image image) {
        this.userAvatar.setImage(image);
    }

    public String getUserName() {
        return this.userName.getText();
    }

    public void setUserName(String userName) {
        this.userName.setText(userName);
    }

    public String getUserEmail() {
        return this.userEmail.getText();
    }

    public void setUserEmail(String userName) {
        this.userEmail.setText(userName);
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    public void navigateToHome(MouseEvent mouseEvent) {
        mouseEvent.consume();
        // TODO: Navigate To home screen.
    }
}
