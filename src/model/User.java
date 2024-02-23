package model;

import java.sql.Timestamp;


/**
 * The User class represents a records in the Users table.
 */
public class User {

    /**
     * The creation date of the user.
     */
    private final Timestamp createDate;
    /**
     * The username that created this user.
     */
    private final String createdBy;
    /**
     * The last update date of the user.
     */
    private final Timestamp lastUpdate;
    /**
     * The username that last updated this user.
     */
    private final String lastUpdatedBy;
    /**
     * The id of the user.
     */
    public int id;
    /**
     * The username of the user.
     */
    public String name;
    /**
     * The password of the user.
     */
    public String password;

    // Declare Methods

    /**
     * Constructs a new User object with the provided parameters.
     *
     * @param id            The user id
     * @param name          The username
     * @param password      The user password
     * @param createDate    The creation date of the user
     * @param createdBy     The username that created this user
     * @param lastUpdate    The last update date of the user
     * @param lastUpdatedBy The username that last updated this user
     */
    public User(int id, String name, String password, Timestamp createDate, String createdBy, Timestamp lastUpdate,
                String lastUpdatedBy) {
        this.id = id;
        this.name = name;
        this.password = password;
        this.createDate = createDate;
        this.createdBy = createdBy;
        this.lastUpdate = lastUpdate;
        this.lastUpdatedBy = lastUpdatedBy;
    }

    /**
     * Returns the user id.
     *
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * Returns the username.
     *
     * @return the username
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the user password
     *
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Returns the creation date of the user.
     *
     * @return the creation date
     */
    public Timestamp getCreateDate() {
        return createDate;
    }

    /**
     * Returns the username that created this user.
     *
     * @return the created by username
     */
    public String getCreatedBy() {
        return createdBy;
    }

    /**
     * Returns the last update date of the user.
     *
     * @return the last update date
     */
    public Timestamp getLastUpdate() {
        return lastUpdate;
    }

    /**
     * Returns the username that last updated this user.
     *
     * @return the last updated by username
     */
    public String getLastUpdatedBy() {
        return lastUpdatedBy;
    }
}
