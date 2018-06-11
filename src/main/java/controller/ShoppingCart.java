package controller;

import com.gluonhq.charm.glisten.control.CardPane;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXSnackbar;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import model.Book;
import model.BookList;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

class ShoppingCart {


    @FXML
    private AnchorPane shoppingCartRootPane;

    @FXML
    private Label numberOfItemsLabel;

    @FXML
    private Label totalPriceLabel;

    @FXML
    private JFXButton purchaseButton;

    @FXML
    private CardPane cartItemCardPane;

    private StackPane rootPane;

    private PurchaseDialog purchaseDialog;

    private JFXSnackbar snackBar;

    private Node node;

    private BookList books;

    private int numberOfItems;
    private double totalCost;

    ShoppingCart(StackPane rootPane) {
        this.rootPane = rootPane;
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/shoppingCart.fxml"));
        fxmlLoader.setController(this);
        try {
            node = fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        totalCost = 0;
        numberOfItems = 0;
        init();

        snackBar = new JFXSnackbar(shoppingCartRootPane);

        refresh();
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
            CartItemController cartItemController = new CartItemController(cartItemCardPane, book, this);
            cartItemCardPane.getCards().add(cartItemController.getNode());
        }
    }

    private void clearBooks() {
        cartItemCardPane.getCards().clear();
    }

    private void setLabels() {
        numberOfItemsLabel.setText(Integer.toString(numberOfItems));
        totalPriceLabel.setText(Double.toString(totalCost));
    }

    private void fetchBooks() {
        try {
            BookList books = null;

            ArrayList<String> ISBNValues = new ArrayList<>(MainController
                    .getInstance().getCurrentUser().getCart().getKeys());

            books = MainController.getInstance()
                    .getBackendService().getBooks(ISBNValues, Book.BOOK_TITLE_COLNAME, true);


            if (books == null) {
                snackBar.enqueue(new JFXSnackbar.SnackbarEvent("Failed to retrieve books."));
                return;
            }

            if (books.getBooks().size() != MainController.getInstance().getCurrentUser().getCart().size()) {
                snackBar.enqueue(new JFXSnackbar.SnackbarEvent("Failed to retrieve some books."));
            }

            clearBooks();

            populateBookView(books);

            this.books = books;

            numberOfItems = books.size();
            totalCost = 0;
            for (Book book : books.getBooks()) {
                totalCost += book.getPrice() * MainController.getInstance().getCurrentUser()
                        .getCart().getOrDefault(book.getISBN(), 0);
            }

            setLabels();
        } catch (SQLException e) {
            e.printStackTrace();
            snackBar.enqueue(new JFXSnackbar.SnackbarEvent("Failed to retrieve books."));
        }
    }

    private void init() {
        purchaseButton.setOnMouseClicked(mouseEvent -> handlePurchaseButtonAction());
    }

    private void handlePurchaseButtonAction() {
        if (purchaseDialog == null) {
            purchaseDialog = new PurchaseDialog(rootPane, this);
        }
        purchaseDialog.show();
    }

    public void refresh() {
        fetchBooks();
    }

    public void clear() {
        cartItemCardPane.getCards().clear();
        MainController.getInstance().getCurrentUser().getCart().clear();
        numberOfItems = 0;
        totalCost = 0;
        setLabels();
    }

    public void showError(String errorMessage) {
        snackBar.enqueue(new JFXSnackbar.SnackbarEvent(errorMessage));
    }

    public void showMessage(String message) {
        snackBar.enqueue(new JFXSnackbar.SnackbarEvent(message));
    }

    public void adjustLabels(double price, int oldCount, int newCount) {
        numberOfItems += newCount - oldCount;
        totalCost += (newCount - oldCount) * price;
        setLabels();
    }
}
