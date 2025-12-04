package ridesharing.models;

import ridesharing.exceptions.InvalidLocationException;

import java.util.Objects;

/**
 * Represents a geographical location with latitude and longitude.
 * Immutable value object.
 */
public class Location {
    private final double latitude;
    private final double longitude;
    private final String address;

    public Location(double latitude, double longitude, String address) {
        validateCoordinates(latitude, longitude);
        this.latitude = latitude;
        this.longitude = longitude;
        this.address = address;
    }

    public Location(double latitude, double longitude) {
        this(latitude, longitude, null);
    }

    private void validateCoordinates(double latitude, double longitude) {
        if (latitude < -90 || latitude > 90) {
            throw new InvalidLocationException("Latitude must be between -90 and 90");
        }
        if (longitude < -180 || longitude > 180) {
            throw new InvalidLocationException("Longitude must be between -180 and 180");
        }
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getAddress() {
        return address;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Location location = (Location) o;
        return Double.compare(location.latitude, latitude) == 0 &&
               Double.compare(location.longitude, longitude) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(latitude, longitude);
    }

    @Override
    public String toString() {
        return String.format("Location{lat=%.6f, lng=%.6f, address='%s'}", 
                latitude, longitude, address);
    }
}



