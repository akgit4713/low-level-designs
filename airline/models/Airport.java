package airline.models;

/**
 * Represents an airport with code and location details.
 */
public class Airport {
    private final String code; // IATA code (e.g., JFK, LAX)
    private final String name;
    private final String city;
    private final String country;
    private final String timezone;

    public Airport(String code, String name, String city, String country, String timezone) {
        this.code = code;
        this.name = name;
        this.city = city;
        this.country = country;
        this.timezone = timezone;
    }

    public String getCode() {
        return code;
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

    public String getTimezone() {
        return timezone;
    }

    @Override
    public String toString() {
        return String.format("%s (%s, %s)", code, city, country);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Airport airport = (Airport) o;
        return code.equals(airport.code);
    }

    @Override
    public int hashCode() {
        return code.hashCode();
    }
}



