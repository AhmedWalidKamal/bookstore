package model;

import java.time.Year;
import java.util.*;

public class Book {

    public static final String ISBN_COLNAME = "ISBN";
    public static final String PUBLISHER_NAME_COLNAME = "PUBLISHER_NAME";
    public static final String BOOK_TITLE_COLNAME = "BOOK_TITLE";
    public static final String BOOKS_IN_STOCK_COLNAME = "BOOKS_IN_STOCK";
    public static final String MIN_THRESHOLD_COLNAME = "MIN_THRESHOLD";
    public static final String PUBLICATION_YEAR_COLNAME = "PUBLICATION_YEAR";
    public static final String PRICE_COLNAME = "PRICE";
    public static final String CATEGORY_COLNAME = "CATEGORY";
    public static final String IMAGE_PATH_COLNAME = "IMAGE_PATH";
    public static final String RATING_COLNAME = "RATING";

    public static final List<String> columns = Arrays.asList(ISBN_COLNAME,
                                                             PUBLISHER_NAME_COLNAME,
                                                             BOOK_TITLE_COLNAME,
                                                             BOOKS_IN_STOCK_COLNAME,
                                                             MIN_THRESHOLD_COLNAME,
                                                             PUBLICATION_YEAR_COLNAME,
                                                             PRICE_COLNAME,
                                                             CATEGORY_COLNAME,
                                                             IMAGE_PATH_COLNAME,
                                                             RATING_COLNAME);

    private String ISBN;
    private String publisherName;
    private String bookTitle;
    private int booksInStock;
    private int minThreshold;
    private Date publicationYear;
    private double price;
    private String category;
    private String imagePath;
    private double rating;


    private List<BookAuthor> authors;

    public Book() {
        authors = new ArrayList<>();
    }

    public String getISBN() {
        return ISBN;
    }

    public void setISBN(String ISBN) {
        this.ISBN = ISBN;
    }

    public String getPublisherName() {
        return publisherName;
    }

    public void setPublisherName(String publisherName) {
        this.publisherName = publisherName;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public void setBookTitle(String bookTitle) {
        this.bookTitle = bookTitle;
    }

    public int getBooksInStock() {
        return booksInStock;
    }

    public void setBooksInStock(int booksInStock) {
        this.booksInStock = booksInStock;
    }

    public int getMinThreshold() {
        return minThreshold;
    }

    public void setMinThreshold(int minThreshold) {
        this.minThreshold = minThreshold;
    }

    public Date getPublicationYear() {
        return publicationYear;
    }

    public void setPublicationYear(Date publicationYear) {
        this.publicationYear = publicationYear;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public List<BookAuthor> getAuthors() {
        return authors;
    }

    public void setAuthors(List<BookAuthor> authors) {
        this.authors = authors;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Book book = (Book) o;
        return Objects.equals(ISBN, book.ISBN);
    }

    @Override
    public int hashCode() {

        return Objects.hash(ISBN);
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }
}
