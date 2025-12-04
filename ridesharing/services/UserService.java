package ridesharing.services;

import ridesharing.models.Driver;
import ridesharing.models.Location;
import ridesharing.models.Passenger;
import ridesharing.models.Vehicle;

import java.util.Optional;

/**
 * Service interface for user management.
 */
public interface UserService {
    
    // Passenger operations
    Passenger registerPassenger(String name, String email, String phone);
    
    Optional<Passenger> getPassenger(String passengerId);
    
    // Driver operations
    Driver registerDriver(String name, String email, String phone, Vehicle vehicle, String licenseNumber);
    
    Optional<Driver> getDriver(String driverId);
    
    void updateDriverLocation(String driverId, Location location);
    
    void setDriverOnline(String driverId);
    
    void setDriverOffline(String driverId);
}



