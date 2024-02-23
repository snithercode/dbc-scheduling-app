package model;

import java.sql.Timestamp;
import java.time.LocalDateTime;

/**
 * Represents a customer with various personal and contact details.
 */
public class Customer {

    /**
     * The customer phone number.
     */
    private final String phone;
    /**
     * The state or province of the customer's address.
     */
    private final String stateProvince;
    /**
     * The postal code for the customer's address.
     */
    private final String postalCode;
    /**
     * The country for the customer's address.
     */
    private final String country;
    /**
     * The date and time for when the customer record was created.
     */
    private final LocalDateTime createDate;
    /**
     * The user who created the customer record.
     */
    private final String createdBy;
    /**
     * The timestamp of most recent update to the customer record.
     */
    private final Timestamp lastUpdate;
    /**
     * The username of the person who most recently updated the customer record.
     */
    private final String lastUpdatedBy;
    /**
     * The unique identifier for the customer.
     */
    private Integer id;
    /**
     * The customer name.
     */
    private String name;
    /**
     * The customer's address.
     */
    private String address;

    /**
     * Constructs a Customer with specified details.
     */

    public Customer(Integer id, String name, String address, String phone, String stateProvince, String postalCode,
                    String country, LocalDateTime createDate, String createdBy, Timestamp lastUpdate,
                    String lastUpdatedBy) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.phone = phone;
        this.stateProvince = stateProvince;
        this.postalCode = postalCode;
        this.country = country;
        this.createDate = createDate;
        this.createdBy = createdBy;
        this.lastUpdate = lastUpdate;
        this.lastUpdatedBy = lastUpdatedBy;
    }

    /**
     * Returns the customer ID.
     *
     * @return the customer id
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the customer ID.
     *
     * @param id the new customer ID
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Returns the customer's name.
     *
     * @return the customer name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the customer name.
     *
     * @param name the customer name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the customer address.
     *
     * @return the address
     */
    public String getAddress() {
        return address;
    }

    /**
     * Sets the customer address.
     *
     * @param address the new customer address
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * Returns the customer phone number.
     *
     * @return the phone number
     */
    public String getPhone() {
        return phone;
    }

    /**
     * Returns the customer state or province
     *
     * @return the state or province
     */
    public String getStateProvince() {
        return stateProvince;
    }

    /**
     * Returns the customer's postal code.
     *
     * @return the postal code
     */
    public String getPostalCode() {
        return postalCode;
    }

    /**
     * Returns the customer country.
     *
     * @return the country
     */
    public String getCountry() {
        return country;
    }

    /**
     * Returns the date and time the customer record was created.
     *
     * @return the creation date and time
     */
    public LocalDateTime getCreateDate() {
        return createDate;
    }

    /**
     * Returns the name of the user who created the customer record.
     *
     * @return the username of the user who created the record
     */
    public String getCreatedBy() {
        return createdBy;
    }

    /**
     * Returns the most recent update timestamp of the customer record.
     *
     * @return the most recent update timestamp
     */
    public Timestamp getLastUpdate() {
        return lastUpdate;
    }

    /**
     * Returns the name of the user who last updated the customer record.
     *
     * @return the last updater's username
     */
    public String getLastUpdatedBy() {
        return lastUpdatedBy;
    }
}