package onlineshopping.strategies.pricing;

import onlineshopping.models.Cart;

import java.math.BigDecimal;

/**
 * Strategy interface for pricing and discount calculation
 */
public interface PricingStrategy {
    
    /**
     * Calculate discount amount for the cart
     * @param cart the shopping cart
     * @param subtotal the cart subtotal
     * @return discount amount
     */
    BigDecimal calculateDiscount(Cart cart, BigDecimal subtotal);
    
    /**
     * Get the strategy name for display
     */
    String getName();
    
    /**
     * Get description of the discount
     */
    String getDescription();
    
    /**
     * Check if this discount is applicable
     */
    boolean isApplicable(Cart cart, BigDecimal subtotal);
}



