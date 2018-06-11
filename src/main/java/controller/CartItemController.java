package controller;

import com.gluonhq.charm.glisten.control.CardPane;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.validation.NumberValidator;
import com.jfoenix.validation.RequiredFieldValidator;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import model.Book;
import model.BookAuthor;

import java.io.File;
import java.io.IOException;
import java.util.List;

class CartItemController {

    @FXML
    private ImageView bookCoverView;

    @FXML
    private Label titleLabel;

    @FXML
    private JFXButton removeFromCartButton;

    @FXML
    private Label byLabel;

    @FXML
    private Label authorsLabel;

    @FXML
    private Label isbnLabel;

    @FXML
    private JFXTextField quantityField;

    @FXML
    private Label priceLabel;

    private static final String BOOK_COVER_PATH = "/view" + File.separator
            + "images" + File.separator + "books" + File.separator;
    private static final String DEFAULT_IMAGE = "default.png";

    private CardPane parentCardPane;
    private Book book;
    private Node node;

    CartItemController(CardPane parentCardPane, Book book) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/cartItem.fxml"));
        fxmlLoader.setController(this);
        try {
            node = fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.parentCardPane = parentCardPane;
        this.book = book;
        init();
    }

    Node getNode() {
        return node;
    }

    private void init() {
        removeFromCartButton.setOnMouseClicked(mouseEvent -> handleRemoveFromCartAction());
        initFields();
        initCoverImage();
    }

    private void handleRemoveFromCartAction() {
        parentCardPane.getCards().remove(node);
        MainController.getInstance().getCurrentUser().getCart().remove(book.getISBN());
        // TODO: Update the labels like number of Items and total price.
    }

    private void initFields() {
        if (titleLabel == null) {
            System.out.println("Error Title label was null");
        }
        if (book == null) {
            System.out.println("book was null");
        }
        if (book.getBookTitle() == null) {
            System.out.println("Error Book Title was null");
        }
        titleLabel.setText(book.getBookTitle());
        int count = 0;
        StringBuilder sb = new StringBuilder();
        List<BookAuthor> authors = book.getAuthors();
        for (BookAuthor author : authors) {
            sb.append(author.getAuthorName());
            if (count++ < authors.size() - 1) {
                sb.append(", ");
            }
        }
        authorsLabel.setText(sb.toString());
        if (authorsLabel.getText().isEmpty()) {
            authorsLabel.setVisible(false);
            byLabel.setVisible(false);
        }
        initQuantityField();
        priceLabel.setText(Double.toString(book.getPrice()));
    }

    private void initCoverImage() {
        String imagePath;

        if (book.getImagePath() == null) {
            imagePath = BOOK_COVER_PATH;
            imagePath += DEFAULT_IMAGE;
            imagePath = getClass().getResource(imagePath).toExternalForm();
        } else {
            imagePath = book.getImagePath();
        }

        bookCoverView.setImage(new Image(imagePath));
    }

    private void refreshQuantityField() {
        quantityField.setText(Integer.toString(MainController.getInstance()
                .getCurrentUser().getCart().getValue(book.getISBN())));
    }

    private void initQuantityField() {
        refreshQuantityField();
        RequiredFieldValidator requiredFieldValidator = new RequiredFieldValidator();
        NumberValidator numberValidator = new NumberValidator();

        requiredFieldValidator.setMessage("Quantity Required");
        numberValidator.setMessage("Integers Only");

        quantityField.getValidators().addAll(requiredFieldValidator, numberValidator);

        quantityField.focusedProperty().addListener((o, oldVal, newVal) -> {
            if (!newVal) quantityField.validate();
        });
    }
}
