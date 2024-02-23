package model;

import java.sql.Timestamp;
import java.time.LocalDateTime;

/**
 * The Appointment class represents an appointment for a customer in the scheduling system.
 */
public class Appointment {

    // Declare Fields

    /**
     * The id of the appointment.
     */
    public Integer id;

    /**
     * The title of the appointment.
     */
    public String title;

    /**
     * The description of the appointment.
     */
    public String description;

    /**
     * The location of the appointment.
     */
    public String location;

    /**
     * The type of appointment.
     */
    public String type;

    /**
     * The date and time at which the appointment is scheduled to begin.
     */
    public LocalDateTime startTime;

    /**
     * The date and time at which the appointment is scheduled to end.
     */

    public LocalDateTime endTime;
    /**
     * The date and time at which the appointment was created.
     */
    public LocalDateTime createDate;

    /**
     * The username of the person who created the appointment.
     */
    public String createdBy;

    /**
     * The date and time at which the most recent update was made to the appointment.
     */
    public Timestamp lastUpdate;

    /**
     * The username of the person who made the most recent update to the appointment.
     */
    public String lastUpdatedBy;

    /**
     * The customer ID number associated with the customer the appointment was made for.
     */
    public int customerId;

    /**
     * The user ID number associated with the user who created the appointment.
     */
    public int userId;

    /**
     * The contact ID number.
     */
    public int contactId;


    /**
     * The contact name.
     */
    public String contactName;

    // Declare Methods

    /**
     * Constructs a new Appointment object with the provided parameters.
     *
     * @param id            The unique ID for the appointment.
     * @param title         The title of the appointment.
     * @param description   The description of the appointment.
     * @param location      The location of the appointment.
     * @param type          The type of appointment.
     * @param startTime     The date and time at which the appointment is scheduled to begin.
     * @param endTime       The date and time at which the appointment is scheduled to end.
     * @param createDate    The date and time at which the appointment was created.
     * @param createdBy     The username of the person who created the appointment.
     * @param lastUpdate    The date and time at which the most recent update was made to the appointment.
     * @param lastUpdatedBy The username of the person who made the most recent update to the appointment.
     * @param customerId    The customer ID number associated with the customer the appointment was made for.
     * @param userId        The user ID number associated with the user who created the appointment.
     * @param contactId     The contact ID number.
     */
    public Appointment(Integer id, String title, String description, String location, String type,
                       LocalDateTime startTime, LocalDateTime endTime, LocalDateTime createDate, String createdBy,
                       Timestamp lastUpdate, String lastUpdatedBy, int customerId, int userId, int contactId,
                       String contactName) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.location = location;
        this.type = type;
        this.startTime = startTime;
        this.endTime = endTime;
        this.createDate = createDate;
        this.createdBy = createdBy;
        this.lastUpdate = lastUpdate;
        this.lastUpdatedBy = lastUpdatedBy;
        this.customerId = customerId;
        this.userId = userId;
        this.contactId = contactId;

        this.contactName = contactName;
    }

    /**
     * Returns the appointment id.
     *
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * Returns the appointment title.
     *
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Returns the appointment description.
     *
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns the appointment location.
     *
     * @return the location
     */
    public String getLocation() {
        return location;
    }

    /**
     * Returns the appointment type.
     *
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * Returns the date and time the appointment starts.
     *
     * @return the start date and time
     */
    public LocalDateTime getStartTime() {
        return startTime;
    }

    /**
     * Returns the date and time the appointment ends.
     *
     * @return the end date and time
     */
    public LocalDateTime getEndTime() {
        return endTime;
    }

    /**
     * Returns the date and time the appointment was created.
     *
     * @return the creation date and time
     */
    public LocalDateTime getCreateDate() {
        return createDate;
    }

    /**
     * Returns the user who created the appointment.
     *
     * @return the appointment creator's username
     */
    public String getCreatedBy() {
        return createdBy;
    }

    /**
     * Returns the date and time of the last update made to the appointment.
     *
     * @return the date and time of the most recent update
     */
    public Timestamp getLastUpdate() {
        return lastUpdate;
    }

    /**
     * Returns the user who last updated the appointment.
     *
     * @return the username of the person who made the most recent update to the appointment
     */
    public String getLastUpdatedBy() {
        return lastUpdatedBy;
    }

    /**
     * Returns the ID of the customer associated with the appointment.
     *
     * @return the customer ID
     */
    public int getCustomerId() {
        return customerId;
    }

    /**
     * Returns the ID of the user who created the appointment.
     *
     * @return the user ID
     */
    public int getUserId() {
        return userId;
    }

    /**
     * Returns the contact ID for the appointment.
     *
     * @return the contact ID
     */
    public int getContactId() {
        return contactId;
    }

    /**
     * Returns the contact Name for the appointment.
     *
     * @return the contact name
     */
    public String getContactName() {
        return contactName;
    }
}


