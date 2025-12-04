package carrental.services;

import carrental.enums.CarType;
import carrental.models.Car;
import carrental.models.SearchCriteria;

import java.time.LocalDate;
import java.util.List;

/**
 * Service interface for car management operations.
 */
public interface CarService {
    
    /**
     * Adds a new car to the system.
     */
    Car addCar(Car car);
    
    /**
     * Gets a car by its ID.
     */
    Car getCarById(String carId);
    
    /**
     * Gets all cars in the system.
     */
    List<Car> getAllCars();
    
    /**
     * Gets all available cars.
     */
    List<Car> getAvailableCars();
    
    /**
     * Gets all cars of a specific type.
     */
    List<Car> getCarsByType(CarType carType);
    
    /**
     * Searches for cars based on criteria.
     */
    List<Car> searchCars(SearchCriteria criteria);
    
    /**
     * Checks if a car is available for the given date range.
     */
    boolean isCarAvailable(String carId, LocalDate startDate, LocalDate endDate);
    
    /**
     * Gets all available cars for a specific date range.
     */
    List<Car> getAvailableCarsForDates(LocalDate startDate, LocalDate endDate);
    
    /**
     * Removes a car from the system.
     */
    boolean removeCar(String carId);
    
    /**
     * Marks a car as under maintenance.
     */
    void markUnderMaintenance(String carId);
    
    /**
     * Marks a car as available.
     */
    void markAvailable(String carId);
}



