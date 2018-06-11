package model;

import javafx.scene.image.Image;

import java.time.LocalDate;
import java.util.Date;
import java.util.Objects;

public class BookstoreUser {

    public static final String USER_ID_COLNAME = "USER_ID";
    public static final String USER_NAME_COLNAME = "USER_NAME";
    public static final String EMAIL_COLNAME = "EMAIL";
    public static final String PASSWORD_COLNAME = "PASSWORD";
    public static final String USER_GROUP_COLNAME = "USER_GROUP";

    private int userID;
    private String userName;
    private String email;
    private String userGroup;

    private Cart cart;

    private UserProfile profile;

    public BookstoreUser() {
        profile = new UserProfile();
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUserGroup() {
        return userGroup;
    }

    public void setUserGroup(String userGroup) {
        this.userGroup = userGroup;
    }

    public UserProfile getProfile() {
        return profile;
    }

    public void setProfile(UserProfile profile) {
        this.profile = profile;
    }

    public Cart getCart() {
        return cart;
    }

    public void setCart(Cart cart) {
        this.cart = cart;
    }

    public int getUserID() {

        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BookstoreUser that = (BookstoreUser) o;
        return userID == that.userID;
    }

    @Override
    public int hashCode() {

        return Objects.hash(userID);
    }

    public class UserProfile {

        public static final String FIRST_NAME_COLNAME = "FIRST_NAME";
        public static final String LAST_NAME_COLNAME = "LAST_NAME";
        public static final String BIRTH_DATE_COLNAME = "BIRTH_DATE";
        public static final String PHONE_NUMBER_COLNAME = "PHONE_NUMBER";
        public static final String SHIPPING_ADDRESS_COLNAME = "SHIPPING_ADDRESS";
        public static final String USER_PHOTO_PATH_COLNAME = "USER_PHOTO_PATH";

        private String firstName;
        private String lastName;
        private LocalDate birthDate;
        private String phoneNumber;
        private String shippingAddress;
        private String userPhotoPath;


        public UserProfile() {

        }

        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public void setLastName(String lastName) {
            this.lastName = lastName;
        }

        public LocalDate getBirthDate() {
            return birthDate;
        }

        public void setBirthDate(LocalDate birthDate) {
            this.birthDate = birthDate;
        }

        public String getPhoneNumber() {
            return phoneNumber;
        }

        public void setPhoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
        }

        public String getShippingAddress() {
            return shippingAddress;
        }

        public void setShippingAddress(String shippingAddress) {
            this.shippingAddress = shippingAddress;
        }

        public String getUserPhotoPath() {
            return userPhotoPath;
        }

        public void setUserAvatarPath(String userPhotoPath) {
            this.userPhotoPath = userPhotoPath;
        }


    }
}
