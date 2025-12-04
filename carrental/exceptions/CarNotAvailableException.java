package carrental.exceptions;

import java.time.LocalDate;

/**
 * Thrown when a car is not available for the requested dates.
 */
public class CarNotAvailableException extends CarRentalException {
    
    public CarNotAvailableException(String carId, LocalDate startDate, LocalDate endDate) {
        super(String.format("Car %s is not available from %s to %s", carId, startDate, endDate));
    }

    public CarNotAvailableException(String message) {
        super(message);
    }
}



