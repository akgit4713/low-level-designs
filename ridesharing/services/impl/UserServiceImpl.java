package ridesharing.services.impl;

import ridesharing.enums.DriverStatus;
import ridesharing.exceptions.DriverNotFoundException;
import ridesharing.models.Driver;
import ridesharing.models.Location;
import ridesharing.models.Passenger;
import ridesharing.models.Vehicle;
import ridesharing.repositories.DriverRepository;
import ridesharing.repositories.PassengerRepository;
import ridesharing.services.UserService;

import java.util.Optional;
import java.util.UUID;

/**
 * Implementation of UserService.
 * Manages passenger and driver accounts.
 */
public class UserServiceImpl implements UserService {
    
    private final PassengerRepository passengerRepository;
    private final DriverRepository driverRepository;

    public UserServiceImpl(PassengerRepository passengerRepository,
                          DriverRepository driverRepository) {
        this.passengerRepository = passengerRepository;
        this.driverRepository = driverRepository;
    }

    @Override
    public Passenger registerPassenger(String name, String email, String phone) {
        // Check if email already exists
        if (passengerRepository.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("Email already registered");
        }
        
        String passengerId = "P-" + UUID.randomUUID().toString().substring(0, 8);
        Passenger passenger = new Passenger(passengerId, name, email, phone);
        return passengerRepository.save(passenger);
    }

    @Override
    public Optional<Passenger> getPassenger(String passengerId) {
        return passengerRepository.findById(passengerId);
    }

    @Override
    public Driver registerDriver(String name, String email, String phone, 
                                 Vehicle vehicle, String licenseNumber) {
        // Check if email already exists
        if (driverRepository.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("Email already registered");
        }
        
        String driverId = "D-" + UUID.randomUUID().toString().substring(0, 8);
        Driver driver = new Driver(driverId, name, email, phone);
        driver.setVehicle(vehicle);
        driver.setLicenseNumber(licenseNumber);
        return driverRepository.save(driver);
    }

    @Override
    public Optional<Driver> getDriver(String driverId) {
        return driverRepository.findById(driverId);
    }

    @Override
    public void updateDriverLocation(String driverId, Location location) {
        driverRepository.updateLocation(driverId, location);
    }

    @Override
    public void setDriverOnline(String driverId) {
        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new DriverNotFoundException(driverId));
        
        driver.goOnline();
        driverRepository.save(driver);
    }

    @Override
    public void setDriverOffline(String driverId) {
        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new DriverNotFoundException(driverId));
        
        driver.goOffline();
        driverRepository.save(driver);
    }
}



