package controller;

import com.gluonhq.charm.glisten.control.CardPane;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXSnackbar;
import com.jfoenix.controls.JFXTextField;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import model.Book;
import model.BookAuthor;
import model.BookList;

import java.io.IOException;
import java.sql.SQLException;
import java.util.*;

class Home {

    private int pageSize;
    private int pageNumber;
    private String orderCol;
    private boolean ascending;
    private EditBookDialog editBookDialog;

    @FXML
    private StackPane homeRootPane;

    @FXML
    private JFXTextField searchTextField;
    @FXML
    private JFXButton searchButton;

    @FXML
    private JFXButton sortISBNButton;
    @FXML
    private JFXButton sortTitleButton;
    @FXML
    private JFXButton sortPriceButton;

    @FXML
    private JFXButton refreshButton;

    @FXML
    private CardPane bookCardPane;

    @FXML
    private JFXButton prevPageButton;
    @FXML
    private Label pageNumberLabel;
    @FXML
    private JFXButton nextPageButton;

    private String sortAscImage;

    private String sortDescImage;

    private String sortArrowsImage;

    private String searchImage;

    private String resetSearchImage;

    private boolean isSearching;

    private Map<String, String> searchColumns;

    private LinkedHashMap<String, ArrayList<String>> condition;

    private JFXSnackbar snackBar;

    private Node node;

    private String buttonStyleFormat = "-fx-background-image: url('%s'); " +
            "-fx-background-position: center center; " +
            "-fx-background-repeat: stretch;";

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

        sortAscImage = getClass().getResource("/view/images/sort-arrow-asc-icon.png").toExternalForm();
        sortDescImage = getClass().getResource("/view/images/sort-arrow-desc-icon.png").toExternalForm();
        sortArrowsImage = getClass().getResource("/view/images/sort-arrows-icon.png").toExternalForm();

        searchImage = getClass().getResource("/view/images/search-icon.png").toExternalForm();
        resetSearchImage = getClass().getResource("/view/images/reset-search-icon.png").toExternalForm();

        condition = null;
        isSearching = false;

        snackBar = new JFXSnackbar(homeRootPane);

        searchColumns = new HashMap<>();
        searchColumns.put("isbn", Book.ISBN_COLNAME);
        searchColumns.put("author", BookAuthor.AUTHOR_NAME_COLNAME);
        searchColumns.put("title", Book.BOOK_TITLE_COLNAME);
        searchColumns.put("category", Book.CATEGORY_COLNAME);
        searchColumns.put("publisher", Book.PUBLISHER_NAME_COLNAME);

