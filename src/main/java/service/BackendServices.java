package service;

import model.Book;
import model.BookOrder;
import model.BookstoreUser;

import java.io.File;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.CallableStatement;
import java.sql.Types;

import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class BackendServices {
    private Connection DBConnection;
    private ResourceBundle resourceBundle;
    private String jasperPath;

    /**
     * Constructs a new BackendServices
     *
     * @throws SQLException If the connection to the database failed.
     */
    public BackendServices() throws SQLException {
        resourceBundle = ResourceBundle.getBundle("services.dbuser");
        String user = resourceBundle.getString("user");
        String password = resourceBundle.getString("password");
        String connectionUrl = "jdbc:mysql://localhost:3306/BOOKSTORE?useUnicode=true&" +
                "characterEncoding=UTF-8";
        DBConnection = DriverManager.getConnection(connectionUrl, user, password);
        try {
            jasperPath = new File(getClass().getResource("/jasper/").toURI()).getPath();
        } catch (URISyntaxException e) {
            jasperPath = null;
        }
        System.out.println(jasperPath);
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
                BookstoreUser.USER_NAME_COLNAME + ", " +
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
        curBook.setMinThreshold(rs.getInt(Book.MIN_THRESHOLD_COLNAME));
        curBook.setBookTitle(rs.getString(Book.BOOK_TITLE_COLNAME));
        curBook.setCategory(rs.getString(Book.CATEGORY_COLNAME));
        curBook.setPrice(rs.getDouble(Book.PRICE_COLNAME));
        curBook.setPublicationYear(rs.getDate(Book.PUBLICATION_YEAR_COLNAME));
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

    public boolean addBook(String ISBN, String publisherName, String bookTitle,
                           int booksInStock, int minThreshold, Date publicationYear,
                           double price, String category) throws SQLException {

        if (ISBN == null || publisherName == null || bookTitle == null
                || booksInStock < 0 || minThreshold < 0 || publicationYear == null
                || price <= 0 || category == null) {
            return false;
        }
        String sqlQuery = "INSERT INTO BOOK (" +
                Book.ISBN_COLNAME + ", " +
                Book.PUBLISHER_NAME_COLNAME + ", " +
                Book.BOOK_TITLE_COLNAME + ", " +
                Book.BOOKS_IN_STOCK_COLNAME + ", " +
                Book.MIN_THRESHOLD_COLNAME + ", " +
                Book.PUBLICATION_YEAR_COLNAME + ", " +
                Book.PRICE_COLNAME + ", " +
                Book.CATEGORY_COLNAME + ") VALUES(?, ?, ?, ?, ?, ?, ?, ?)";

        PreparedStatement preparedStatement = DBConnection.prepareStatement(sqlQuery);
        preparedStatement.setString(1, ISBN);
        preparedStatement.setString(2, publisherName);
        preparedStatement.setString(3, bookTitle);
        preparedStatement.setInt(4, booksInStock);
        preparedStatement.setInt(5, minThreshold);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
        String yearStr = sdf.format(publicationYear);
        preparedStatement.setString(6, yearStr);
        preparedStatement.setDouble(7, price);
        preparedStatement.setString(8, category);

        int updateCount = preparedStatement.executeUpdate();

        return updateCount > 0;
    }

    public boolean addBook(String ISBN, String publisherName, String bookTitle,
                           int minThreshold, Date publicationYear,
                           double price, String category) throws SQLException {
        return addBook(ISBN, publisherName, bookTitle, 0, minThreshold,
                publicationYear, price, category);
    }

    public boolean modifyBook(String ISBN, String colName, String newValue) throws SQLException {
        String sqlQuery = "UPDATE BOOK SET " + colName +
                " = ? WHERE " + Book.ISBN_COLNAME + " = ?";
        PreparedStatement preparedStatement = DBConnection.prepareStatement(sqlQuery);
        preparedStatement.setString(1, newValue);
        preparedStatement.setString(2, ISBN);

        int updateCount = preparedStatement.executeUpdate();

        return updateCount > 0;
    }

    public boolean modifyBook(String ISBN, String colName, Date newYear) throws SQLException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
        String yearStr = sdf.format(newYear);

        return modifyBook(ISBN, colName, yearStr);
    }

    public boolean modifyBook(String ISBN, String colName, int newValue) throws SQLException {
        return modifyBook(ISBN, colName, Integer.toString(newValue));
    }

    public boolean modifyBook(String ISBN, String colName, double newValue) throws SQLException {
        return modifyBook(ISBN, colName, Double.toString(newValue));
    }

    public boolean deleteBook(String ISBN) throws SQLException {
        String sqlQuery = "DELETE FROM BOOK WHERE " + Book.ISBN_COLNAME + " = ?";
        PreparedStatement preparedStatement = DBConnection.prepareStatement(sqlQuery);
        preparedStatement.setString(1, ISBN);

        int updateCount = preparedStatement.executeUpdate();

        return updateCount > 0;
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
        String sqlQuery = "UPDATE BOOKSTORE_USER SET " + colName +
                " = ? WHERE " + BookstoreUser.USER_NAME_COLNAME + " = ?";
        PreparedStatement preparedStatement = DBConnection.prepareStatement(sqlQuery);
        preparedStatement.setString(1, value);
        preparedStatement.setString(2, userName);

        int updateCount = preparedStatement.executeUpdate();

        return updateCount > 0;
    }

    public boolean promote(String userName, String newRole) throws SQLException {
        return updateUser(userName, BookstoreUser.USER_GROUP_COLNAME, newRole);
    }

    public boolean updatePassword(String userName, String oldPassword, String newPassword) throws SQLException {
        String sqlQuery = "{CALL UPDATE_PASSWORD(?, ?, ?, ?, ?)}";
        CallableStatement callStatement = DBConnection.prepareCall(sqlQuery);
        callStatement.setString(1, userName);
        callStatement.setString(2, oldPassword);
        callStatement.setString(3, newPassword);
        callStatement.registerOutParameter(4, Types.BOOLEAN);
        callStatement.registerOutParameter(5, Types.VARCHAR);

        int updateCount = callStatement.executeUpdate();

        boolean success = callStatement.getBoolean(4);

        String errorMessage = callStatement.getString(5);

        if (errorMessage != null) {
            System.out.println(errorMessage);
        }

        return success;
    }

    public int getNumberOfPages(int pageSize) throws SQLException {
        return (getNumberOfBooks() - 1) / pageSize + 1;
    }

    public boolean buyBook(String userName, String ISBN, int quantity, Date purchaseDate) throws SQLException {
        String sqlQuery = "{CALL MAKE_PURCHASE(?, ?, ?, ?, ?, ?, ?, ?)}";
        CallableStatement callStatement = DBConnection.prepareCall(sqlQuery);
        callStatement.setString(1, userName);
        callStatement.setString(2, ISBN);
        callStatement.setDate(3, new java.sql.Date(purchaseDate.getTime()));
        callStatement.setInt(4, quantity);
        callStatement.registerOutParameter(5, Types.BOOLEAN);
        callStatement.registerOutParameter(6, Types.INTEGER);
        callStatement.registerOutParameter(7, Types.FLOAT);
        callStatement.registerOutParameter(8, Types.VARCHAR);

        int updateCount = callStatement.executeUpdate();

        boolean success = callStatement.getBoolean(5);

        int purchaseId = callStatement.getInt(6);

        double totalCost = callStatement.getDouble(7);

        System.out.println(totalCost);

        String errorMessage = callStatement.getString(8);

        if (errorMessage != null) {
            System.out.println(errorMessage);
        }

        return success;

    }

    public boolean buyBook(String userName, String ISBN, int quantity) throws SQLException {
        return buyBook(userName, ISBN, quantity, new Date());
    }

    public int orderBook(String ISBN, String publisherName, int quantity) throws SQLException {

        if (ISBN == null || publisherName == null || quantity <= 0) {
            return -1;
        }
        String sqlQuery = "INSERT INTO BOOK_ORDERS (" +
                BookOrder.ISBN_COLNAME +  ", " +
                BookOrder.PUBLISHER_NAME_COLNAME + ", " +
                BookOrder.QUANTITY_COLNAME + ") VALUES(?, ?, ?)";

        PreparedStatement preparedStatement = DBConnection.prepareStatement(sqlQuery,
                                                Statement.RETURN_GENERATED_KEYS);
        preparedStatement.setString(1, ISBN);
        preparedStatement.setString(2, publisherName);
        preparedStatement.setInt(3, quantity);

        int updateCount = preparedStatement.executeUpdate();

        ResultSet rs = preparedStatement.getGeneratedKeys();
        rs.next();
        return rs.getInt(1);
    }

    public boolean confirmOrder(int orderNo) throws SQLException {
        String sqlQuery = "DELETE FROM BOOK_ORDERS WHERE " +
                BookOrder.ORDER_NO_COLNAME +  " = ?";

        PreparedStatement preparedStatement = DBConnection.prepareStatement(sqlQuery);
        preparedStatement.setInt(1, orderNo);

        int updateCount = preparedStatement.executeUpdate();

        return updateCount > 0;
    }

    public boolean printJasperReport(String jasperFile, String outputFile, String reportTitle) {

        if (jasperPath == null) {
            return false;
        }

        if (outputFile == null) {
            outputFile = jasperFile + JasperReportService.PDF_EXTENSION;
        }

        jasperFile = jasperPath + File.separator + jasperFile + JasperReportService.JASPER_EXTENSION;

        JasperReportService.printReport(DBConnection, reportTitle,
                jasperFile, outputFile);
        return false;
    }

    public boolean printLastMonthReport(String outputFile) {
        String reportLastMonthFileName = "report-last-month-sales";
        String reportLastMonthTitle = "Last Month Sales";

        return printJasperReport(reportLastMonthFileName, outputFile, reportLastMonthTitle);
    }

    public boolean printTopSellingBooks(String outputFile) {
        String reportLastMonthFileName = "report-top-ten-selling-books";
        String reportLastMonthTitle = "Top Ten Selling Books in The Past Three Months";

        return printJasperReport(reportLastMonthFileName, outputFile, reportLastMonthTitle);
    }

    public boolean printTopUsers(String outputFile) {
        String reportLastMonthFileName = "report-top-five-customers";
        String reportLastMonthTitle = "Top Five Users in The Past Three Months";

        return printJasperReport(reportLastMonthFileName, outputFile, reportLastMonthTitle);
    }

    public static void main(String[] args) {
        try {
            BackendServices sys = new BackendServices();
//            sys.register("Barry", "barry@bmail.bom", "bassword", "manager");
//            sys.register("b4", "b2@a.c", "pw", "customer");
            String usergroup = sys.login("Barry", "bassword").getUserGroup();
            System.out.println(usergroup);


            usergroup = sys.login("b4", "pw").getUserGroup();
            System.out.println(usergroup);
            System.out.println(sys.updatePassword("b4", "pw", "pw"));
            for (Book book : sys.getBooks(1, 3)) {
                System.out.println(book.getBookTitle() + "\t" + book.getISBN() + "\t" + book.getCategory() + "\t" + book.getPublisherName() + "\t" + book.getBooksInStock());
            }
            System.out.println("" + sys.getNumberOfBooks() + " " + sys.getNumberOfPages(3));
            System.out.println(sys.confirmOrder(sys.orderBook("1234567890126", "Ahmed Walid", 5000)));
            System.out.println("\n" + sys.buyBook("b4","1234567890126", 500) + "\n");
            for (Book book : sys.findBooks(1, 5, Book.PUBLISHER_NAME_COLNAME, "Ahmed Walid")) {
                System.out.println(book.getBookTitle() + "\t" + book.getISBN() + "\t" + book.getCategory() + "\t" + book.getPublisherName() + "\t" + book.getBooksInStock());
            }
            sys.printLastMonthReport(null);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
