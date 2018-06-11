package controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXSnackbar;
import com.jfoenix.controls.JFXTextField;
import controller.util.BookUtil;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

import java.sql.SQLException;

public class AddBook {

    @FXML
    private JFXTextField isbn, title, authors, publisher, price, threshold, publicationYear;

    @FXML
    private JFXComboBox<Label> category;

    @FXML
    private JFXButton addBookButton, clearButton;

    @FXML
    private Label failLabel;

    @FXML
    private AnchorPane addBookRootPane;

    private BookUtil bookUtil;

    @FXML
    public void initialize() {
        bookUtil = new BookUtil(isbn, title, authors, publisher, price, threshold, publicationYear, category, failLabel);
        initButtons();
        bookUtil.initFieldsVaildators();
    }

    private void initButtons() {
        initClear();
        initAddBook();
    }

    private void initClear() {
        clearButton.setOnAction(actionEvent -> handleClearButton());
    }

    private void handleClearButton() {
        JFXSnackbar bar = new JFXSnackbar(addBookRootPane);
        bar.enqueue(new JFXSnackbar.SnackbarEvent("Fields Cleared"));
        failLabel.setVisible(false);
        bookUtil.clearFields();
    }

    private void initAddBook() {
        addBookButton.setOnAction(actionEvent -> handleAddBook());
    }

    private void handleAddBook() {
        if (bookUtil.validateFields()) {
            try {
                boolean success = MainController.getInstance().getBackendService().
                        addBook(isbn.getText(), publisher.getText(),
                                title.getText(), Integer.parseInt(threshold.getText()),
                               bookUtil.getDate(publicationYear.getText()), Float.parseFloat(price.getText()),
                                category.getValue().getText(), bookUtil.getAuthorsList(authors.getText()));
                if (success) {
                    JFXSnackbar bar = new JFXSnackbar(addBookRootPane);
                    bar.enqueue(new JFXSnackbar.SnackbarEvent("Book Added Successfully"));
                    failLabel.setVisible(false);
                    bookUtil.clearFields();
                } else {
                    failLabel.setText("Failed to add book!");
                    failLabel.setVisible(true);
                }

            } catch (SQLException e) {
                e.printStackTrace();
                failLabel.setText("Failed to add book!");
                failLabel.setVisible(true);
            }
        } else {
            failLabel.setVisible(true);
        }
    }
}
