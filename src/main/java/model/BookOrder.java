package model;

import java.util.Objects;

public class BookOrder {

    public static final String ISBN_COLNAME = "ISBN";
    public static final String QUANTITY_COLNAME = "QUANTITY";
    public static final String PUBLISHER_NAME_COLNAME = "PUBLISHER_NAME";
    public static final String ORDER_NO_COLNAME = "ORDER_NO";

    private int orderNo;
    private String publisherName;
    private String ISBN;
    private int quantity;

    public BookOrder() {

    }

    public int getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(int orderNo) {
        this.orderNo = orderNo;
    }

    public String getPublisherName() {
        return publisherName;
    }

    public void setPublisherName(String publisherName) {
        this.publisherName = publisherName;
    }

    public String getISBN() {
        return ISBN;
    }

    public void setISBN(String ISBN) {
        this.ISBN = ISBN;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BookOrder bookOrder = (BookOrder) o;
        return orderNo == bookOrder.orderNo;
    }

    @Override
    public int hashCode() {

        return Objects.hash(orderNo);
    }
}
