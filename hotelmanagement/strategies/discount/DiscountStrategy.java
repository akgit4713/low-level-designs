package hotelmanagement.strategies.discount;

import hotelmanagement.models.Reservation;

import java.math.BigDecimal;

/**
 * Strategy interface for calculating discounts
 * Follows Strategy Pattern - allows different discount algorithms
 */
public interface DiscountStrategy {
    
    /**
     * Calculate the discount amount for a reservation
     * @param reservation The reservation to apply discount to
     * @param subtotal The subtotal before discount
     * @return The discount amount
     */
    BigDecimal calculateDiscount(Reservation reservation, BigDecimal subtotal);
    
    /**
     * Check if this discount is applicable to the reservation
     * @param reservation The reservation to check
     * @return true if discount is applicable
     */
    boolean isApplicable(Reservation reservation);
    
    /**
     * Get the name/description of this discount
     */
    String getDiscountName();
    
    /**
     * Get priority - lower number means higher priority
     * Used when multiple discounts are applicable
     */
    default int getPriority() {
        return 100;
    }
}



