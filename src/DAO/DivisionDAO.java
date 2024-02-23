package DAO;

import helper.JDBC;
import model.Division;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles data access operations for division data.
 */
public class DivisionDAO {

    /**
     * Selects the division record from the database that is associated with the unique id provided.
     *
     * @param divisionId The id of the division to be selected.
     * @return The division object associated with the unique id provided, or null if no matching records.
     */
    public static Division selectDivisionById(int divisionId) {

        String name;
        int countryId;

        // SQL Used
        String sql = "SELECT Division, Country_ID FROM client_schedule.first_level_divisions WHERE Division_ID = ?";

        // Use established JDBC connection to query database with sql string
        try {
            PreparedStatement statement = JDBC.connection.prepareStatement(sql);
            statement.setInt(1, divisionId);
            ResultSet resultSet = statement.executeQuery();

            // Process resultSet
            if (resultSet.next()) {
                name = resultSet.getString("Division");
                countryId = resultSet.getInt("Country_ID");
                return new Division(divisionId, name, countryId);
            }
        } catch (SQLException e) {
            System.err.println("Error while fetching division: " + e.getMessage());
        }
        return null;
    }


    /**
     * Selects the division ID from the database that matches the provided division name.
     *
     * @param divisionName The name of the division to search for
     * @return The ID of the division, or -1 if not found.
     */
    public static int selectDivisionIdByName(String divisionName) {
        int divisionId = -1;  // default value indicating not found

        // SQL Used
        String sql = "SELECT Division_ID FROM client_schedule.first_level_divisions WHERE Division = ?";

        // Use established JDBC connection to query database with sql string
        try {
            PreparedStatement statement = JDBC.connection.prepareStatement(sql);
            statement.setString(1, divisionName);
            ResultSet resultSet = statement.executeQuery();

            // Process resultSet
            if (resultSet.next()) {
                divisionId = resultSet.getInt("Division_ID");
            }
        } catch (SQLException e) {
            System.err.println("Error while fetching division ID: " + e.getMessage());
        }
        return divisionId;
    }

    /**
     * Gathers a list of division names from the database based on the specified country ID.
     *
     * @param countryId The ID of the country division names need to be fetched for.
     * @return A list of division names associated with the given country ID.
     */
    public static List<String> selectDivisionNamesByCountryID(int countryId) {

        List<String> divisionNamesByCountryID = new ArrayList<>();
        String name;

        // SQL Used
        String sql = "SELECT Division FROM client_schedule.first_level_divisions WHERE Country_ID = ?";

        // Use established JDBC connection to query database with sql string
        try {
            PreparedStatement statement = JDBC.connection.prepareStatement(sql);
            statement.setInt(1, countryId);
            ResultSet resultSet = statement.executeQuery();

            // Process resultSet
            while (resultSet.next()) {
                name = resultSet.getString("Division");
                divisionNamesByCountryID.add(name);
            }
        } catch (SQLException e) {
            System.err.println("Error while fetching division names: " + e.getMessage());
        }
        return divisionNamesByCountryID;
    }
}
