package service;

import javafx.stage.Stage;
import model.*;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;

import java.sql.*;
import java.time.LocalDate;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.*;

public class BackendServices {
    private Connection DBConnection;
    private String jasperPath;

    /**
     * Constructs a new BackendServices
     *
     * @throws SQLException If the connection to the database failed.
     */
    public BackendServices() throws SQLException {
        final ResourceBundle resourceBundle = ResourceBundle.getBundle("services.dbuser");
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

    public static void main(String[] args) {
        try {
            BackendServices sys = new BackendServices();
//            sys.register("Barry", "barry@bmail.bom", "bassword", "manager");
//            sys.register("b4", "b2@a.c", "pw", "customer");
            String firstName = sys.login("Barry", "bassword").getProfile().getFirstName();
            System.out.println(firstName);

            System.out.println(sys.updatePassword(1, "pw", "pw"));

            for (Book book : sys.getBooks(1, 5, Book.BOOKS_IN_STOCK_COLNAME, true).getBooks()) {
                System.out.println(book.getBookTitle() + "\t" + book.getISBN() + "\t" + book.getCategory() + "\t" + book.getPublisherName() + "\t" + book.getBooksInStock() + "\t" + Arrays.toString(book.getAuthors().toArray()));
            }
            System.out.println("" + sys.getNumberOfBooks() + " " + sys.getNumberOfPages(3));
            System.out.println("\n" + sys.buyBook(1, "1234567890126", 500) + "\n");
            System.out.println(sys.confirmOrder(sys.orderBook("1234567890126", 5000)));

            for (Book book : sys.findBooks(1, 5, Book.PUBLISHER_NAME_COLNAME, "Ahmed Walid", Book.ISBN_COLNAME, false, true).getBooks()) {
                System.out.println(book.getBookTitle() + "\t" + book.getISBN() + "\t" + book.getCategory() + "\t" + book.getPublisherName() + "\t" + book.getBooksInStock() + "\t" + Arrays.toString(book.getAuthors().toArray()));
            }

            LinkedHashMap<String, ArrayList<String>> colValues = new LinkedHashMap<>();
            colValues.put("ISBN", new ArrayList<>(Arrays.asList("1234567890123", "1234567890127")));
            colValues.put("BOOKS_IN_STOCK", new ArrayList<>(Arrays.asList("0")));
            colValues.put(BookAuthor.AUTHOR_NAME_COLNAME, new ArrayList<>(Arrays.asList("A", "B")));
            for (Book book : sys.findBooks(1, 5, colValues, Book.ISBN_COLNAME, true, true).getBooks()) {
                System.out.println(book.getBookTitle() + "\t" + book.getISBN() + "\t" + book.getCategory() + "\t" + book.getPublisherName() + "\t" + book.getBooksInStock() + "\t" + Arrays.toString(book.getAuthors().toArray()));
            }
            Book bookz = sys.getBook("1234567890125");
            System.out.println(bookz.getBookTitle() + "\t" + bookz.getISBN() + "\t" + bookz.getCategory() + "\t" + bookz.getPublisherName() + "\t" + bookz.getBooksInStock() + "\t" + Arrays.toString(bookz.getAuthors().toArray()));

            LinkedHashMap<String, String> userUpdates = new LinkedHashMap<>();
            userUpdates.put(BookstoreUser.UserProfile.FIRST_NAME_COLNAME, "Ahmed");
            userUpdates.put(BookstoreUser.UserProfile.LAST_NAME_COLNAME, "Walid");
            sys.updateUser(1, userUpdates);


            System.out.println("Book Orders: ");
            for (BookOrder order : sys.getOrders(1, 5)) {
                System.out.println(order.getOrderNo() + "\t" + order.getISBN() + "\t" + order.getPublisherName() + "\t" + order.getQuantity());
            }

            System.out.println("Books that contain Sleep");
            colValues.clear();
            colValues.put(Book.BOOK_TITLE_COLNAME, new ArrayList<>(Arrays.asList("Sleep")));
            for (Book book : sys.findBooks(1, 5, colValues, Book.ISBN_COLNAME, false, true).getBooks()) {
                System.out.println(book.getBookTitle() + "\t" + book.getISBN() + "\t" + book.getCategory() + "\t" + book.getPublisherName() + "\t" + book.getBooksInStock() + "\t" + Arrays.toString(book.getAuthors().toArray()));
            }

            BookList books = sys.getBooks(1, 1, "ISBN", true);
            System.out.println(books.hasNext());
            System.out.println(books.hasPrev());

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private BookstoreUser getUser(ResultSet rs) throws SQLException {
        BookstoreUser curUser = new BookstoreUser();
        curUser.setUserID(rs.getInt(BookstoreUser.USER_ID_COLNAME));
        curUser.setUserName(rs.getString(BookstoreUser.USER_NAME_COLNAME));
        curUser.setEmail(rs.getString(BookstoreUser.EMAIL_COLNAME));
        curUser.setUserGroup(rs.getString(BookstoreUser.USER_GROUP_COLNAME));
        curUser.getProfile().setFirstName(rs.getString(BookstoreUser.UserProfile.FIRST_NAME_COLNAME));
        curUser.getProfile().setLastName(rs.getString(BookstoreUser.UserProfile.LAST_NAME_COLNAME));
        curUser.getProfile().setPhoneNumber(rs.getString(BookstoreUser.UserProfile.PHONE_NUMBER_COLNAME));
        curUser.getProfile().setShippingAddress(rs.getString(BookstoreUser.UserProfile.SHIPPING_ADDRESS_COLNAME));
        Calendar gmt = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        java.sql.Date birthDate = rs.getDate(BookstoreUser.UserProfile.BIRTH_DATE_COLNAME, gmt);

        curUser.getProfile().setBirthDate(birthDate == null ? null : birthDate.toLocalDate());
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

        if (rs.next()) {
            String userPassword = rs.getString(BookstoreUser.PASSWORD_COLNAME);
            if (!userPassword.equals(password)) {
                return null;
            } else {
                return getUser(rs);
            }
        }
        return null;
    }

    public BookstoreUser getUserData(String userName) throws SQLException {
        if (userName == null) {
            return null;
        }
        String sqlQuery = "SELECT * FROM BOOKSTORE_USER WHERE USER_NAME=?";
        PreparedStatement preparedStatement = DBConnection.prepareStatement(sqlQuery);
        preparedStatement.setString(1, userName);
        ResultSet rs = preparedStatement.executeQuery();

        if (rs.next()) {
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

        ResultSet rs = preparedStatement.getGeneratedKeys();

        BookstoreUser curUser = new BookstoreUser();
        curUser.setUserGroup(userGroup);
        curUser.setEmail(email);
        curUser.setUserName(userName);
        curUser.setUserID(rs.getInt(1));

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

            Calendar gmt = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
            java.sql.Date publicationYear = rs.getDate(Book.PUBLICATION_YEAR_COLNAME, gmt);
            curBook.setPublicationYear(publicationYear == null ? null : publicationYear.toLocalDate());
            curBook.setImagePath(rs.getString(Book.IMAGE_PATH_COLNAME));
            curBook.setRating(rs.getDouble(Book.RATING_COLNAME));
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
                + BookAuthor.ISBN_COLNAME + " = T." + Book.ISBN_COLNAME;

        PreparedStatement preparedStatement = DBConnection.prepareStatement(sqlQuery);
        preparedStatement.setInt(1, pageSize + 1);
        preparedStatement.setInt(2, (pageNumber - 1) * pageSize);


        ResultSet rs = preparedStatement.executeQuery();
        System.out.println(preparedStatement.toString());

        BookList books = new BookList();
        books.setPrev(pageNumber > 1);

        while (rs.next()) {
            String ISBN = rs.getString(Book.ISBN_COLNAME);
            if (books.size() == pageSize && !books.contains(ISBN)) {
                books.setNext(true);
                continue;
            }
            Book curBook = books.findBook(ISBN);
            curBook = getBook(curBook, rs);
            books.addBook(curBook);
        }
        return books;
    }

    private void buildQueryCondition(StringBuilder sqlQuery,
                                     LinkedHashMap<String, ArrayList<String>> colValues,
                                     boolean useLikeQuery) {
        int cnt = 0;
        String  bookTitleSuffix = Book.BOOK_TITLE_COLNAME + "`";
        String  bookPublisherSuffix = Book.PUBLISHER_NAME_COLNAME + "`";
        String  bookAuthorSuffix = BookAuthor.AUTHOR_NAME_COLNAME + "`";
        for (String colName : colValues.keySet()) {
            cnt++;
            sqlQuery.append(colName);
            if (useLikeQuery && (colName.endsWith(bookTitleSuffix)
                    || colName.endsWith(bookPublisherSuffix)
                    || colName.endsWith(bookAuthorSuffix))) {
                sqlQuery.append(" LIKE ?");
            } else {
                sqlQuery.append(" = ?");
            }
            for (int i = 0; i < colValues.get(colName).size() - 1; i++) {
                sqlQuery.append(" AND ");
                sqlQuery.append(colName);
                if (useLikeQuery && (colName.endsWith(bookTitleSuffix)
                        || colName.endsWith(bookPublisherSuffix)
                        || colName.endsWith(bookAuthorSuffix))) {
                    sqlQuery.append(" LIKE ?");
                } else {
                    sqlQuery.append(" = ?");
                }
            }
            if (cnt != colValues.size()) {
                sqlQuery.append(" AND ");
            }
        }

    }

    public BookList findBooks(int pageNumber, int pageSize,
                              LinkedHashMap<String, ArrayList<String>> colValues,
                              String orderCol, boolean ascending, boolean useLikeQueries) throws SQLException {
        LinkedHashMap<String, ArrayList<String>> authorConditions = new LinkedHashMap<>();
        LinkedHashMap<String, ArrayList<String>> bookConditions = new LinkedHashMap<>();

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
//                authorConditions.put(alias + ".`" + colName + "`", colValues.get(colName));
            }
        }

        StringBuilder sqlQuery = new StringBuilder();
        sqlQuery.append("SELECT * FROM (SELECT * FROM BOOK"); /* +
                "` WHERE ");*/
        if (!bookConditions.isEmpty()) {
            sqlQuery.append(" WHERE ");
            buildQueryCondition(sqlQuery, bookConditions, useLikeQueries);
        }

        if (!authorConditions.isEmpty()) {
            if (!bookConditions.isEmpty()) {
                sqlQuery.append(" AND ");
            } else {
                sqlQuery.append(" WHERE ");
            }
            sqlQuery.append("BOOK.`");
            sqlQuery.append(Book.ISBN_COLNAME);
            sqlQuery.append("` IN (SELECT DISTINCT ");
            sqlQuery.append(BookAuthor.ISBN_COLNAME);
            sqlQuery.append(" FROM BOOK_AUTHORS");
            sqlQuery.append(" WHERE ");
            buildQueryCondition(sqlQuery, authorConditions, useLikeQueries);
            sqlQuery.append(")");
        }
        sqlQuery.append(" ORDER BY BOOK.`").append(orderCol).append("`");
        if (ascending) {
            sqlQuery.append(" ASC");
        } else {
            sqlQuery.append(" DESC");
        }
        sqlQuery.append(" LIMIT ? OFFSET ?");
        sqlQuery.append(" ) AS " + alias
                + " LEFT OUTER JOIN BOOK_AUTHORS ON BOOK_AUTHORS.`"
                + BookAuthor.ISBN_COLNAME + "` = " + alias + ".`" + Book.ISBN_COLNAME + "`");



        String  bookTitleSuffix = Book.BOOK_TITLE_COLNAME + "`";
        String  bookPublisherSuffix = Book.PUBLISHER_NAME_COLNAME + "`";
        String  bookAuthorSuffix = BookAuthor.AUTHOR_NAME_COLNAME + "`";

        PreparedStatement preparedStatement = DBConnection.prepareStatement(sqlQuery.toString());
        int i = 1;
        for (String colName : bookConditions.keySet()) {
            for (int j = 0; j < bookConditions.get(colName).size(); i++, j++) {
                if (useLikeQueries && (colName.endsWith(bookTitleSuffix)
                        || colName.endsWith(bookPublisherSuffix)
                        || colName.endsWith(bookAuthorSuffix))) {
                    preparedStatement.setString(i,  "%" + bookConditions.get(colName).get(j) + "%");
                } else {
                    preparedStatement.setString(i, bookConditions.get(colName).get(j));
                }
            }
        }


        for (String colName : authorConditions.keySet()) {
            for (int j = 0; j < authorConditions.get(colName).size(); i++, j++) {

                if (useLikeQueries && (colName.endsWith(bookTitleSuffix)
                        || colName.endsWith(bookPublisherSuffix)
                        || colName.endsWith(bookAuthorSuffix))) {
                    preparedStatement.setString(i,  "%" + authorConditions.get(colName).get(j) + "%");
                } else {
                    preparedStatement.setString(i, authorConditions.get(colName).get(j));
                }
            }
        }
        preparedStatement.setInt(i++, pageSize + 1);
        preparedStatement.setInt(i++, (pageNumber - 1) * pageSize);

        System.out.println(preparedStatement.toString());
        ResultSet rs = preparedStatement.executeQuery();

        BookList books = new BookList();

        books.setPrev(pageNumber > 1);

        while (rs.next()) {
            String ISBN = rs.getString(Book.ISBN_COLNAME);
            if (books.size() == pageSize && !books.contains(ISBN)) {
                books.setNext(true);
                continue;
            }
            Book curBook = books.findBook(ISBN);
            curBook = getBook(curBook, rs);
            books.addBook(curBook);
        }
        return books;
    }

    public BookList findBooks(int pageNumber, int pageSize,
                              LinkedHashMap<String, ArrayList<String>> colValues,
                              String orderCol, boolean ascending) throws SQLException {
        return findBooks(pageNumber, pageSize, colValues, orderCol, ascending, false);
    }

    public BookList findBooks(int pageNumber, int pageSize,
                              String colName, ArrayList<String> values,
                              String orderCol, boolean ascending,
                              boolean useLikeQuery) throws SQLException {
        LinkedHashMap<String, ArrayList<String>> colValues = new LinkedHashMap<>();
        colValues.put(colName, values);
        return findBooks(pageNumber, pageSize, colValues, orderCol, ascending, useLikeQuery);
    }

    public BookList findBooks(int pageNumber, int pageSize,
                              String colName, String value,
                              String orderCol, boolean ascending,
                              boolean useLikeQuery) throws SQLException {
        ArrayList<String> values = new ArrayList<>();
        values.add(value);
        return findBooks(pageNumber, pageSize, colName, values, orderCol, ascending, useLikeQuery);
    }

    public Book getBook(String ISBN) throws SQLException {
        BookList books = findBooks(1, 1, Book.ISBN_COLNAME, ISBN,
                Book.ISBN_COLNAME,true,false);

        return books.findBook(ISBN);
    }

    public boolean addBook(String ISBN, String publisherName, String bookTitle,
                           int booksInStock, int minThreshold, Date publicationYear,
                           double price, String category, Collection<String> bookAuthors) throws SQLException {
        if (ISBN == null || publisherName == null || bookTitle == null
                || booksInStock < 0 || minThreshold < 0 || publicationYear == null
                || price <= 0 || category == null) {
            return false;
        }

        DBConnection.setAutoCommit(false);

        String bookInsertQuery = "INSERT INTO BOOK (`" +
                Book.ISBN_COLNAME + "`, `" +
                Book.PUBLISHER_NAME_COLNAME + "`, `" +
                Book.BOOK_TITLE_COLNAME + "`, `" +
                Book.BOOKS_IN_STOCK_COLNAME + "`, `" +
                Book.MIN_THRESHOLD_COLNAME + "`, `" +
                Book.PUBLICATION_YEAR_COLNAME + "`, `" +
                Book.PRICE_COLNAME + "`, `" +
                Book.CATEGORY_COLNAME + "`) VALUES(?, ?, ?, ?, ?, ?, ?, ?)";

        PreparedStatement bookInsertStatement = null;
        PreparedStatement authorInsertStatement = null;
        boolean retVal = false;

        try {
            bookInsertStatement = DBConnection.prepareStatement(bookInsertQuery);
            bookInsertStatement.setString(1, ISBN);
            bookInsertStatement.setString(2, publisherName);
            bookInsertStatement.setString(3, bookTitle);
            bookInsertStatement.setInt(4, booksInStock);
            bookInsertStatement.setInt(5, minThreshold);

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
            String yearStr = sdf.format(publicationYear);
            System.out.println(yearStr);
            bookInsertStatement.setString(6, yearStr);
            bookInsertStatement.setDouble(7, price);
            bookInsertStatement.setString(8, category);

            System.out.println(bookInsertStatement);
            bookInsertStatement.executeUpdate();

            if (!bookAuthors.isEmpty()) {
                StringBuilder authorInsertQuery = new StringBuilder();
                authorInsertQuery.append("INSERT INTO BOOK_AUTHORS VALUES ");

                for (int i = 0; i < bookAuthors.size(); i++) {
                    authorInsertQuery.append("(?, ?)");

                    if (i < bookAuthors.size() - 1) {
                        authorInsertQuery.append(", ");
                    }
                }

                authorInsertStatement = DBConnection.prepareStatement(authorInsertQuery.toString());

                int pos = 1;

                for (String author : bookAuthors) {
                    authorInsertStatement.setString(pos++, ISBN);
                    authorInsertStatement.setString(pos++, author);
                }

                System.out.println(authorInsertStatement);
                authorInsertStatement.executeUpdate();
            }


            DBConnection.commit();
            retVal = true;
        } catch (SQLException ex) {
            DBConnection.rollback();
        } finally {
            if (bookInsertStatement != null) {
                bookInsertStatement.close();
            }
            if (authorInsertStatement != null) {
                authorInsertStatement.close();
            }
            DBConnection.setAutoCommit(true);
        }
        return retVal;
    }

    public boolean addBook(String ISBN, String publisherName, String bookTitle,
                           int minThreshold, Date publicationYear,
                           double price, String category, Collection<String> bookAuthors) throws SQLException {
        return addBook(ISBN, publisherName, bookTitle, 0, minThreshold,
                publicationYear, price, category, bookAuthors);
    }

    public boolean modifyBook(String ISBN, LinkedHashMap<String, String> colValues) throws SQLException {
        if (colValues == null || colValues.isEmpty()) {
            return false;
        }
        StringBuilder sqlQuery = new StringBuilder();
        sqlQuery.append("UPDATE BOOK SET");

        int i = 0;

        for (String colName : colValues.keySet()) {
            sqlQuery.append(" BOOK.`");
            sqlQuery.append(colName);
            sqlQuery.append("` = ? ");
            if (i++ < colValues.size() - 1) {
                sqlQuery.append(",");
            }
        }

        sqlQuery.append(" WHERE " + Book.ISBN_COLNAME + " = ?");

        PreparedStatement preparedStatement = DBConnection.prepareStatement(sqlQuery.toString());

        int pos = 1;

        for (String colName : colValues.keySet()) {
            preparedStatement.setString(pos++, colValues.get(colName));
        }

        preparedStatement.setString(pos, ISBN);

        int updateCount = preparedStatement.executeUpdate();

        System.out.println(preparedStatement);

        return updateCount > 0;
    }

    public boolean modifyBook(String ISBN, String colName, String newValue) throws SQLException {
        LinkedHashMap<String, String> colValues = new LinkedHashMap<>();
        colValues.put(colName, newValue);

        return modifyBook(ISBN, colValues);
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

    public boolean updateUser(int userID, LinkedHashMap<String, String> colValues) throws SQLException {

        if (colValues == null || colValues.isEmpty()) {
            return false;
        }
        StringBuilder sqlQuery = new StringBuilder();
        sqlQuery.append("UPDATE BOOKSTORE_USER SET");

        int i = 0;

        for (String colName : colValues.keySet()) {
            sqlQuery.append(" BOOKSTORE_USER.`");
            sqlQuery.append(colName);
            sqlQuery.append("` = ?");
            if (i++ < colValues.size() - 1) {
                sqlQuery.append(",");
            }
        }

        sqlQuery.append(" WHERE " + BookstoreUser.USER_ID_COLNAME + " = ?");

        PreparedStatement preparedStatement = DBConnection.prepareStatement(sqlQuery.toString());

        int pos = 1;

        for (String colName : colValues.keySet()) {
            preparedStatement.setString(pos++, colValues.get(colName));
        }

        preparedStatement.setInt(pos, userID);

        int updateCount = preparedStatement.executeUpdate();

        System.out.println(preparedStatement);

        return updateCount > 0;
    }

    public boolean promote(int userID, String newRole) throws SQLException {
        LinkedHashMap<String, String> colValues = new LinkedHashMap<>();
        colValues.put(BookstoreUser.USER_GROUP_COLNAME, newRole);
        return updateUser(userID, colValues);
    }

    public boolean promote(String userName, String newRole) throws SQLException {
        return false;
    }

    public boolean updatePassword(int userID, String oldPassword, String newPassword) throws SQLException {
        String sqlQuery = "{CALL UPDATE_PASSWORD(?, ?, ?, ?, ?)}";
        CallableStatement callStatement = DBConnection.prepareCall(sqlQuery);
        callStatement.setInt(1, userID);
        callStatement.setString(2, oldPassword);
        callStatement.setString(3, newPassword);
        callStatement.registerOutParameter(4, Types.BOOLEAN);
        callStatement.registerOutParameter(5, Types.VARCHAR);

        int updateCount = callStatement.executeUpdate();

        System.out.println(callStatement);

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

    public boolean buyBooks(int userID,
                            Map<String, Integer> bookQuantities,
                            Date purchaseDate) throws SQLException {
        String sqlQuery = "{CALL MAKE_PURCHASE(?, ?, ?, ?, ?, ?, ?, ?)}";
        Collection<Statement> statements = new ArrayList<>();

        if (bookQuantities.isEmpty()) {
            return false;
        }

        DBConnection.setAutoCommit(false);

        boolean retVal = false;

        try {
            for (String ISBN : bookQuantities.keySet()) {
                CallableStatement callStatement = DBConnection.prepareCall(sqlQuery);

                statements.add(callStatement);

                callStatement.setInt(1, userID);
                callStatement.setString(2, ISBN);
                callStatement.setDate(3, new java.sql.Date(purchaseDate.getTime()));
                callStatement.setInt(4, bookQuantities.get(ISBN));
                callStatement.registerOutParameter(5, Types.BOOLEAN);
                callStatement.registerOutParameter(6, Types.INTEGER);
                callStatement.registerOutParameter(7, Types.FLOAT);
                callStatement.registerOutParameter(8, Types.VARCHAR);


                int updateCount = callStatement.executeUpdate();

                System.out.println(callStatement);

                boolean success = callStatement.getBoolean(5);


                int purchaseId = callStatement.getInt(6);

                double totalCost = callStatement.getDouble(7);


                String errorMessage = callStatement.getString(8);

                if (!success) {
                    throw new SQLException(errorMessage);
                }

                System.out.println(totalCost);


                if (errorMessage != null) {
                    System.out.println(errorMessage);
                }
            }

            DBConnection.commit();
            retVal = true;

        } catch (SQLException ex) {
            ex.printStackTrace();
            DBConnection.rollback();
        } finally {
            for (Statement statement : statements) {
                if (statement != null) {
                    statement.close();
                }
            }
            DBConnection.setAutoCommit(true);
        }

        return retVal;
    }

    public boolean buyBook(int userID, Map<String, Integer> bookQuantities) throws SQLException {
        return buyBooks(userID, bookQuantities, new Date());
    }

    public boolean buyBook(int userID, String ISBN, int quantity) throws SQLException {
        Map<String, Integer> bookQuantities = new HashMap<>();

        bookQuantities.put(ISBN, quantity);

        return buyBook(userID, bookQuantities);
    }

    public int orderBook(String ISBN, int quantity) throws SQLException {

        if (ISBN == null || quantity <= 0) {
            return -1;
        }

        DBConnection.setAutoCommit(false);
        int orderNo = -1;

        PreparedStatement selectStatement = null;
        PreparedStatement insertStatement = null;

        try {
            String selectQuery = "SELECT "+ Book.PUBLISHER_NAME_COLNAME + " FROM BOOK WHERE " + Book.ISBN_COLNAME + " = ?";

            selectStatement = DBConnection.prepareStatement(selectQuery);
            selectStatement.setString(1, ISBN);

            ResultSet selectResultSet = selectStatement.executeQuery();
            System.out.println(selectStatement);

            selectResultSet.next();
            String publisherName = selectResultSet.getString(Book.PUBLISHER_NAME_COLNAME);

            System.out.println("PUB: " + publisherName);

            String sqlQuery = "INSERT INTO BOOK_ORDERS (" +
                    BookOrder.ISBN_COLNAME + ", " +
                    BookOrder.PUBLISHER_NAME_COLNAME + ", " +
                    BookOrder.QUANTITY_COLNAME + ") VALUES(?, ?, ?)";

            insertStatement = DBConnection.prepareStatement(sqlQuery,
                    Statement.RETURN_GENERATED_KEYS);
            insertStatement.setString(1, ISBN);
            insertStatement.setString(2, publisherName);
            insertStatement.setInt(3, quantity);

            int updateCount = insertStatement.executeUpdate();

            System.out.println(insertStatement);

            ResultSet rs = insertStatement.getGeneratedKeys();
            rs.next();
            orderNo = rs.getInt(1);
            DBConnection.commit();
        } catch(SQLException e) {
            orderNo = -1;
            DBConnection.rollback();
        } finally {
            DBConnection.setAutoCommit(true);
            if (selectStatement != null) {
                selectStatement.close();
            }
            if (insertStatement != null) {
                insertStatement.close();
            }
        }
        return orderNo;
    }

    public boolean confirmOrder(int orderNo) throws SQLException {
        String sqlQuery = "DELETE FROM BOOK_ORDERS WHERE " +
                BookOrder.ORDER_NO_COLNAME + " = ?";

        PreparedStatement preparedStatement = DBConnection.prepareStatement(sqlQuery);
        preparedStatement.setInt(1, orderNo);

        int updateCount = preparedStatement.executeUpdate();

        return updateCount > 0;
    }

    public Collection<BookOrder> getOrders(int pageNumber, int pageSize) throws SQLException {
        String sqlQuery = "SELECT * FROM BOOK_ORDERS ORDER BY BOOK_ORDERS.`" + BookOrder.ORDER_NO_COLNAME
                            + "` DESC LIMIT ? OFFSET ?";

        PreparedStatement preparedStatement = DBConnection.prepareStatement(sqlQuery);

        preparedStatement.setInt(1, pageSize);
        preparedStatement.setInt(2, (pageNumber - 1) * pageSize);

        ResultSet rs = preparedStatement.executeQuery();

        System.out.println(preparedStatement);

        Collection<BookOrder> orders = new ArrayList<>();

        while (rs.next()) {
            BookOrder curOrder = new BookOrder();

            curOrder.setISBN(rs.getString(BookOrder.ISBN_COLNAME));
            curOrder.setOrderNo(rs.getInt(BookOrder.ORDER_NO_COLNAME));
            curOrder.setPublisherName(rs.getString(BookOrder.PUBLISHER_NAME_COLNAME));
            curOrder.setQuantity(rs.getInt(BookOrder.QUANTITY_COLNAME));

            Calendar gmt = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
            Timestamp orderDate = rs.getTimestamp(BookOrder.ORDER_DATE_COLNAME);
            curOrder.setOrderDate(orderDate.toLocalDateTime());

            orders.add(curOrder);
        }

        return orders;
    }

    public int getNumberOfOrders() throws SQLException {
        Statement statement = DBConnection.createStatement();
        ResultSet rs = statement.executeQuery("SELECT COUNT(*) FROM BOOK_ORDERS");

        if (rs.next()) {
            return rs.getInt(1);
        }

        return -1;
    }

    public int getOrdersPageCount(int pageSize) throws SQLException {
        return (getNumberOfOrders() - 1) / pageSize + 1;

    }

    public boolean printJasperReport(String jasperFile, String outputFile, String reportTitle) {

        if (jasperPath == null) {
            return false;
        }

        if (outputFile == null) {
            outputFile = jasperFile + JasperReportService.PDF_EXTENSION;
        }

        jasperFile = jasperPath + File.separator + jasperFile + JasperReportService.JASPER_EXTENSION;

        return JasperReportService.printReport(DBConnection, reportTitle,
                jasperFile, outputFile);
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
}
