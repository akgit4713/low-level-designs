package cricinfo.models;

import java.util.Objects;
import java.util.UUID;

/**
 * Represents a cricket venue/stadium.
 */
public class Venue {
    private final String id;
    private String name;
    private String city;
    private String country;
    private int capacity;
    private String timezone;

    public Venue(String name, String city, String country) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.city = city;
        this.country = country;
    }

    public Venue(String id, String name, String city, String country) {
        this.id = id;
        this.name = name;
        this.city = city;
        this.country = country;
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCity() {
        return city;
    }

    public String getCountry() {
        return country;
    }

    public int getCapacity() {
        return capacity;
    }

    public String getTimezone() {
        return timezone;
    }

    // Setters
    public void setName(String name) {
        this.name = name;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public String getFullName() {
        return name + ", " + city;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Venue venue = (Venue) o;
        return Objects.equals(id, venue.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Venue{" +
                "name='" + name + '\'' +
                ", city='" + city + '\'' +
                ", country='" + country + '\'' +
                '}';
    }
}



