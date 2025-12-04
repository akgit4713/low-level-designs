package hotelmanagement.strategies.discount;

import hotelmanagement.models.Reservation;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Discount strategy for long stays (7+ nights)
 */
public class LongStayDiscountStrategy implements DiscountStrategy {
    
    private final int minimumNights;
    private final int discountPercent;
    
    public LongStayDiscountStrategy() {
        this(7, 10); // 10% discount for 7+ nights by default
    }
    
    public LongStayDiscountStrategy(int minimumNights, int discountPercent) {
        this.minimumNights = minimumNights;
        this.discountPercent = discountPercent;
    }
    
    @Override
    public BigDecimal calculateDiscount(Reservation reservation, BigDecimal subtotal) {
        long nights = reservation.getNumberOfNights();
        
        if (nights < minimumNights) {
            return BigDecimal.ZERO;
        }
        
        // Progressive discount: +2% for every additional week
        int extraWeeks = (int) ((nights - minimumNights) / 7);
        int totalDiscountPercent = discountPercent + (extraWeeks * 2);
        
        // Cap at 25%
        totalDiscountPercent = Math.min(totalDiscountPercent, 25);
        
        return subtotal
            .multiply(BigDecimal.valueOf(totalDiscountPercent))
            .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
    }
    
    @Override
    public boolean isApplicable(Reservation reservation) {
        return reservation.getNumberOfNights() >= minimumNights;
    }
    
    @Override
    public String getDiscountName() {
        return "Long Stay Discount (" + minimumNights + "+ nights)";
    }
    
    @Override
    public int getPriority() {
        return 20;
    }
}



