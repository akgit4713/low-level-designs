package fooddelivery.strategies.pricing;

import fooddelivery.models.Location;
import fooddelivery.models.Order;
import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Distance-based delivery fee calculation.
 */
public class DistanceBasedFeeStrategy implements DeliveryFeeStrategy {
    
    private final BigDecimal baseFee;
    private final BigDecimal perKmFee;
    private final double freeDeliveryThreshold;
    
    public DistanceBasedFeeStrategy() {
        this.baseFee = new BigDecimal("20.00");
        this.perKmFee = new BigDecimal("8.00");
        this.freeDeliveryThreshold = 500.0; // Free delivery above ₹500
    }
    
    public DistanceBasedFeeStrategy(BigDecimal baseFee, BigDecimal perKmFee, double freeDeliveryThreshold) {
        this.baseFee = baseFee;
        this.perKmFee = perKmFee;
        this.freeDeliveryThreshold = freeDeliveryThreshold;
    }

    @Override
    public BigDecimal calculateFee(Order order, Location restaurantLocation, Location deliveryLocation) {
        // Free delivery for high-value orders
        if (order.getSubtotal().doubleValue() >= freeDeliveryThreshold) {
            return BigDecimal.ZERO;
        }
        
        double distance = restaurantLocation.distanceTo(deliveryLocation);
        BigDecimal distanceFee = perKmFee.multiply(BigDecimal.valueOf(distance));
        
        return baseFee.add(distanceFee).setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public String getStrategyName() {
        return "Distance Based";
    }
}



