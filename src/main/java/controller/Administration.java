package controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXSnackbar;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.validation.RequiredFieldValidator;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;

class Administration {

    @FXML
    private AnchorPane rootPane;

    private AddBook addBookController;
    private BookOrders bookOrdersController;
    private Reports reportsController;
    private UserPromotion userPromotionController;
    private Node node;

    Administration() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/administration.fxml"));
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
