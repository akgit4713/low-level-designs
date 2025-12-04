package ridesharing.models;

import ridesharing.enums.DriverStatus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Represents a driver who can accept and fulfill rides.
 */
public class Driver extends User {
    private Vehicle vehicle;
    private String licenseNumber;
    private DriverStatus status;
    private Location currentLocation;
    private final List<String> rideHistory;
    private double totalEarnings;
    private String currentRideId;

    public Driver(String userId, String name, String email, String phone) {
        super(userId, name, email, phone);
        this.status = DriverStatus.OFFLINE;
        this.rideHistory = new ArrayList<>();
        this.totalEarnings = 0.0;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public void setVehicle(Vehicle vehicle) {
        this.vehicle = Objects.requireNonNull(vehicle, "Vehicle is required for driver");
    }

    public String getLicenseNumber() {
        return licenseNumber;
    }

    public void setLicenseNumber(String licenseNumber) {
        this.licenseNumber = licenseNumber;
    }

    public DriverStatus getStatus() {
        return status;
    }

    public void setStatus(DriverStatus status) {
        this.status = status;
    }

    public Location getCurrentLocation() {
        return currentLocation;
    }

    public void setCurrentLocation(Location currentLocation) {
        this.currentLocation = currentLocation;
    }

    public List<String> getRideHistory() {
        return Collections.unmodifiableList(rideHistory);
    }

    public void addRideToHistory(String rideId) {
        rideHistory.add(rideId);
    }

    public double getTotalEarnings() {
        return totalEarnings;
    }

    public void addEarnings(double amount) {
        this.totalEarnings += amount;
    }

    public String getCurrentRideId() {
        return currentRideId;
    }

    public void setCurrentRideId(String currentRideId) {
        this.currentRideId = currentRideId;
    }

    public boolean isAvailable() {
        return status == DriverStatus.AVAILABLE;
    }

    public void goOnline() {
        if (currentLocation == null) {
            throw new IllegalStateException("Location must be set before going online");
        }
        this.status = DriverStatus.AVAILABLE;
    }

    public void goOffline() {
        if (status == DriverStatus.BUSY) {
            throw new IllegalStateException("Cannot go offline while on a ride");
        }
        this.status = DriverStatus.OFFLINE;
    }

    @Override
    public String toString() {
        return String.format("Driver{id='%s', name='%s', status=%s, rating=%.2f, vehicle=%s}", 
                userId, name, status, rating, vehicle);
    }
}



