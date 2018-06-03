package model;

import java.util.Objects;

public class Publisher {

    public static final String PUBLISHER_NAME_COLNAME = "PUBLISHER_NAME";
    public static final String ADDRESS_COLNAME = "ADDRESS";
    public static final String TELEPHONE_NUMBER_COLNAME = "TELEPHONE_NUMBER";

    private String publisherName;
    private String address;
    private String telephoneNumber;

    public Publisher() {

    }

    public String getPublisherName() {
        return publisherName;
    }

    public void setPublisherName(String publisherName) {
        this.publisherName = publisherName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTelephoneNumber() {
        return telephoneNumber;
    }

    public void setTelephoneNumber(String telephoneNumber) {
        this.telephoneNumber = telephoneNumber;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Publisher publisher = (Publisher) o;
        return Objects.equals(publisherName, publisher.publisherName);
    }

    @Override
    public int hashCode() {

        return Objects.hash(publisherName);
    }
}
