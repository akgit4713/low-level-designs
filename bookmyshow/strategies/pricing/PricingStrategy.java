package bookmyshow.strategies.pricing;

import bookmyshow.models.Show;
import bookmyshow.models.ShowSeat;
import java.math.BigDecimal;
import java.util.List;

/**
 * Strategy interface for calculating ticket prices.
 * Implementations can define different pricing logic (base, weekend, surge, etc.)
 */
public interface PricingStrategy {
    
    /**
     * Calculate total price for selected seats.
     * @param show The show for which tickets are being booked
     * @param showSeats List of seats being booked
     * @return Total price for all seats
     */
    BigDecimal calculateTotalPrice(Show show, List<ShowSeat> showSeats);
    
    /**
     * Calculate price for a single seat.
     * @param show The show for which ticket is being booked
     * @param showSeat The specific seat
     * @return Price for the seat
     */
    BigDecimal calculateSeatPrice(Show show, ShowSeat showSeat);
    
    /**
     * Get the name/description of this pricing strategy.
     * @return Strategy name
     */
    String getStrategyName();
}



