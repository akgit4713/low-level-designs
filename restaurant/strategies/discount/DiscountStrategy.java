package restaurant.strategies.discount;

import restaurant.models.Order;
import java.math.BigDecimal;

/**
 * Strategy interface for calculating discounts
 * Follows Strategy Pattern - allows adding new discount types without modifying billing logic
 */
public interface DiscountStrategy {
    
    /**
     * Calculate the discount amount for an order
     * @param order The order to calculate discount for
     * @param subtotal The subtotal before discount
     * @return The discount amount
     */
    BigDecimal calculateDiscount(Order order, BigDecimal subtotal);
    
    /**
     * Check if this discount is applicable to the order
     * @param order The order to check
     * @return true if discount can be applied
     */
    boolean isApplicable(Order order);
    
    /**
     * Get the discount name for display on bill
     */
    String getDiscountName();
    
    /**
     * Get discount description
     */
    String getDescription();
}

