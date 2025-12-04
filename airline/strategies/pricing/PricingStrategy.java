package airline.strategies.pricing;

import airline.enums.SeatClass;
import airline.models.Flight;

import java.math.BigDecimal;

/**
 * Strategy interface for calculating flight prices.
 * Allows different pricing algorithms (dynamic, seasonal, promotional).
 */
public interface PricingStrategy {
    
    /**
     * Calculates the price for a seat on a flight.
     * 
     * @param flight The flight
     * @param seatClass The seat class
     * @return The calculated price
     */
    BigDecimal calculatePrice(Flight flight, SeatClass seatClass);
    
    /**
     * Gets a description of this pricing strategy.
     */
    String getDescription();
}



