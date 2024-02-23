package DAO;

import controller.UserLogin;
import helper.JDBC;
import model.Appointment;
import model.Contact;
import model.User;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


/**
 * Handles data access operations for appointment data.
 */
@SuppressWarnings("SqlNoDataSourceInspection")
public class AppointmentDAO {

    /**
     * Inserts a new appointment into the appointments table.
     *
     * @param appointment The appointment object to be inserted.
     * @return True if the insertion is successful, false otherwise.
     */
    public static boolean insertAppointment(Appointment appointment) {
        String sql = "INSERT INTO client_schedule.appointments (Title, Description, Location, Type, Start, End, " +
                "Create_Date, Created_By, Last_Update, Last_Updated_By, Customer_ID, User_ID, Contact_ID) VALUES (?, " +
                "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try {
            PreparedStatement statement = JDBC.connection.prepareStatement(sql);
            statement.setString(1, appointment.getTitle());
            statement.setString(2, appointment.getDescription());
            statement.setString(3, appointment.getLocation());
            statement.setString(4, appointment.getType());
            statement.setTimestamp(5, Timestamp.valueOf(appointment.getStartTime()));
            statement.setTimestamp(6, Timestamp.valueOf(appointment.getEndTime()));
            statement.setTimestamp(7, Timestamp.valueOf(appointment.getCreateDate()));
            statement.setString(8, appointment.getCreatedBy());
            statement.setTimestamp(9, appointment.getLastUpdate());
            statement.setString(10, appointment.getLastUpdatedBy());
            statement.setInt(11, appointment.getCustomerId());
            statement.setInt(12, appointment.getUserId());
            statement.setInt(13, appointment.getContactId());
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    /**
     * Gets all appointments from the appointments table.
     *
     * @return A list of all appointment objects in the appointments table.
     */
    public static List<Appointment> selectAllAppointments() {
        List<Appointment> appointments = new ArrayList<>();
        String sql = "SELECT * FROM client_schedule.appointments";

        try (PreparedStatement statement = JDBC.connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                appointments.add(createAppointmentFromResultSet(resultSet));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return appointments;
    }

    /**
     * Updates an existing appointment in the appointments table.
     *
     * @param appointment The appointment object with updated details.
     * @return True if the update is successful, false otherwise.
     */
    public static boolean updateAppointment(Appointment appointment) {
        String sql = "UPDATE client_schedule.appointments SET Title = ?, Description = ?, Location = ?, Type = ?, " +
                "Start = ?, End = ?, Create_Date = ?, Created_By = ?, Last_Update = ?, Last_Updated_By = ?, " +
                "Customer_ID = ?, User_ID = ?, Contact_ID = ? WHERE Appointment_ID = ?";
        try {
            PreparedStatement statement = JDBC.connection.prepareStatement(sql);
            statement.setString(1, appointment.getTitle());
            statement.setString(2, appointment.getDescription());
            statement.setString(3, appointment.getLocation());
            statement.setString(4, appointment.getType());
            statement.setTimestamp(5, Timestamp.valueOf(appointment.getStartTime()));
            statement.setTimestamp(6, Timestamp.valueOf(appointment.getEndTime()));
            statement.setTimestamp(7, Timestamp.valueOf(appointment.getCreateDate()));
            statement.setString(8, appointment.getCreatedBy());
            statement.setTimestamp(9, appointment.getLastUpdate());
            statement.setString(10, appointment.getLastUpdatedBy());
            statement.setInt(11, appointment.getCustomerId());
            statement.setInt(12, appointment.getUserId());
            statement.setInt(13, appointment.getContactId());
            statement.setInt(14, appointment.getId());
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    /**
     * Deletes an appointment from the appointments table by its Appointment_ID.
     *
     * @param appointmentId The ID of the appointment to be deleted.
     * @return True if the deletion is successful, false otherwise.
     */
    public static boolean deleteAppointment(int appointmentId) {
        String sql = "DELETE FROM client_schedule.appointments WHERE Appointment_ID = ?";
        try {
            PreparedStatement statement = JDBC.connection.prepareStatement(sql);
            statement.setInt(1, appointmentId);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Gets all appointments for a given customer from the appointments table.
     *
     * @param customerId The ID of the customer.
     * @return A list of all appointment objects for the specified customer.
     */
    public static List<Appointment> selectAppointmentsByCustomerId(int customerId) {
        List<Appointment> appointments = new ArrayList<>();
        String sql = "SELECT * FROM client_schedule.appointments WHERE Customer_ID = ?";
        try {
            PreparedStatement statement = JDBC.connection.prepareStatement(sql);
            statement.setInt(1, customerId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                appointments.add(createAppointmentFromResultSet(resultSet));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return appointments;
    }

    /**
     * Gets all appointments starting within the next 15 minutes for the currently logged-in user.
     *
     * <p>This method calculates the current local time and then identifies appointments
     * scheduled to start between the current time and 15 minutes from now. It then filters
     * these appointments to only include ones where the User_Name matches the current user.
     * The result is a list of such appointments from the client_schedule.appointments table.</p>
     *
     * @return A list of all appointment objects starting in the next 15 minutes for the currently logged-in user.
     */
    public static List<Appointment> selectUpcomingAppointmentsForCurrentUser() {
        List<Appointment> appointments = new ArrayList<>();
        String sql = "SELECT * FROM client_schedule.appointments WHERE Start BETWEEN UTC_TIMESTAMP() AND DATE_ADD" +
                "(UTC_TIMESTAMP(), INTERVAL 15 MINUTE) AND User_ID = ?";

        User user = UserDAO.selectUserByUsername(UserLogin.currentUser);
        if (user == null) {
            System.err.println("Error: No user found with username " + UserLogin.currentUser);
            return appointments;  // Return an empty list or handle the case as appropriate.
        }

        int userId = user.getId();
        try {
            PreparedStatement statement = JDBC.connection.prepareStatement(sql);
            statement.setInt(1, userId);  // Use setInt instead of setString for a numeric ID.
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                appointments.add(createAppointmentFromResultSet(resultSet));
            }
        } catch (SQLException e) {
            System.err.println("SQL Error: " + e.getMessage());
        }

        return appointments;
    }


    /**
     * Gets all appointments for a given contact from the appointments table.
     *
     * @param contactId The ID of the contact.
     * @return A list of all appointment objects for the specified contact.
     */
    public static List<Appointment> selectAppointmentsByContactId(int contactId) {
        List<Appointment> appointments = new ArrayList<>();
        String sql = "SELECT * FROM client_schedule.appointments WHERE Contact_ID = ?";
        try {
            PreparedStatement statement = JDBC.connection.prepareStatement(sql);
            statement.setInt(1, contactId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                appointments.add(createAppointmentFromResultSet(resultSet));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return appointments;
    }


    /**
     * Gets the list of appointments that conflict with a desired time range for a given customer.
     * Checks for overlapping appointments based on start and end times using SQL queries.
     *
     * @param customerId the customer id that the user entered
     * @param desiredStartDateTime the start date and time that the user entered
     * @param desiredEndDateTime the end date and time that the user entered
     * @return a list of conflicting appointments
     */
    public static List<Appointment> selectAppointmentsWithRangeConflictForCustomerID(int customerId,
                                                                                     LocalDateTime desiredStartDateTime, LocalDateTime desiredEndDateTime) {
        List<Appointment> appointments = new ArrayList<>();

        System.out.println("                 Checking database for appointments with range conflict for desired start and end " +
                "date/time...");
        String sql = "SELECT * FROM client_schedule.appointments" +
                " WHERE Customer_ID = ?" +
                " AND (" +
                "      (Start < ? AND End > ?) OR" +  // overlaps the start
                "      (Start >= ? AND End <= ?) OR" + // entirely within
                "      (Start < ? AND End > ?)" + // overlaps the end
                "     )";
        try {
            PreparedStatement statement = JDBC.connection.prepareStatement(sql);
            statement.setInt(1, customerId);
            statement.setTimestamp(2, Timestamp.valueOf(desiredEndDateTime));
            statement.setTimestamp(3, Timestamp.valueOf(desiredStartDateTime));
            statement.setTimestamp(4, Timestamp.valueOf(desiredStartDateTime));
            statement.setTimestamp(5, Timestamp.valueOf(desiredEndDateTime));
            statement.setTimestamp(6, Timestamp.valueOf(desiredStartDateTime));
            statement.setTimestamp(7, Timestamp.valueOf(desiredEndDateTime));
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                appointments.add(createAppointmentFromResultSet(resultSet));
            }
            System.out.println("                 Size of conflict list: " + appointments.size());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return appointments;
    }

    /**
     * Gets all appointments for the current week from the appointments table.
     *
     * @return A list of all appointment objects for the current week.
     */
    public static List<Appointment> selectAppointmentsForCurrentWeek() {
        List<Appointment> appointments = new ArrayList<>();
        String sql = "SELECT * FROM client_schedule.appointments WHERE WEEK(Start) = WEEK(CURDATE()) AND YEAR(Start) " +
                "= YEAR(CURDATE())";
        try {
            PreparedStatement statement = JDBC.connection.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                appointments.add(createAppointmentFromResultSet(resultSet));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return appointments;
    }

    /**
     * Gets all appointments for the current month from the appointments table.
     *
     * @return A list of all appointment objects for the current month.
     */

    public static List<Appointment> selectAppointmentsForCurrentMonth() {
        List<Appointment> appointments = new ArrayList<>();
        String sql = "SELECT * FROM client_schedule.appointments WHERE MONTH(Start) = MONTH(CURDATE()) AND YEAR" +
                "(Start) = YEAR(CURDATE())";
        try {
            PreparedStatement statement = JDBC.connection.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery();
            int apptCount = 0;
            while (resultSet.next()) {
                appointments.add(createAppointmentFromResultSet(resultSet));
                apptCount++;
                System.out.println("Adding appt to list for current month. Count so far: " + apptCount);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return appointments;
    }

    /**
     * Extracts appointment details from the ResultSet and creates an Appointment object.
     *
     * @param resultSet A set of appointment data
     * @return The Appointment object created from the ResultSet.
     * @throws SQLException If a SQL error occurs during data extraction.
     */
    private static Appointment createAppointmentFromResultSet(ResultSet resultSet) throws SQLException {
        Integer contactId = resultSet.getObject("Contact_ID", Integer.class); // null-safe fetch added for contactId

        // If contactId is not null, get Contact object, otherwise set contact object to null
        Contact contact = (contactId != null) ? ContactDAO.selectContactById(contactId) : null;
        // If contact is not null, get its name, otherwise set contactName to "Null Contact"
        String contactName = (contact != null) ? contact.getName() : "Null Contact";

        // Adding some more null checks before converting Timestamps to LocalDateTime
        LocalDateTime start = resultSet.getTimestamp("Start") != null ? resultSet.getTimestamp("Start").toLocalDateTime() : null;
        LocalDateTime end = resultSet.getTimestamp("End") != null ? resultSet.getTimestamp("End").toLocalDateTime() : null;
        LocalDateTime createDate = resultSet.getTimestamp("Create_Date") != null ? resultSet.getTimestamp("Create_Date").toLocalDateTime() : null;

        // Was still getting a warning about potential NPE, so now if it's null it'll just assign the value -1 instead.
        int extraNullSafeContactId = (contactId != null) ? contactId : -1;

        return new Appointment(
                resultSet.getInt("Appointment_ID"),
                resultSet.getString("Title"),
                resultSet.getString("Description"),
                resultSet.getString("Location"),
                resultSet.getString("Type"),
                start,
                end,
                createDate,
                resultSet.getString("Created_By"),
                resultSet.getTimestamp("Last_Update"),
                resultSet.getString("Last_Updated_By"),
                resultSet.getInt("Customer_ID"),
                resultSet.getInt("User_ID"),
                extraNullSafeContactId,
                contactName
        );
    }
}

