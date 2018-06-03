package model;

import java.util.Objects;

public class BookAuthor {

    public static final String ISBN_COLNAME = "ISBN";
    public static final String AUTHOR_NAME_COLNAME = "AUTHOR_NAME";

    private String ISBN;
    private String authorName;

    public BookAuthor() {

    }

    public String getISBN() {
        return ISBN;
    }

    public void setISBN(String ISBN) {
        this.ISBN = ISBN;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BookAuthor that = (BookAuthor) o;
        return Objects.equals(ISBN, that.ISBN) &&
                Objects.equals(authorName, that.authorName);
    }

    @Override
    public int hashCode() {

        return Objects.hash(ISBN, authorName);
    }
}
