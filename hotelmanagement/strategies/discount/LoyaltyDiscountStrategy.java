package hotelmanagement.strategies.discount;

import hotelmanagement.models.Reservation;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Discount strategy based on guest loyalty tier
 */
public class LoyaltyDiscountStrategy implements DiscountStrategy {
    
    @Override
    public BigDecimal calculateDiscount(Reservation reservation, BigDecimal subtotal) {
        int discountPercent = reservation.getGuest().getLoyaltyDiscountPercent();
        
        if (discountPercent == 0) {
            return BigDecimal.ZERO;
        }
        
        return subtotal
            .multiply(BigDecimal.valueOf(discountPercent))
            .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
    }
    
    @Override
    public boolean isApplicable(Reservation reservation) {
        return reservation.getGuest().getLoyaltyDiscountPercent() > 0;
    }
    
    @Override
    public String getDiscountName() {
        return "Loyalty Member Discount";
    }
    
    @Override
    public int getPriority() {
        return 10; // High priority - apply loyalty discounts first
    }
}



