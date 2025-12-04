package fooddelivery.strategies.pricing;

import fooddelivery.models.Location;
import fooddelivery.models.Order;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalTime;

/**
 * Surge pricing based on time of day and demand.
 */
public class SurgePricingStrategy implements DeliveryFeeStrategy {
    
    private final DeliveryFeeStrategy baseStrategy;
    
    public SurgePricingStrategy(DeliveryFeeStrategy baseStrategy) {
        this.baseStrategy = baseStrategy;
    }

    @Override
    public BigDecimal calculateFee(Order order, Location restaurantLocation, Location deliveryLocation) {
        BigDecimal baseFee = baseStrategy.calculateFee(order, restaurantLocation, deliveryLocation);
        BigDecimal multiplier = getSurgeMultiplier();
        
        return baseFee.multiply(multiplier).setScale(2, RoundingMode.HALF_UP);
    }
    
    private BigDecimal getSurgeMultiplier() {
        LocalTime now = LocalTime.now();
        
        // Peak hours: 12-2 PM (lunch) and 7-10 PM (dinner)
        if ((now.isAfter(LocalTime.of(12, 0)) && now.isBefore(LocalTime.of(14, 0))) ||
            (now.isAfter(LocalTime.of(19, 0)) && now.isBefore(LocalTime.of(22, 0)))) {
            return new BigDecimal("1.5"); // 1.5x surge
        }
        
        // Late night: 10 PM - 12 AM
        if (now.isAfter(LocalTime.of(22, 0))) {
            return new BigDecimal("1.3"); // 1.3x surge
        }
        
        return BigDecimal.ONE; // No surge
    }

    @Override
    public String getStrategyName() {
        return "Surge Pricing";
    }
}



