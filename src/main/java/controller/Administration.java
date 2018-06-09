package controller;

import com.jfoenix.controls.*;
import com.jfoenix.validation.RequiredFieldValidator;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
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

public class Administration {

    private Parent parent;

    @FXML
    private AnchorPane rootPane;

    private AddBook addBookController;
    private BookOrders bookOrdersController;
    private Reports reportsController;
    private UserPromotion userPromotionController;

    Administration() {
        if (parent == null) {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/administration.fxml"));
            fxmlLoader.setController(this);
            try {
                parent = fxmlLoader.load();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    Parent getParent() {
        return parent;
    }
}
