package onlineshopping.strategies.shipping;

import onlineshopping.models.Address;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Strategy interface for shipping cost calculation
 */
public interface ShippingStrategy {
    
    /**
     * Calculate shipping cost
     * @param destination shipping address
     * @param orderValue total order value
     * @param weight total weight in kg (optional, for weight-based shipping)
     * @return shipping cost
     */
    BigDecimal calculateCost(Address destination, BigDecimal orderValue, double weight);
    
    /**
     * Get estimated delivery date
     */
    LocalDateTime getEstimatedDelivery();
    
    /**
     * Get display name
     */
    String getName();
    
    /**
     * Check if this shipping method is available for the destination
     */
    boolean isAvailable(Address destination);
}



