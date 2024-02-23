package DAO;

import helper.JDBC;
import model.User;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles data access operations for user data.
 */
public class UserDAO {

    /**
     * Inserts a new user into the Users table.
     *
     * @param user The user object to be inserted.
     * @return True if the insertion is successful, false otherwise.
     */
    public static boolean insertUser(User user) {
        String sql = "INSERT INTO client_schedule.users (User_Name, Password, Create_Date, Created_By, Last_Update, " +
                "Last_Updated_By) VALUES (?, ?, ?, ?, ?, ?)";
        try {
            PreparedStatement statement = JDBC.connection.prepareStatement(sql);
            statement.setString(1, user.getName());
            statement.setString(2, user.getPassword());
            statement.setTimestamp(3, user.getCreateDate());
            statement.setString(4, user.getCreatedBy());
            statement.setTimestamp(5, user.getLastUpdate());
            statement.setString(6, user.getLastUpdatedBy());
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Gets a user from the Users table by its User_ID.
     *
     * @param userId The ID of the user to get.
     * @return The user object corresponding to the given User_ID.
     */
    public static User selectUserById(int userId) {
        String sql = "SELECT * FROM client_schedule.users WHERE User_ID = ?";
        try {
            PreparedStatement statement = JDBC.connection.prepareStatement(sql);
            statement.setInt(1, userId);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return new User(resultSet.getInt("User_ID"), resultSet.getString("User_Name"), resultSet.getString(
                        "Password"), resultSet.getTimestamp("Create_Date"), resultSet.getString("Created_By"),
                        resultSet.getTimestamp("Last_Update"), resultSet.getString("Last_Updated_By"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Gets a user from the Users table by its User_Name.
     *
     * @param username The username of the user to get.
     * @return The user object corresponding to the given User_Name.
     */
    public static User selectUserByUsername(String username) {
        String sql = "SELECT * FROM client_schedule.users WHERE User_Name = ?";
        try {
            PreparedStatement statement = JDBC.connection.prepareStatement(sql);
            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return new User(resultSet.getInt("User_ID"), resultSet.getString("User_Name"), resultSet.getString(
                        "Password"), resultSet.getTimestamp("Create_Date"), resultSet.getString("Created_By"),
                        resultSet.getTimestamp("Last_Update"), resultSet.getString("Last_Updated_By"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Gets all users from the Users table.
     *
     * @return A list of all user objects in the Users table.
     */
    public static List<User> selectAllUsers() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM client_schedule.users";
        try {
            PreparedStatement statement = JDBC.connection.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                users.add(new User(resultSet.getInt("User_ID"), resultSet.getString("User_Name"),
                        resultSet.getString("Password"), resultSet.getTimestamp("Create_Date"), resultSet.getString(
                        "Created_By"), resultSet.getTimestamp("Last_Update"), resultSet.getString(
                        "Last_Updated_By")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    /**
     * Updates an existing user in the Users table.
     *
     * @param user The user object with updated details.
     * @return True if the update is successful, false otherwise.
     */
    public static boolean updateUser(User user) {
        String sql = "UPDATE client_schedule.users SET User_Name = ?, Password = ?, Last_Update = ?, Last_Updated_By " +
                "= ? WHERE User_ID = ?";
        try {
            PreparedStatement statement = JDBC.connection.prepareStatement(sql);
            statement.setString(1, user.getName());
            statement.setString(2, user.getPassword());
            statement.setTimestamp(3, user.getLastUpdate());
            statement.setString(4, user.getLastUpdatedBy());
            statement.setInt(5, user.getId());
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Deletes a user from the Users table by its User_ID.
     *
     * @param userId The ID of the user to be deleted.
     * @return True if the deletion is successful, false otherwise.
     */
    public static boolean deleteUser(int userId) {
        String sql = "DELETE FROM client_schedule.users WHERE User_ID = ?";
        try {
            PreparedStatement statement = JDBC.connection.prepareStatement(sql);
            statement.setInt(1, userId);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    /**
     * Verify user login credentials.
     *
     * @param username The username entered by the user.
     * @param password The password entered by the user.
     * @return true if credentials are valid, false if not or if database error occurs.
     */
    public static boolean verifyLoginCredentials(String username, String password) throws SQLException {
        String sql = "SELECT Password FROM client_schedule.users WHERE User_Name = ?";
        PreparedStatement statement = JDBC.connection.prepareStatement(sql);

        statement.setString(1, username);
        ResultSet resultSet = statement.executeQuery();

        if (!resultSet.next()) {
            return false;
        }

        String dbPassword = resultSet.getString("Password");
        return dbPassword.equals(password);
    }

}
