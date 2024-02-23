package model;

/**
 * The Contact class represents a contact in the scheduling system.
 * Contact entries in the database consist of a unique Contact ID, a Contact Name, and an Email.
 */
public class Contact {

    // Declare Fields

    /**
     * The id of the contact.
     */
    public int id;

    /**
     * The name of the contact.
     */
    public String name;

    /**
     * The email for the contact.
     */
    public String email;

    // Declare Methods

    /**
     * Constructs a new Contact object with the provided parameters.
     *
     * @param id    The id of the contact.
     * @param name  The name of the contact.
     * @param email The email for the contact.
     */
    public Contact(int id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }

    /**
     * Returns the contact id.
     *
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * Returns the contact name.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the contact email.
     *
     * @return the email
     */
    public String getEmail() {
        return email;
    }
}