        initButtons();
        initHome();

    }

    Node getNode() {
        return node;
    }

    private void populateBookView(BookList books) {
        if (books == null) {
            return;
        }
        for (Book book : books.getBooks()) {
            if (book == null) {
                System.out.println("Error: book was null");
                continue;
            }
            BookController bookController = new BookController(book, this);
            bookCardPane.getCards().add(bookController.getNode());
        }
    }

    private void clearBooks() {
        bookCardPane.getCards().clear();
    }

    private void fetchPage(int pageNumber, int pageSize) {
        try {
            BookList books;

            if (isSearching && condition != null) {
                books = MainController.getInstance().getBackendService().findBooks(pageNumber, pageSize,
                        condition, orderCol, ascending);
            } else {
                books = MainController.getInstance().getBackendService().getBooks(pageNumber,
                        pageSize, orderCol, ascending);
            }


            if (books == null) {
                snackBar.enqueue(new JFXSnackbar.SnackbarEvent("Failed to retrieve books."));
                return;
            }

            clearBooks();

            populateBookView(books);

            prevPageButton.setDisable(!books.hasPrev());
            nextPageButton.setDisable(!books.hasNext());

            pageNumberLabel.setText(Integer.toString(pageNumber));
        } catch (SQLException e) {
            e.printStackTrace();
            snackBar.enqueue(new JFXSnackbar.SnackbarEvent("Failed to retrieve books."));
        }
    }

    private void setBackGroundImage(JFXButton button, String imagePath) {
        button.setStyle(String.format(buttonStyleFormat, imagePath));
    }

    private void handleSortOrderChange(String newOrderCol, boolean ascending, JFXButton buttonClicked) {
        switch(orderCol) {
            case Book.ISBN_COLNAME:
                setBackGroundImage(sortISBNButton, sortArrowsImage);
                break;
            case Book.BOOK_TITLE_COLNAME:
                setBackGroundImage(sortTitleButton, sortArrowsImage);
                break;
            case Book.PRICE_COLNAME:
                setBackGroundImage(sortPriceButton, sortArrowsImage);
                break;
            default:
                break;
        }

        if (ascending) {
            setBackGroundImage(buttonClicked, sortAscImage);
        } else {
            setBackGroundImage(buttonClicked, sortDescImage);
        }

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

    void deleteBook(String ISBN) {
        try {
            boolean success = MainController.getInstance().getBackendService().deleteBook(ISBN);
            if (success) {
                handleRefresh();
                snackBar.enqueue(new JFXSnackbar.SnackbarEvent("Book deleted successfully"));
            } else {
                snackBar.enqueue(new JFXSnackbar.SnackbarEvent("Failed to delete book"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            snackBar.enqueue(new JFXSnackbar.SnackbarEvent("Failed to delete book"));
        }
    }

    public void handleRefresh() {
        fetchPage(pageNumber, pageSize);
    }

    private void handlePrevPage() {
        fetchPage(--pageNumber, pageSize);
    }

    private void handleNextPage() {
        fetchPage(++pageNumber, pageSize);
    }

    private LinkedHashMap<String, ArrayList<String>> parseCondition() {
        if (searchTextField.getText().trim().isEmpty()) {
            return null;
        }
        LinkedHashMap<String, ArrayList<String>> cond = new LinkedHashMap<>();

        String text = searchTextField.getText().trim();

        String[] queries = text.split(",");

        for (String query : queries) {
            if (query.contains(":")) {
                String[] components = query.split(":");
                if (components.length != 2) {
                    return null;
                }
                String key = components[0].trim();
                String value = components[1].trim();

                System.out.println(key + " " + value);

                if (!searchColumns.containsKey(key.toLowerCase()) || value.isEmpty()) {
                    return null;
                }
                String colName = searchColumns.get(key);
                if (!cond.containsKey(colName)) {
                    cond.put(colName, new ArrayList<>());
                }
                cond.get(colName).add(value);
            } else {
                if (query.trim().isEmpty()) {
                    return null;
                }

                if (!cond.containsKey(Book.BOOK_TITLE_COLNAME)) {
                    cond.put(Book.BOOK_TITLE_COLNAME, new ArrayList<>());
                }
                cond.get(Book.BOOK_TITLE_COLNAME).add(query.trim());
            }
        }

        return cond;
    }

    private void handleSearch() {
        if (!isSearching) {
            condition = parseCondition();
            if (condition != null) {
                setBackGroundImage(searchButton, resetSearchImage);
                isSearching = true;
                pageNumber = 1;
                searchTextField.setDisable(true);
                fetchPage(pageNumber, pageSize);
            } else {
                snackBar.enqueue(new JFXSnackbar.SnackbarEvent("Invalid search query!"));
            }
        } else {
            setBackGroundImage(searchButton, searchImage);
            searchTextField.clear();
            isSearching = false;
            condition = null;
            pageNumber = 1;
            searchTextField.setDisable(false);
            fetchPage(pageNumber, pageSize);
        }
    }

    private void initButtons() {

        refreshButton.setOnAction(actionEvent -> handleRefresh());
        prevPageButton.setOnAction(actionEvent -> handlePrevPage());
        nextPageButton.setOnAction(actionEvent -> handleNextPage());

        sortISBNButton.setOnAction(actionEvent -> {
            boolean ascending = true;
            if (this.orderCol.equals(Book.ISBN_COLNAME)) {
                ascending = !this.ascending;
            }

            handleSortOrderChange(Book.ISBN_COLNAME, ascending, sortISBNButton);
        });

        sortTitleButton.setOnAction(actionEvent -> {
            boolean ascending = true;
            if (this.orderCol.equals(Book.BOOK_TITLE_COLNAME)) {
                ascending = !this.ascending;
            }

            handleSortOrderChange(Book.BOOK_TITLE_COLNAME, ascending, sortTitleButton);
        });

        sortPriceButton.setOnAction(actionEvent -> {
            boolean ascending = true;
            if (this.orderCol.equals(Book.PRICE_COLNAME)) {
                ascending = !this.ascending;
            }

            handleSortOrderChange(Book.PRICE_COLNAME, ascending, sortPriceButton);
        });

        setBackGroundImage(sortTitleButton, sortAscImage);

        searchTextField.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ENTER) {
                handleSearch();
            }
        });

        searchButton.setOnAction(actionEvent -> handleSearch());
    }

    private void initHome() {
        fetchPage(pageNumber, pageSize);
    }

    void editBook(Book bookToEdit) {
        editBookDialog = new EditBookDialog(homeRootPane, bookToEdit);
        editBookDialog.show();
    }
}
