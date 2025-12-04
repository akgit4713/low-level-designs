package carrental.repositories;

import carrental.enums.CarStatus;
import carrental.enums.CarType;
import carrental.models.Car;

import java.util.List;

/**
 * Repository interface for Car entities.
 * Extends the base Repository with car-specific query methods.
 */
public interface CarRepository extends Repository<Car, String> {
    
    /**
     * Finds a car by its license plate number.
     */
    Car findByLicensePlate(String licensePlate);
    
    /**
     * Finds all cars with a specific status.
     */
    List<Car> findByStatus(CarStatus status);
    
    /**
     * Finds all cars of a specific type.
     */
    List<Car> findByType(CarType carType);
    
    /**
     * Finds all available cars.
     */
    List<Car> findAvailable();
    
    /**
     * Finds all cars by make.
     */
    List<Car> findByMake(String make);
}



