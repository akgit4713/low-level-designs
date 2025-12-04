package carrental.exceptions;

/**
 * Thrown when a car cannot be found in the system.
 */
public class CarNotFoundException extends CarRentalException {
    
    public CarNotFoundException(String carId) {
        super("Car not found with ID: " + carId);
    }
}



