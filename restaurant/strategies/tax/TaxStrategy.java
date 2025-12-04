package restaurant.strategies.tax;

import restaurant.models.Order;
import java.math.BigDecimal;

/**
 * Strategy interface for calculating taxes
 * Allows different tax rules for different order types or locations
 */
public interface TaxStrategy {
    
    /**
     * Calculate tax amount
     * @param order The order
     * @param taxableAmount The amount to calculate tax on
     * @return The tax amount
     */
    BigDecimal calculateTax(Order order, BigDecimal taxableAmount);
    
    /**
     * Get tax rate as decimal (e.g., 0.18 for 18%)
     */
    BigDecimal getTaxRate();
    
    /**
     * Get tax name for display
     */
    String getTaxName();
    
    /**
     * Check if tax is applicable to the order
     */
    boolean isApplicable(Order order);
}

