package controller;

import com.jfoenix.controls.*;
import controller.util.BookUtil;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import model.Book;
import model.BookAuthor;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.StringJoiner;

class EditBookDialog {

    @FXML
    private JFXTextField isbn, title, authors, publisher, price, threshold, publicationYear;

    @FXML
    private JFXComboBox<Label> category;

    @FXML
    private JFXButton editBookButton, clearButton;

    @FXML
    private Label failLabel;

    private JFXSnackbar snackbar;

    private AnchorPane dialogContentRootPane;
    private JFXDialog dialog;
    private BookUtil bookUtil;
    private Book bookToEdit;
    private BookController bookController;

    EditBookDialog(StackPane homeRootPane, Book bookToEdit, BookController bookController) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/editBook.fxml"));
        fxmlLoader.setController(this);
        try {
            dialogContentRootPane = fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.bookToEdit = bookToEdit;
        this.bookController = bookController;
        dialog = new JFXDialog(homeRootPane, dialogContentRootPane, JFXDialog.DialogTransition.CENTER);
        bookUtil = new BookUtil(isbn, title, authors, publisher, price, threshold, publicationYear, category, failLabel);
        snackbar = new JFXSnackbar(homeRootPane);
        initButtons();
        bookUtil.initFieldsVaildators();
        initFields();
    }

    private void initFields() {
        this.isbn.setText(bookToEdit.getISBN());
        this.title.setText(bookToEdit.getBookTitle());
        StringJoiner authorsStringJoiner = new StringJoiner(", ");
        for (BookAuthor author : bookToEdit.getAuthors()) {
            authorsStringJoiner.add(author.getAuthorName());
        }
        this.authors.setText(authorsStringJoiner.toString());
        this.publisher.setText(bookToEdit.getPublisherName());
        this.price.setText(Double.toString(bookToEdit.getPrice()));
        this.threshold.setText(Integer.toString(bookToEdit.getMinThreshold()));
        this.publicationYear.setText(Integer.toString(bookToEdit.getPublicationYear().getYear()));
        ObservableList<Label> categories = this.category.getItems();
        for (Label currCategory : categories) {
            if (currCategory.getText().equals(bookToEdit.getCategory())) {
                this.category.setValue(currCategory);
                break;
            }
        }
    }

    private void initButtons() {
        initClear();
        initEdit();
    }

    private void initEdit() {
        editBookButton.setOnAction(actionEvent -> handleEditBookButton());
    }

    private void handleEditBookButton() {
        if (bookUtil.validateFields()) {
            try {
                LinkedHashMap<String, String> newVals = new LinkedHashMap<>();
                newVals.put(Book.ISBN_COLNAME, isbn.getText());
                newVals.put(Book.BOOK_TITLE_COLNAME, title.getText());
                newVals.put(Book.PUBLISHER_NAME_COLNAME, publisher.getText());
                newVals.put(Book.PRICE_COLNAME, price.getText());
                newVals.put(Book.MIN_THRESHOLD_COLNAME, threshold.getText());
                newVals.put(Book.PUBLICATION_YEAR_COLNAME, publicationYear.getText());
                newVals.put(Book.CATEGORY_COLNAME, category.getValue().getText());
                boolean success = MainController.getInstance().
                        getBackendService().modifyBook(bookToEdit.getISBN(),
                        newVals, bookUtil.getAuthorsList(authors.getText()));
                if (success) {
                    snackbar.enqueue(new JFXSnackbar.SnackbarEvent("Book Edited Successfully"));
                    failLabel.setVisible(false);
                    bookToEdit.setISBN(isbn.getText());
                    bookToEdit.setBookTitle(title.getText());
                    bookToEdit.setPublisherName(publisher.getText());
                    bookToEdit.setPrice(Double.parseDouble(price.getText()));
                    bookToEdit.setMinThreshold(Integer.parseInt(threshold.getText()));
                    bookToEdit.setPublicationYear(LocalDate.of(Integer.parseInt(
                            publicationYear.getText()), 1, 1));
                    bookToEdit.setCategory(category.getValue().getText());
                    bookToEdit.getAuthors().clear();
                    for (String author : authors.getText().split(",")) {
                        BookAuthor newAuthor = new BookAuthor();
                        newAuthor.setISBN(bookToEdit.getISBN());
                        newAuthor.setAuthorName(author.trim());
                        bookToEdit.getAuthors().add(newAuthor);
                    }
                    bookController.initFields();
                    dialog.close();
                } else {
                    failLabel.setText("Failed to edit book!");
                    failLabel.setVisible(true);
                }

            } catch (SQLException e) {
                e.printStackTrace();
                failLabel.setText("Failed to edit book!");
                failLabel.setVisible(true);
            }
        } else {
            failLabel.setVisible(true);
        }
    }

    private void initClear() {
        clearButton.setOnAction(actionEvent -> handleClearButton());
    }

    private void handleClearButton() {
        snackbar.enqueue(new JFXSnackbar.SnackbarEvent("Fields Cleared"));
        failLabel.setVisible(false);
        bookUtil.clearFields();
    }

    void show() {
        dialog.show();
    }
}
