package carrental.strategies.pricing;

import carrental.models.Car;
import carrental.models.Reservation;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Strategy interface for calculating rental prices.
 * Follows Strategy Pattern to allow different pricing algorithms.
 * 
 * Extension Point: Add new pricing strategies without modifying existing code (OCP).
 */
public interface PricingStrategy {
    
    /**
     * Calculates the total rental price for a car within a date range.
     * 
     * @param car The car being rented
     * @param startDate Start date of rental
     * @param endDate End date of rental
     * @return Total rental price
     */
    BigDecimal calculatePrice(Car car, LocalDate startDate, LocalDate endDate);
    
    /**
     * Calculates the price for an existing reservation.
     */
    default BigDecimal calculatePrice(Reservation reservation) {
        return calculatePrice(reservation.getCar(), reservation.getStartDate(), reservation.getEndDate());
    }
    
    /**
     * Returns the name of this pricing strategy.
     */
    String getStrategyName();
}



