package hotelmanagement.models;

import java.util.Objects;

/**
 * Value object representing a physical address
 */
public class Address {
    private final String street;
    private final String city;
    private final String state;
    private final String country;
    private final String postalCode;

    public Address(String street, String city, String state, String country, String postalCode) {
        this.street = street;
        this.city = city;
        this.state = state;
        this.country = country;
        this.postalCode = postalCode;
    }

    public String getStreet() {
        return street;
    }

    public String getCity() {
        return city;
    }

    public String getState() {
        return state;
    }

    public String getCountry() {
        return country;
    }

    public String getPostalCode() {
        return postalCode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Address address = (Address) o;
        return Objects.equals(street, address.street) &&
               Objects.equals(city, address.city) &&
               Objects.equals(state, address.state) &&
               Objects.equals(country, address.country) &&
               Objects.equals(postalCode, address.postalCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(street, city, state, country, postalCode);
    }

    @Override
    public String toString() {
        return String.format("%s, %s, %s %s, %s", street, city, state, postalCode, country);
    }
}



