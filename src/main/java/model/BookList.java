package model;

import java.util.*;

public class BookList {
    private Map<String, Book> books;

    public BookList() {
        books = new LinkedHashMap<>();
    }

    public boolean contains(String ISBN) {
        return books.containsKey(ISBN);
    }

    public Book findBook(String ISBN) {
        return books.getOrDefault(ISBN, null);
    }

    public void addBook(Book book) {
        books.put(book.getISBN(), book);
    }

    public Collection<Book> getBooks() {
        return books.values();
    }

    public int size() {
        return books.size();
    }
}
