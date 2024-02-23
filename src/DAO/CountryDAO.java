package DAO;

import helper.JDBC;
import model.Country;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles data access operations for country data.
 */
public class CountryDAO {

    /**
     * Selects the country record from the database that is associated with the unique id provided.
     *
     * @param id The unique id of the country to be selected.
     * @return The country object associated with the unique id provided, or null if no matching records.
     */
    public static Country select(int id) {

        String name;

        String sql = "SELECT Country FROM client_schedule.countries WHERE Country_ID = ?";

        try {
            PreparedStatement statement = JDBC.connection.prepareStatement(sql);
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                name = resultSet.getString("Country");
                return new Country(id, name);
            }
        } catch (SQLException e) {
            System.err.println("Error while fetching country: " + e.getMessage());
        }
        return null;
    }


    /**
     * Selects all country names from the database
     *
     * @return A list of country names; empty if none found or an error occurred.
     */
    public static List<String> selectAllCountryNames() {
        List<String> countryNames = new ArrayList<>();

        try {
            String sql = "SELECT Country FROM client_schedule.countries";
            PreparedStatement statement = JDBC.connection.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                String countryName = resultSet.getString("Country");
                countryNames.add(countryName);
            }

        } catch (SQLException e) {
            System.err.println("Error while fetching country names: " + e.getMessage());
        }
        return countryNames;
    }

    /**
     * Gets the ID of a country based on its name from the database.
     *
     * @param countryName The name of the country whose ID is to be fetched.
     * @return The unique ID associated with the provided country name, or null if no matching country is found.
     */
    public static Integer selectCountryIDByName(String countryName) {

        Integer countryId = null;

        String sql = "SELECT Country_ID FROM client_schedule.countries WHERE Country = ?";

        try {
            PreparedStatement statement = JDBC.connection.prepareStatement(sql);
            statement.setString(1, countryName);
            ResultSet resultSet = statement.executeQuery();

            // If matching country is found, get the id
            if (resultSet.next()) {
                countryId = resultSet.getInt("Country_ID");
            }
        } catch (SQLException e) {
            System.err.println("Error while fetching country ID: " + e.getMessage());
        }
        return countryId;
    }

}


