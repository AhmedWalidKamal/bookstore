package controller;

import com.jfoenix.controls.JFXSnackbar;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import model.Book;
import model.BookList;

import java.io.IOException;
import java.sql.SQLException;

class Home {

    private int pageSize;
    private int pageNumber;
    private String orderCol;
    private boolean ascending;

    @FXML
    private AnchorPane homeRootPane;

    private Node node;

    Home() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/home.fxml"));
        fxmlLoader.setController(this);
        try {
            node = fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        pageSize = 10;
        pageNumber = 1;

        orderCol = Book.BOOK_TITLE_COLNAME;
        ascending = true;

        initHome();
    }

    Node getNode() {
        return node;
    }

    private void fetchPage(int pageNumber, int pageSize) {
        try {
            BookList books = MainController.getInstance().getBackendService().getBooks(pageNumber, pageSize, orderCol, ascending);

            if (books == null) {
                JFXSnackbar bar = new JFXSnackbar(homeRootPane);
                bar.enqueue(new JFXSnackbar.SnackbarEvent("Failed to retrieve books."));
            }

            if (books.hasPrev()) {

            }

            if (books.hasNext()) {

            }

        } catch (SQLException e) {
            JFXSnackbar bar = new JFXSnackbar(homeRootPane);
            bar.enqueue(new JFXSnackbar.SnackbarEvent("Failed to retrieve books."));
        }
    }

    private void handleSortOrderChange(String newOrderCol, boolean ascending) {
        orderCol = newOrderCol;
        this.ascending = ascending;
        pageNumber = 1;
        fetchPage(pageNumber, pageSize);
    }

    private void handlePageSizeChange(int newSize) {
        // Or we could simply return to page 1
        int firstElement = pageSize * (pageNumber - 1) + 1; // First element in the current page
        pageSize = newSize;
        pageNumber = (firstElement - 1) / pageSize + 1;
        fetchPage(pageNumber, pageSize);
    }

    private void handleNextPage() {
        fetchPage(++pageNumber, pageSize);
    }

    private void handlePrevPage() {
        fetchPage(--pageNumber, pageSize);
    }

    private void initHome() {
        fetchPage(pageNumber, pageSize);
    }
}
