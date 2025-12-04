package onlineshopping.strategies.shipping;

import onlineshopping.models.Address;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Standard shipping - free above threshold, else flat rate
 */
public class StandardShippingStrategy implements ShippingStrategy {
    
    private static final BigDecimal FREE_SHIPPING_THRESHOLD = new BigDecimal("50");
    private static final BigDecimal FLAT_RATE = new BigDecimal("5.99");
    private static final int DELIVERY_DAYS = 5;

    @Override
    public BigDecimal calculateCost(Address destination, BigDecimal orderValue, double weight) {
        if (orderValue.compareTo(FREE_SHIPPING_THRESHOLD) >= 0) {
            return BigDecimal.ZERO;
        }
        return FLAT_RATE;
    }

    @Override
    public LocalDateTime getEstimatedDelivery() {
        return LocalDateTime.now().plusDays(DELIVERY_DAYS);
    }

    @Override
    public String getName() {
        return "Standard Shipping (5-7 business days)";
    }

    @Override
    public boolean isAvailable(Address destination) {
        // Standard shipping available everywhere
        return true;
    }

    /**
     * Get free shipping threshold
     */
    public static BigDecimal getFreeShippingThreshold() {
        return FREE_SHIPPING_THRESHOLD;
    }
}



