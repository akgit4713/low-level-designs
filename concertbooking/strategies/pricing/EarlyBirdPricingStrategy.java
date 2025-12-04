package concertbooking.strategies.pricing;

import concertbooking.models.Concert;
import concertbooking.models.Seat;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * Early bird pricing strategy - offers discounts for advance bookings
 */
public class EarlyBirdPricingStrategy implements PricingStrategy {
    
    private final int earlyBirdDays;
    private final BigDecimal discountPercentage;
    
    public EarlyBirdPricingStrategy() {
        this(30, new BigDecimal("0.15")); // Default: 15% off if booked 30+ days in advance
    }
    
    public EarlyBirdPricingStrategy(int earlyBirdDays, BigDecimal discountPercentage) {
        this.earlyBirdDays = earlyBirdDays;
        this.discountPercentage = discountPercentage;
    }
    
    @Override
    public BigDecimal calculatePrice(Concert concert, List<Seat> seats) {
        BigDecimal baseTotal = seats.stream()
            .map(seat -> concert.getSectionPrice(seat.getSectionType()))
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        long daysToEvent = ChronoUnit.DAYS.between(LocalDateTime.now(), concert.getDateTime());
        
        if (daysToEvent >= earlyBirdDays) {
            BigDecimal discount = baseTotal.multiply(discountPercentage);
            return baseTotal.subtract(discount).setScale(2, RoundingMode.HALF_UP);
        }
        
        return baseTotal;
    }
    
    @Override
    public String getStrategyName() {
        return "Early Bird Pricing (" + (discountPercentage.multiply(new BigDecimal("100")).intValue()) 
            + "% off for " + earlyBirdDays + "+ days advance)";
    }
}



