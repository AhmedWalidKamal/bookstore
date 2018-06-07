package service;

import javafx.stage.Stage;
import model.*;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;

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

        URL url = getClass().getResource("/jasper");
        File file = null;

        if (url.getProtocol().equals("jar")) {
            jasperPath = "/jasper";
        } else {
            try {
                file = new File(url.toURI());
            } catch (URISyntaxException e) {
                file = new File(url.getPath());
            } catch (IllegalArgumentException e) {
                file = new File(url.toExternalForm());
            } finally {
                jasperPath = file.getAbsolutePath();
            }
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

    private Book getBook(Book curBook, ResultSet rs) throws SQLException {
        if (curBook == null) {
            curBook = new Book();
            curBook.setISBN(rs.getString(Book.ISBN_COLNAME));
            curBook.setPublisherName(rs.getString(Book.PUBLISHER_NAME_COLNAME));
            curBook.setBooksInStock(rs.getInt(Book.BOOKS_IN_STOCK_COLNAME));
            curBook.setMinThreshold(rs.getInt(Book.MIN_THRESHOLD_COLNAME));
            curBook.setBookTitle(rs.getString(Book.BOOK_TITLE_COLNAME));
            curBook.setCategory(rs.getString(Book.CATEGORY_COLNAME));
            curBook.setPrice(rs.getDouble(Book.PRICE_COLNAME));
            curBook.setPublicationYear(rs.getDate(Book.PUBLICATION_YEAR_COLNAME));
        }
        BookAuthor author = new BookAuthor();
        author.setISBN(curBook.getISBN());
        author.setAuthorName(rs.getString(BookAuthor.AUTHOR_NAME_COLNAME));
        if (author.getAuthorName() != null) {
            curBook.getAuthors().add(author);
        }
        return curBook;
    }

    public BookList getBooks(int pageNumber, int pageSize, String orderCol, boolean ascending) throws SQLException {

        if (!Book.columns.contains(orderCol)) {
            return null;
        }

        String sqlQuery = "SELECT * FROM (SELECT * FROM BOOK ORDER BY BOOK.`" + orderCol + "`";

        if (ascending) {
            sqlQuery += " ASC";
        } else {
            sqlQuery += " DESC";
        }

        sqlQuery += " LIMIT ? OFFSET ?) AS T" +
                " LEFT OUTER JOIN BOOK_AUTHORS ON BOOK_AUTHORS."
                + BookAuthor.ISBN_COLNAME  + " = T." + Book.ISBN_COLNAME;

        PreparedStatement preparedStatement = DBConnection.prepareStatement(sqlQuery);
        preparedStatement.setInt(1, pageSize);
        preparedStatement.setInt(2, (pageNumber - 1) * pageSize);


        ResultSet rs = preparedStatement.executeQuery();
        System.out.println(preparedStatement.toString());

        BookList books = new BookList();

        while (rs.next()) {
            String ISBN = rs.getString(Book.ISBN_COLNAME);
            Book curBook = books.findBook(ISBN);
            curBook = getBook(curBook, rs);
            books.addBook(curBook);
        }
        return books;
    }

    private void buildQueryCondition(StringBuilder sqlQuery, Map<String, ArrayList<String>> colValues) {
        int cnt = 0;
        for (String colName : colValues.keySet()) {
            cnt++;
            sqlQuery.append(colName);
            sqlQuery.append(" = ?");
            for (int i = 0 ; i < colValues.get(colName).size() - 1 ; i++) {
                sqlQuery.append(" OR ");
                sqlQuery.append(colName);
                sqlQuery.append(" = ?");
            }
            if (cnt != colValues.size()) {
                sqlQuery.append(" OR ");
            }
        }

    }

    public BookList findBooks(int pageNumber, int pageSize,
                                     Map<String, ArrayList<String>> colValues,
                                     String orderCol, boolean ascending) throws SQLException {
        Map<String, ArrayList<String>> authorConditions = new HashMap<>();
        Map<String, ArrayList<String>> bookConditions = new HashMap<>();

        if (!Book.columns.contains(orderCol)) {
            return null;
        }

        final String alias = "T";
        for (String colName : colValues.keySet()) {
            if (colName.equals(BookAuthor.AUTHOR_NAME_COLNAME)) {
                authorConditions.put("BOOK_AUTHORS.`" + colName + "`", colValues.get(colName));
            } else {
                if (!Book.columns.contains(colName)) {
                    return null;
                }
                bookConditions.put("BOOK.`" + colName + "`", colValues.get(colName));
                authorConditions.put(alias + ".`" + colName + "`", colValues.get(colName));
            }
        }

        StringBuilder sqlQuery = new StringBuilder();
        sqlQuery.append("SELECT * FROM (SELECT * FROM BOOK"); /* +
                "` WHERE ");*/
        if (!bookConditions.isEmpty()) {
            sqlQuery.append(" WHERE ");
            buildQueryCondition(sqlQuery, bookConditions);
        }

        sqlQuery.append(" ORDER BY BOOK.`" + orderCol + "`");
        if (ascending) {
            sqlQuery.append(" ASC");
        } else {
            sqlQuery.append(" DESC");
        }
        sqlQuery.append(" LIMIT ? OFFSET ?");
        sqlQuery.append(" ) AS " + alias
                + " LEFT OUTER JOIN BOOK_AUTHORS ON BOOK_AUTHORS.`"
                + BookAuthor.ISBN_COLNAME  + "` = " + alias + ".`" + Book.ISBN_COLNAME + "`");

        if (!authorConditions.isEmpty()) {
            sqlQuery.append(" WHERE ");
            buildQueryCondition(sqlQuery, authorConditions);
        }

        PreparedStatement preparedStatement = DBConnection.prepareStatement(sqlQuery.toString());
        int i = 1;
        for (String colName : bookConditions.keySet()) {
            for (int j = 0; j < bookConditions.get(colName).size() ; i++, j++) {
                preparedStatement.setString(i, bookConditions.get(colName).get(j));
            }
        }

        preparedStatement.setInt(i++, pageSize);
        preparedStatement.setInt(i++, (pageNumber - 1) * pageSize);

        for (String colName : authorConditions.keySet()) {
            for (int j = 0; j < authorConditions.get(colName).size() ; i++, j++) {
                preparedStatement.setString(i, authorConditions.get(colName).get(j));
            }
        }

        ResultSet rs = preparedStatement.executeQuery();
        System.out.println(preparedStatement.toString());

        BookList books = new BookList();

        while (rs.next()) {
            String ISBN = rs.getString(Book.ISBN_COLNAME);
            Book curBook = books.findBook(ISBN);
            curBook = getBook(curBook, rs);
            books.addBook(curBook);
        }
        return books;
    }

    public BookList findBooks(int pageNumber, int pageSize,
                              String colName, ArrayList<String> values,
                              String orderCol, boolean ascending) throws SQLException {
        Map<String, ArrayList<String>> colValues = new HashMap<>();
        colValues.put(colName, values);
        return findBooks(pageNumber, pageSize, colValues, orderCol, ascending);
    }

    public BookList findBooks(int pageNumber, int pageSize,
                                     String colName, String value,
                              String orderCol, boolean ascending) throws SQLException {
        ArrayList<String> values = new ArrayList<>();
        values.add(value);
        return findBooks(pageNumber, pageSize, colName, values, orderCol, ascending);
    }


    public Book getBook(String ISBN) throws SQLException {
        BookList books = findBooks(1, 1, Book.ISBN_COLNAME, ISBN,
                Book.ISBN_COLNAME, true);

        return books.findBook(ISBN);
    }

    public boolean addBook(String ISBN, String publisherName, String bookTitle,
                           int booksInStock, int minThreshold, Date publicationYear,
                           double price, String category) throws SQLException {

        if (ISBN == null || publisherName == null || bookTitle == null
                || booksInStock < 0 || minThreshold < 0 || publicationYear == null
                || price <= 0 || category == null) {
            return false;
        }
        String sqlQuery = "INSERT INTO BOOK (`" +
                Book.ISBN_COLNAME + "`, `" +
                Book.PUBLISHER_NAME_COLNAME + "`, `" +
                Book.BOOK_TITLE_COLNAME + "`, `" +
                Book.BOOKS_IN_STOCK_COLNAME + "`, `" +
                Book.MIN_THRESHOLD_COLNAME + "`, `" +
                Book.PUBLICATION_YEAR_COLNAME + "`, `" +
                Book.PRICE_COLNAME + "`, `" +
                Book.CATEGORY_COLNAME + "`) VALUES(?, ?, ?, ?, ?, ?, ?, ?)";

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
        String sqlQuery = "UPDATE BOOK SET `" + colName +
                "` = ? WHERE " + Book.ISBN_COLNAME + " = ?";
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
        String reportInputFile = "report-last-month-sales";
        String reportTitle = "Last Month Sales";

        return printJasperReport(reportInputFile, outputFile, reportTitle);
    }

    public boolean printTopSellingBooks(String outputFile) {
        String reportInputFile = "report-top-ten-selling-books";
        String reportTitle = "Top Ten Selling Books in The Past Three Months";

        return printJasperReport(reportInputFile, outputFile, reportTitle);
    }

    public boolean printTopUsers(String outputFile) {
        String reportInputFile = "report-top-five-customers";
        String reportTitle = "Top Five Users in The Past Three Months";

        return printJasperReport(reportInputFile, outputFile, reportTitle);
    }

    private void showJasperReport(Stage primaryStage, String reportTitle,
                                        String inputFile) {
        HashMap<String, Object> parametersMap = new HashMap<>();
        parametersMap.put("Report Title", reportTitle);

        JasperReportService.showJasperReport(primaryStage, reportTitle,
                jasperPath + File.separator + inputFile + JasperReportService.JASPER_EXTENSION,
                parametersMap, DBConnection);
    }

    public void showLastMonthReport(Stage primaryStage) {
        String reportInputFile = "report-last-month-sales";
        String reportTitle = "Last Month Sales";

        showJasperReport(primaryStage, reportTitle, reportInputFile);
    }

    public void showTopSellingBooks(Stage primaryStage) {
        String reportInputFile = "report-top-ten-selling-books";
        String reportTitle = "Top Ten Selling Books in The Past Three Months";

        showJasperReport(primaryStage, reportTitle, reportInputFile);
    }

    public void showTopUsers(Stage primaryStage) {
        String reportInputFile = "report-top-five-customers";
        String reportTitle = "Top Five Users in The Past Three Months";

        showJasperReport(primaryStage, reportTitle, reportInputFile);
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
            for (Book book : sys.getBooks(1, 5, Book.BOOKS_IN_STOCK_COLNAME, true).getBooks()) {
                System.out.println(book.getBookTitle() + "\t" + book.getISBN() + "\t" + book.getCategory() + "\t" + book.getPublisherName() + "\t" + book.getBooksInStock() + "\t" + Arrays.toString(book.getAuthors().toArray()));
            }
            System.out.println("" + sys.getNumberOfBooks() + " " + sys.getNumberOfPages(3));
            System.out.println(sys.confirmOrder(sys.orderBook("1234567890126", "Ahmed Walid", 5000)));
            System.out.println("\n" + sys.buyBook("b4","1234567890126", 500) + "\n");

            for (Book book : sys.findBooks(1, 5, Book.PUBLISHER_NAME_COLNAME, "Ahmed Walid", Book.ISBN_COLNAME, false).getBooks()) {
                System.out.println(book.getBookTitle() + "\t" + book.getISBN() + "\t" + book.getCategory() + "\t" + book.getPublisherName() + "\t" + book.getBooksInStock() + "\t" + Arrays.toString(book.getAuthors().toArray()));
            }

            Map<String, ArrayList<String>> colValues = new HashMap<>();
            colValues.put("ISBN", new ArrayList<>(Arrays.asList("1234567890123", "1234567890127")));
            colValues.put("BOOKS_IN_STOCK", new ArrayList<>(Arrays.asList("0")));
            colValues.put(BookAuthor.AUTHOR_NAME_COLNAME, new ArrayList<>(Arrays.asList("A", "B")));
            for (Book book : sys.findBooks(1, 5, colValues, Book.ISBN_COLNAME, true).getBooks()) {
                System.out.println(book.getBookTitle() + "\t" + book.getISBN() + "\t" + book.getCategory() + "\t" + book.getPublisherName() + "\t" + book.getBooksInStock() + "\t" + Arrays.toString(book.getAuthors().toArray()));
            }
            Book book = sys.getBook("1234567890125");
            System.out.println(book.getBookTitle() + "\t" + book.getISBN() + "\t" + book.getCategory() + "\t" + book.getPublisherName() + "\t" + book.getBooksInStock() + "\t" + Arrays.toString(book.getAuthors().toArray()));

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
