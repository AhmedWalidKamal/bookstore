package controller;

import com.jfoenix.controls.JFXButton;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Paint;
import model.Book;
import model.BookAuthor;

import java.io.File;
import java.io.IOException;
import java.util.List;

class BookController {

    @FXML
    private ImageView bookCoverView;
    @FXML
    private Label titleLabel;
    @FXML
    private Label authorsLabel;
    @FXML
    private Label categoryLabel;

    @FXML
    private FontAwesomeIcon oneStarIcon;
    @FXML
    private FontAwesomeIcon twoStarIcon;
    @FXML
    private FontAwesomeIcon threeStarIcon;
    @FXML
    private FontAwesomeIcon fourStarIcon;
    @FXML
    private FontAwesomeIcon fiveStarIcon;

    private FontAwesomeIcon[] stars;

    @FXML
    private Label publicationYearLabel;
    @FXML
    private Label publisherLabel;
    @FXML
    private Label priceLabel;
    @FXML
    private Label outOfStockLabel;
    @FXML
    private Label byLabel;

    @FXML
    private JFXButton addToCartButton;

    @FXML
    private JFXButton deleteButton;

    @FXML
    private JFXButton editButton;

    private static final String BOOK_COVER_PATH = "/view" + File.separator
            + "images" + File.separator + "books" + File.separator;
    private static final String DEFAULT_IMAGE = "default.png";


    private Book book;
    private Node node;
    private Home home;

    BookController(Book book, Home home) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/book.fxml"));
        fxmlLoader.setController(this);
        try {
            node = fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.book = book;
        stars = new FontAwesomeIcon[] {oneStarIcon, twoStarIcon, threeStarIcon, fourStarIcon, fiveStarIcon};
        initButtons();
        initFields();
        initCoverImage();
        initRating();
        this.home = home;
    }

    Node getNode() {
        return node;
    }

    private void initButtons() {
        addToCartButton.setOnAction(actionEvent -> addToCart());
        deleteButton.setOnAction(actionEvent -> deleteBook());
        editButton.setOnAction(actionEvent -> editBook());
    }

    private void addToCart() {
        int quantity = MainController.getInstance().getCurrentUser()
                .getCart().getOrDefault(book.getISBN(), 0);
        MainController.getInstance().getCurrentUser()
                .getCart().add(book.getISBN(), quantity + 1);
        System.out.println(quantity);
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
        categoryLabel.setText(book.getCategory());
        String year = "";
        if (book.getPublicationYear() != null) {
            year = Integer.toString(book.getPublicationYear().getYear());
        }
        publicationYearLabel.setText(year);
        priceLabel.setText(Double.toString(book.getPrice()));
        publisherLabel.setText(book.getPublisherName());
        if (book.getBooksInStock() <= 0) {
            addToCartButton.setVisible(false);
        } else {
            outOfStockLabel.setVisible(false);
        }
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

    private void initRating() {
        int starsCount = (int) (book.getRating() + 0.5);
        int count = 0;
        for (FontAwesomeIcon star : stars) {
            count++;
            if (count > starsCount) {
                star.setFill(Paint.valueOf("#cccccc"));
            }
        }
    }

    private void deleteBook() {
        this.home.deleteBook(book.getISBN());
    }

    private void editBook() {
        this.home.editBook(book);
    }
}
