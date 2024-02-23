package model;

/**
 * The Country class represents a country.
 */
public class Country {

    // Declare Fields

    /**
     * The id of the country.
     */
    public int id;

    /**
     * The name of the country.
     */
    public String name;

    // Declare Methods

    /**
     * Constructs a new Country object with the provided parameters.
     *
     * @param id   The id of the country
     * @param name The name of the country
     */
    public Country(int id, String name) {
        this.id = id;
        this.name = name;
    }

    /**
     * Returns the country id.
     *
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * Returns the country name.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

}

