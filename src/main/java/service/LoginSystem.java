package service;

import model.Book;
import model.BookstoreUser;

import java.sql.*;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class LoginSystem {
    private Connection DBConnection;
    private ResourceBundle resourceBundle;
    /**
     * Constructs a new LoginSystem
     * @throws SQLException If the connection to the database failed.
     */
    public LoginSystem() throws SQLException {
        resourceBundle = ResourceBundle.getBundle("services.dbuser");
        String user = resourceBundle.getString("user");
        String password = resourceBundle.getString("password");
        String connectionUrl = "jdbc:mysql://localhost:3306/BOOKSTORE?useUnicode=true&" +
                "characterEncoding=UTF-8";
        DBConnection = DriverManager.getConnection(connectionUrl, user, password);
    }

    private BookstoreUser getUser(ResultSet rs) throws SQLException {
        BookstoreUser curUser = new BookstoreUser();
        curUser.setUserName(rs.getString(BookstoreUser.USER_NAME_COLNAME));
        curUser.setEmail(rs.getString(BookstoreUser.EMAIL_COLNAME));
        curUser.setUserGroup(rs.getString(BookstoreUser.USER_GROUP_COLNAME));
        curUser.getProfile().setFirstName(rs.getString(BookstoreUser.UserProfile.FIRST_NAME_COLNAME));
        curUser.getProfile().setFirstName(rs.getString(BookstoreUser.UserProfile.LAST_NAME_COLNAME));
        curUser.getProfile().setNationality(rs.getString(BookstoreUser.UserProfile.NATIONALITY_COLNAME));
//                curUser.getProfile().setBirthDate(rs.getString(BookstoreUser.UserProfile.));
        return curUser;

    }

    public BookstoreUser login(String userName, String password) throws SQLException {
        if (userName == null || password == null) {
            return null;
        }
        String sqlQuery = "SELECT * FROM BOOKSTORE_USER WHERE USER_NAME=?";
        PreparedStatement preparedStatement = DBConnection.prepareStatement(sqlQuery);
        preparedStatement.setString(1, userName);
        ResultSet rs = preparedStatement.executeQuery();

        while (rs.next()) {
            String userPassword = rs.getString(BookstoreUser.PASSWORD_COLNAME);
            if (!userPassword.equals(password)) {
                return null;
            } else {
                return getUser(rs);
            }
        }
        return null;
    }

    public BookstoreUser getUserProfile(String userName) throws SQLException {
        if (userName == null) {
            return null;
        }
        String sqlQuery = "SELECT * FROM BOOKSTORE_USER WHERE USER_NAME=?";
        PreparedStatement preparedStatement = DBConnection.prepareStatement(sqlQuery);
        preparedStatement.setString(1, userName);
        ResultSet rs = preparedStatement.executeQuery();

        while (rs.next()) {
            String userPassword = rs.getString(BookstoreUser.PASSWORD_COLNAME);
            return getUser(rs);
        }
        return null;
    }

    public BookstoreUser register(String userName, String email, String password, String userGroup) throws SQLException {
        if (userName == null || password == null || email == null || userGroup == null) {
            return null;
        }
        String sqlQuery = "INSERT INTO BOOKSTORE_USER (" +
                 BookstoreUser.USER_NAME_COLNAME +  ", " +
                 BookstoreUser.EMAIL_COLNAME + ", " +
                 BookstoreUser.PASSWORD_COLNAME + ", " +
                 BookstoreUser.USER_GROUP_COLNAME + ") VALUES(?, ?, ?, ?)";

        PreparedStatement preparedStatement = DBConnection.prepareStatement(sqlQuery);
        preparedStatement.setString(1, userName);
        preparedStatement.setString(2, email);
        preparedStatement.setString(3, password);
        preparedStatement.setString(4, userGroup);

        int updateCount = preparedStatement.executeUpdate();

        BookstoreUser curUser = new BookstoreUser();
        curUser.setUserGroup(userGroup);
        curUser.setEmail(email);
        curUser.setUserName(userName);

        return curUser;
    }

    private Book getBook(ResultSet rs) throws SQLException {
        Book curBook = new Book();
        curBook.setISBN(rs.getString(Book.ISBN_COLNAME));
        curBook.setPublisherName(rs.getString(Book.PUBLISHER_NAME_COLNAME));
        curBook.setBooksInStock(rs.getInt(Book.BOOKS_IN_STOCK_COLNAME));
        curBook.setBookTitle(rs.getString(Book.BOOK_TITLE_COLNAME));
        curBook.setCategory(rs.getString(Book.CATEGORY_COLNAME));
        curBook.setPrice(rs.getDouble(Book.PRICE_COLNAME));
        // TODO: Set Publication Year.
//            curBook.setPublicationYear(rs.get);
        return curBook;
    }

    public Book getBook(String ISBN) throws SQLException {
        String sqlQuery = "SELECT * FROM BOOK WHERE " + Book.ISBN_COLNAME + " = ?";
        PreparedStatement preparedStatement = DBConnection.prepareStatement(sqlQuery);
        preparedStatement.setString(1, ISBN);

        ResultSet rs = preparedStatement.executeQuery();
        if (rs.next()) {
            return getBook(rs);
        } else {
            return null;
        }
    }

    public ArrayList<Book> getBooks(int pageNumber, int pageSize) throws SQLException {
        String sqlQuery = "SELECT * FROM BOOK LIMIT ? OFFSET ?";
        PreparedStatement preparedStatement = DBConnection.prepareStatement(sqlQuery);
        preparedStatement.setInt(1, pageSize);
        preparedStatement.setInt(2, (pageNumber - 1) * pageSize);



        ResultSet rs = preparedStatement.executeQuery();
        System.out.println(preparedStatement.toString());

        ArrayList<Book> result = new ArrayList<>();

        while (rs.next()) {
            Book curBook = getBook(rs);
            result.add(curBook);
        }
        return result;
    }

    public ArrayList<Book> findBooks(int pageNumber, int pageSize, String colName, String value) throws SQLException {
        String sqlQuery = "SELECT * FROM BOOK" +
                " WHERE " + colName + " = ?" +
                " LIMIT ? OFFSET ?";
        PreparedStatement preparedStatement = DBConnection.prepareStatement(sqlQuery);
        preparedStatement.setString(1, value);
        preparedStatement.setInt(2, pageSize);
        preparedStatement.setInt(3, (pageNumber - 1) * pageSize);



        ResultSet rs = preparedStatement.executeQuery();
        System.out.println(preparedStatement.toString());

        ArrayList<Book> result = new ArrayList<>();

        while (rs.next()) {
            Book curBook = getBook(rs);
            result.add(curBook);
        }
        return result;
    }

    public int getNumberOfBooks() throws SQLException {
        Statement statement = DBConnection.createStatement();
        ResultSet rs = statement.executeQuery("SELECT COUNT(*) FROM BOOK");

        if (rs.next()) {
            return rs.getInt(1);
        }

        return -1;
    }

    public boolean updateUser(String userName, String colName, String value) throws SQLException {
        String sqlQuery = "UPDATE USER SET " + colName +
                " = ? WHERE " + BookstoreUser.USER_NAME_COLNAME + " = ?";
        PreparedStatement preparedStatement = DBConnection.prepareStatement(sqlQuery);
        preparedStatement.setString(1, value);
        preparedStatement.setString(1, userName);

        int updateCount = preparedStatement.executeUpdate();

        return updateCount > 0;
    }

    public boolean updatePassword(String userName, String oldPassword, String newPassword) throws SQLException {
        BookstoreUser curUser = login(userName, oldPassword);
        if (curUser != null) {
            return updateUser(userName, BookstoreUser.PASSWORD_COLNAME, newPassword);
        }
        return false;
    }

    public int getNumberOfPages(int pageSize) throws SQLException {
        return (getNumberOfBooks() - 1) / pageSize + 1;
    }

    public boolean buyBook(String ISBN, int quantity) throws SQLException {
        String sqlQuery = "UPDATE BOOK SET " + Book.BOOKS_IN_STOCK_COLNAME +
                " = " + Book.BOOKS_IN_STOCK_COLNAME + " - ?"
                + " WHERE " + Book.ISBN_COLNAME + " = ?";
        PreparedStatement preparedStatement = DBConnection.prepareStatement(sqlQuery);

        preparedStatement.setInt(1, quantity);
        preparedStatement.setString(2, ISBN);

        try {
            int updateCount = preparedStatement.executeUpdate();
            return updateCount > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

    }

    public static void main(String[] args) {
        try {
            LoginSystem sys = new LoginSystem();
//            sys.register("Barry", "barry@bmail.bom", "bassword", "manager");
//            sys.register("b4", "b2@a.c", "pw", "customer");
            String usergroup = sys.login("Barry", "bassword").getUserGroup();
            System.out.println(usergroup);
            usergroup = sys.login("b4", "pw").getUserGroup();
            System.out.println(usergroup);
            for (Book book : sys.getBooks(1, 3)) {
                System.out.println(book.getBookTitle() + "\t" + book.getISBN() + "\t" + book.getCategory() + "\t" + book.getPublisherName());
            }
            System.out.println("" + sys.getNumberOfBooks() + " " + sys.getNumberOfPages(3));
//            System.out.println(sys.buyBook("1234567890127", 50));
            for (Book book : sys.findBooks(1, 5, Book.PUBLISHER_NAME_COLNAME, "Ahmed Walid")) {
                System.out.println(book.getBookTitle() + "\t" + book.getISBN() + "\t" + book.getCategory() + "\t" + book.getPublisherName());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
