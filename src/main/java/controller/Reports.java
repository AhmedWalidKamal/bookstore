package controller;

import com.jfoenix.controls.JFXButton;
import javafx.fxml.FXML;


public class Reports {
    @FXML
    private JFXButton topTenBooksButton;

    @FXML
    private JFXButton topFiveUsersButton;

    @FXML
    private JFXButton lastMonthSalesButton;

    @FXML
    public void initialize() {
        initButtons();
    }

    private void initButtons() {

        topTenBooksButton.setOnMouseClicked(mouseEvent ->
                MainController.getInstance().getBackendService()
                        .showTopSellingBooks(MainController.getInstance().getPrimaryStage()));
        topFiveUsersButton.setOnMouseClicked(mouseEvent ->
                MainController.getInstance().getBackendService()
                        .showTopUsers(MainController.getInstance().getPrimaryStage()));
        lastMonthSalesButton.setOnMouseClicked(mouseEvent ->
                MainController.getInstance().getBackendService()
                        .showLastMonthReport(MainController.getInstance().getPrimaryStage()));
    }

}
