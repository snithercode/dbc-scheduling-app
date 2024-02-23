package DAO;

import helper.JDBC;
import model.Contact;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles data access operations for contact data.
 */
public class ContactDAO {

    /**
     * Inserts a new contact into the contacts table.
     *
     * @param contact The contact object to be inserted.
     * @return True if the insertion is successful, false otherwise.
     */
    public static boolean insertContact(Contact contact) {
        String sql = "INSERT INTO client_schedule.contacts (Contact_Name, Email) VALUES (?, ?)";
        try {
            PreparedStatement statement = JDBC.connection.prepareStatement(sql);
            statement.setString(1, contact.getName());
            statement.setString(2, contact.getEmail());
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Gets a contact from the contacts table by its Contact_ID.
     *
     * @param contactId The ID of the contact to get.
     * @return The contact object corresponding to the given Contact_ID.
     */
    public static Contact selectContactById(int contactId) {
        String sql = "SELECT * FROM client_schedule.contacts WHERE Contact_ID = ?";
        try {
            PreparedStatement statement = JDBC.connection.prepareStatement(sql);
            statement.setInt(1, contactId);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return new Contact(resultSet.getInt("Contact_ID"), resultSet.getString("Contact_Name"),
                        resultSet.getString("Email"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Gets a contact from the contacts table by its Contact_Name.
     *
     * @param contactName The name of the contact to get.
     * @return The contact object corresponding to the given Contact_Name.
     */
    public static Contact selectContactByName(String contactName) {
        String sql = "SELECT * FROM client_schedule.contacts WHERE Contact_Name = ?";
        try {
            PreparedStatement statement = JDBC.connection.prepareStatement(sql);
            statement.setString(1, contactName);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return new Contact(resultSet.getInt("Contact_ID"), resultSet.getString("Contact_Name"),
                        resultSet.getString("Email"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null; // added safe return of null
        }
        return null; // no contact found
    }

    /**
     * Gets all contacts from the contacts table.
     *
     * @return A list of all contact objects in the contacts table.
     */
    public static List<Contact> selectAllContacts() {
        List<Contact> contacts = new ArrayList<>();
        String sql = "SELECT * FROM client_schedule.contacts";
        try {
            PreparedStatement statement = JDBC.connection.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                contacts.add(new Contact(resultSet.getInt("Contact_ID"), resultSet.getString("Contact_Name"),
                        resultSet.getString("Email")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return contacts;
    }

    /**
     * Updates an existing contact in the contacts table.
     *
     * @param contact The contact object with updated details.
     * @return True if the update is successful, false otherwise.
     */
    public static boolean updateContact(Contact contact) {
        String sql = "UPDATE client_schedule.contacts SET Contact_Name = ?, Email = ? WHERE Contact_ID = ?";
        try {
            PreparedStatement statement = JDBC.connection.prepareStatement(sql);
            statement.setString(1, contact.getName());
            statement.setString(2, contact.getEmail());
            statement.setInt(3, contact.getId());
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Deletes a contact from the contacts table by its Contact_ID.
     *
     * @param contactId The ID of the contact to be deleted.
     * @return True if the deletion is successful, false otherwise.
     */
    public static boolean deleteContact(int contactId) {
        String sql = "DELETE FROM client_schedule.contacts WHERE Contact_ID = ?";
        try {
            PreparedStatement statement = JDBC.connection.prepareStatement(sql);
            statement.setInt(1, contactId);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}

