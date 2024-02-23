package DAO;

import helper.JDBC;
import model.Country;
import model.Customer;
import model.Division;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles data access operations for customer data.
 */
public class CustomerDAO {

    /**
     * Inserts a new customer into the database.
     *
     * @param customer The customer object to insert.
     * @return True if the insertion was successful, false otherwise.
     */
    public static boolean insertCustomer(Customer customer) {
        String sql = "INSERT INTO client_schedule.customers (Customer_Name, Address, Phone, Division_ID, Postal_Code," +
                " Create_Date, Created_By, Last_Update, Last_Updated_By) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try {
            PreparedStatement statement = JDBC.connection.prepareStatement(sql);
            statement.setString(1, customer.getName());
            statement.setString(2, customer.getAddress());
            statement.setString(3, customer.getPhone());
            statement.setInt(4, DivisionDAO.selectDivisionIdByName(customer.getStateProvince()));
            statement.setString(5, customer.getPostalCode());
            statement.setTimestamp(6, Timestamp.valueOf(customer.getCreateDate())); // this line
            statement.setString(7, customer.getCreatedBy());
            statement.setTimestamp(8, customer.getLastUpdate()); // this line
            statement.setString(9, customer.getLastUpdatedBy());

            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Updates a customer record in the database.
     *
     * @param customer The customer object containing updated data.
     * @return True if the update was successful, false otherwise.
     */
    public static boolean updateCustomer(Customer customer) {
        String sql = "UPDATE client_schedule.customers SET Customer_Name = ?, Address = ?, Phone = ?, Division_ID = " +
                "?, Postal_Code = ?, Last_Update = ?, Last_Updated_By = ? WHERE Customer_ID = ?";
        try {
            PreparedStatement statement = JDBC.connection.prepareStatement(sql);
            statement.setString(1, customer.getName());
            statement.setString(2, customer.getAddress());
            statement.setString(3, customer.getPhone());
            statement.setInt(4, DivisionDAO.selectDivisionIdByName(customer.getStateProvince()));
            statement.setString(5, customer.getPostalCode());
            statement.setTimestamp(6, customer.getLastUpdate());
            statement.setString(7, customer.getLastUpdatedBy());
            statement.setInt(8, customer.getId());

            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Deletes the customer record from the database associated with the provided unique ID.
     *
     * @param id The id of the customer to be deleted.
     * @return True if the deletion was successful, false otherwise.
     */
    public static boolean deleteCustomer(int id) {
        String sql = "DELETE FROM client_schedule.customers WHERE Customer_ID = ?";
        try {
            PreparedStatement statement = JDBC.connection.prepareStatement(sql);
            statement.setInt(1, id);

            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    /**
     * Fetches all customer records from the database and returns them in a list.
     *
     * @return The list of all customers in the database, or null if no customer records were found.
     */
    public static List<Customer> selectAllCustomers() {
        // Create empty list
        List<Customer> customers = new ArrayList<>();

        // SQL Used
        String sql = "SELECT * FROM client_schedule.customers";

        // Use established JDBC connection to query database with sql string
        try {
            PreparedStatement statement = JDBC.connection.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery();

            // Process resultSet
            while (resultSet.next()) {
                int id = resultSet.getInt("Customer_ID");
                String name = resultSet.getString("Customer_Name");
                String address = resultSet.getString("Address");
                String postalCode = resultSet.getString("Postal_Code");
                String phone = resultSet.getString("Phone");
                LocalDateTime createDate = resultSet.getObject("Create_Date", LocalDateTime.class);
                String createdBy = resultSet.getString("Created_By");
                Timestamp lastUpdate = resultSet.getTimestamp("Last_Update");
                String lastUpdatedBy = resultSet.getString("Last_Updated_By");

                int divisionId = resultSet.getInt("Division_ID");


                /* State, Province, and Country names are not included in the customers table.
                 * To retrieve these values I need to:
                 */

                // 1. Query first_level_divisions with Division_ID, retrieve Country_ID
                Division division = DivisionDAO.selectDivisionById(divisionId);
                if (division == null) {
                    // Handle case where division is null
                    System.err.println("No division found for division ID: " + divisionId);
                    continue; // Skip iteration if no division found
                }
                String stateProvince = division.getDivisionName();
                int countryId = division.getAssociatedCountryId();

                // 2. Query countries with Country_ID, retrieve Country (name of country)
                Country country = CountryDAO.select(countryId);
                if (country == null) {
                    // Handle case where country is null
                    System.err.println("No country found for country ID: " + countryId);
                    continue; // Skip iteration if no country found
                }
                String countryName = country.getName();


                // Create new customer object and add it to the customers list
                customers.add(new Customer(id, name, address, phone, stateProvince, postalCode, countryName,
                        createDate, createdBy, lastUpdate, lastUpdatedBy));
            }
        } catch (SQLException e) {
            System.err.println("Error while fetching customers: " + e.getMessage());
        }
        // Return list of fetched customers
        return customers;
    }

    /**
     * Gets a customer record from the database with the provided customer ID.
     * The returned record includes customer details with associated division and country information.
     *
     * @param id The ID of the customer to get.
     * @return The customer record, or null if not found.
     */
    public static Customer selectCustomerById(int id) {
        String sql = "SELECT * FROM client_schedule.customers WHERE Customer_ID = ?";
        try {
            PreparedStatement statement = JDBC.connection.prepareStatement(sql);
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                int customerId = resultSet.getInt("Customer_ID");
                String name = resultSet.getString("Customer_Name");
                String address = resultSet.getString("Address");
                String postalCode = resultSet.getString("Postal_Code");
                String phone = resultSet.getString("Phone");
                LocalDateTime createDate = resultSet.getTimestamp("Create_Date").toLocalDateTime();
                String createdBy = resultSet.getString("Created_By");
                Timestamp lastUpdate = resultSet.getTimestamp("Last_Update");
                String lastUpdatedBy = resultSet.getString("Last_Updated_By");
                int divisionId = resultSet.getInt("Division_ID");

                // Get the Division object
                Division division = DivisionDAO.selectDivisionById(divisionId);
                if (division == null) {
                    System.err.println("No division found for division ID: " + divisionId);
                    return null;
                }
                String stateProvince = division.getDivisionName();
                // Get the Country object associated with the division
                Country country = CountryDAO.select(division.getAssociatedCountryId());
                if (country == null) {
                    System.err.println("No country found for country ID: " + division.getAssociatedCountryId());
                    return null;
                }
                String countryName = country.getName();

                // Return the new Customer object

                return new Customer(
                        customerId,
                        name,
                        address,
                        phone,
                        stateProvince,
                        postalCode,
                        countryName,
                        createDate,
                        createdBy,
                        lastUpdate,
                        lastUpdatedBy
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}

