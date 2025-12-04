package fooddelivery.strategies.pricing;

import fooddelivery.models.Location;
import fooddelivery.models.Order;
import java.math.BigDecimal;

/**
 * Strategy interface for calculating delivery fees.
 * Implements Strategy Pattern for different pricing models.
 */
public interface DeliveryFeeStrategy {
    
    /**
     * Calculate delivery fee based on order and locations.
     * @param order The order
     * @param restaurantLocation Restaurant pickup location
     * @param deliveryLocation Customer delivery location
     * @return Calculated delivery fee
     */
    BigDecimal calculateFee(Order order, Location restaurantLocation, Location deliveryLocation);
    
    /**
     * Get strategy name for display.
     */
    String getStrategyName();
}



