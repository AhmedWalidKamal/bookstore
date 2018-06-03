package model;

import java.util.Date;
import java.util.Objects;

public class BookstoreUser {

    public static final String USER_NAME_COLNAME = "USER_NAME";
    public static final String EMAIL_COLNAME = "EMAIL";
    public static final String PASSWORD_COLNAME = "PASSWORD";
    public static final String USER_GROUP_COLNAME = "USER_GROUP";

    private String userName;
    private String email;
    private String userGroup;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BookstoreUser that = (BookstoreUser) o;
        return Objects.equals(userName, that.userName);
    }

    @Override
    public int hashCode() {

        return Objects.hash(userName);
    }

    public class UserProfile {

        public static final String FIRST_NAME_COLNAME = "FIRST_NAME";
        public static final String LAST_NAME_COLNAME = "LAST_NAME";
        public static final String BIRTH_DATE_COLNAME = "BIRTH_DATE";
        public static final String NATIONALITY_COLNAME = "NATIONALITY";

        private String firstName;
        private String lastName;
        private Date birthDate;
        private String nationality;

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

        public Date getBirthDate() {
            return birthDate;
        }

        public void setBirthDate(Date birthDate) {
            this.birthDate = birthDate;
        }

        public String getNationality() {
            return nationality;
        }

        public void setNationality(String nationality) {
            this.nationality = nationality;
        }
    }
}
