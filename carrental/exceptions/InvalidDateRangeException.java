package carrental.exceptions;

import java.time.LocalDate;

/**
 * Thrown when an invalid date range is provided.
 */
public class InvalidDateRangeException extends CarRentalException {
    
    public InvalidDateRangeException(LocalDate startDate, LocalDate endDate) {
        super(String.format("Invalid date range: %s to %s. Start date must be before end date and not in the past.", 
            startDate, endDate));
    }

    public InvalidDateRangeException(String message) {
        super(message);
    }
}



