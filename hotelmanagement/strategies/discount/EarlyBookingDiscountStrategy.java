package hotelmanagement.strategies.discount;

import hotelmanagement.models.Reservation;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * Discount strategy for early bookings (30+ days in advance)
 */
public class EarlyBookingDiscountStrategy implements DiscountStrategy {
    
    private final int minimumDaysInAdvance;
    private final int discountPercent;
    
    public EarlyBookingDiscountStrategy() {
        this(30, 15); // 15% discount for 30+ days advance booking
    }
    
    public EarlyBookingDiscountStrategy(int minimumDaysInAdvance, int discountPercent) {
        this.minimumDaysInAdvance = minimumDaysInAdvance;
        this.discountPercent = discountPercent;
    }
    
    @Override
    public BigDecimal calculateDiscount(Reservation reservation, BigDecimal subtotal) {
        long daysInAdvance = ChronoUnit.DAYS.between(
            reservation.getCreatedAt().toLocalDate(),
            reservation.getCheckInDate()
        );
        
        if (daysInAdvance < minimumDaysInAdvance) {
            return BigDecimal.ZERO;
        }
        
        return subtotal
            .multiply(BigDecimal.valueOf(discountPercent))
            .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
    }
    
    @Override
    public boolean isApplicable(Reservation reservation) {
        long daysInAdvance = ChronoUnit.DAYS.between(
            reservation.getCreatedAt().toLocalDate(),
            reservation.getCheckInDate()
        );
        return daysInAdvance >= minimumDaysInAdvance;
    }
    
    @Override
    public String getDiscountName() {
        return "Early Booking Discount (" + minimumDaysInAdvance + "+ days advance)";
    }
    
    @Override
    public int getPriority() {
        return 30;
    }
}



