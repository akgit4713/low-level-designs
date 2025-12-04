package concertbooking.strategies.pricing;

import concertbooking.models.Concert;
import concertbooking.models.Seat;

import java.math.BigDecimal;
import java.util.List;

/**
 * Strategy interface for calculating ticket prices
 */
public interface PricingStrategy {
    
    /**
     * Calculate the total price for selected seats
     * @param concert The concert
     * @param seats The seats to price
     * @return Total calculated price
     */
    BigDecimal calculatePrice(Concert concert, List<Seat> seats);
    
    /**
     * Get the strategy name
     */
    String getStrategyName();
}



