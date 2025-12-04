package onlineshopping.models;

import java.util.Objects;
import java.util.UUID;

/**
 * Represents a shipping or billing address
 */
public class Address {
    private final String id;
    private final String street;
    private final String city;
    private final String state;
    private final String zipCode;
    private final String country;
    private final String phoneNumber;
    private final boolean isDefault;

    public Address(String street, String city, String state, String zipCode, String country) {
        this(street, city, state, zipCode, country, null, false);
    }

    public Address(String street, String city, String state, String zipCode, 
                   String country, String phoneNumber, boolean isDefault) {
        this.id = UUID.randomUUID().toString();
        this.street = Objects.requireNonNull(street, "Street is required");
        this.city = Objects.requireNonNull(city, "City is required");
        this.state = Objects.requireNonNull(state, "State is required");
        this.zipCode = Objects.requireNonNull(zipCode, "Zip code is required");
        this.country = Objects.requireNonNull(country, "Country is required");
        this.phoneNumber = phoneNumber;
        this.isDefault = isDefault;
    }

    public String getId() {
        return id;
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

    public String getZipCode() {
        return zipCode;
    }

    public String getCountry() {
        return country;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public String getFullAddress() {
        return String.format("%s, %s, %s %s, %s", street, city, state, zipCode, country);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Address address = (Address) o;
        return Objects.equals(id, address.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return getFullAddress();
    }
}



