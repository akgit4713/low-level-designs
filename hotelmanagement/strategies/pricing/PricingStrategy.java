package hotelmanagement.strategies.pricing;

import hotelmanagement.models.Room;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Strategy interface for calculating room rates
 * Follows Strategy Pattern - allows different pricing algorithms
 */
public interface PricingStrategy {
    
    /**
     * Calculate the rate for a room on a specific date
     * @param room The room to price
     * @param date The date for which to calculate the rate
     * @return The room rate for that date
     */
    BigDecimal calculateRate(Room room, LocalDate date);
    
    /**
     * Calculate total rate for a date range
     * @param room The room to price
     * @param checkIn Check-in date
     * @param checkOut Check-out date
     * @return Total room rate for the stay
     */
    default BigDecimal calculateTotalRate(Room room, LocalDate checkIn, LocalDate checkOut) {
        BigDecimal total = BigDecimal.ZERO;
        LocalDate current = checkIn;
        while (current.isBefore(checkOut)) {
            total = total.add(calculateRate(room, current));
            current = current.plusDays(1);
        }
        return total;
    }
    
    /**
     * Get the name of this pricing strategy
     */
    String getStrategyName();
}



