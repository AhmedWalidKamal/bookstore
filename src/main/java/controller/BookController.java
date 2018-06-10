package controller;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Paint;
import model.Book;
import model.BookAuthor;

import java.io.File;
import java.io.IOException;
import java.util.List;

class BookController {

    @FXML
    private AnchorPane rootPane;
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


    private static final String BOOK_COVER_PATH = "/view" + File.separator
            + "images" + File.separator + "books" + File.separator;
    private static final String DEFAULT_IMAGE = "default.png";


    private Book book;
    private Node node;

    BookController(Book book) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/book.fxml"));
        fxmlLoader.setController(this);
        try {
            node = fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.book = book;
        stars = new FontAwesomeIcon[] {oneStarIcon, twoStarIcon, threeStarIcon, fourStarIcon, fiveStarIcon};
        initFields();
        initCoverImage();
        initRating();
    }

    Node getNode() {
        return node;
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
        categoryLabel.setText(book.getCategory());
        String year = "";
        if (book.getPublicationYear() != null) {
            year = Integer.toString(book.getPublicationYear().getYear());
        }
        publicationYearLabel.setText(year);
        priceLabel.setText(Double.toString(book.getPrice()));
        publisherLabel.setText(book.getPublisherName());
        outOfStockLabel.setVisible(book.getBooksInStock() > 0);




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
}
