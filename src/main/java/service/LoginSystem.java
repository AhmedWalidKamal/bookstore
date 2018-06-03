package service;

import java.sql.*;
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


    public String login(String userName, String password) throws SQLException {
        if (userName == null || password == null) {
            return null;
        }
        String sqlQuery = "SELECT * FROM BOOKSTORE_USER WHERE USER_NAME=?";
        PreparedStatement preparedStatement = DBConnection.prepareStatement(sqlQuery);
        preparedStatement.setString(1, userName);
        ResultSet rs = preparedStatement.executeQuery();

        while(rs.next()){
            String userPassword = rs.getString(3);
            if (!userPassword.equals(password)) {
                return null;
            } else {
                return rs.getString(4);
            }
        }
        return null;
    }

    public boolean register(String userName, String email, String password, String userGroup) throws SQLException {
        if (userName == null || password == null || email == null || userGroup == null) {
            return false;
        }
        String sqlQuery = "INSERT INTO BOOKSTORE_USER VALUES(?, ?, ?, ?)";
        PreparedStatement preparedStatement = DBConnection.prepareStatement(sqlQuery);
        preparedStatement.setString(1, userName);
        preparedStatement.setString(2, email);
        preparedStatement.setString(3, password);
        preparedStatement.setString(4, userGroup);
        ResultSet rs = preparedStatement.executeQuery();

        return true;
    }



    public static void main(String[] args) {
        try {
            LoginSystem sys = new LoginSystem();
//            sys.register("b2", "b@a.c", "pw", "customer");
            String usergroup = sys.login("Barry", "bassword");
            System.out.println(usergroup);
            usergroup = sys.login("b2", "pw");
            System.out.println(usergroup);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
