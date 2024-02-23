package model;

/**
 * Represents a division with an ID, name, and associated country ID.
 */
public class Division {

    /**
     * The division id.
     */
    private final int id;

    /**
     * The division name.
     */
    private final String name;

    /**
     * The associated country id.
     */
    private final int countryId;

    /**
     * Constructs a Division with an ID, name, and country ID.
     */
    public Division(int id, String name, int countryId) {
        this.id = id;
        this.name = name;
        this.countryId = countryId;
    }

    /**
     * Returns the division ID.
     *
     * @return the division ID
     */
    public int getDivisionId() {
        return id;
    }

    /**
     * Returns the division name.
     *
     * @return the division name
     */
    public String getDivisionName() {
        return name;
    }

    /**
     * Returns the associated country ID.
     *
     * @return the country ID
     */
    public int getAssociatedCountryId() {
        return countryId;
    }
}
