package concertbooking.strategies.pricing;

import concertbooking.models.Concert;
import concertbooking.models.Seat;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * Dynamic pricing strategy - adjusts prices based on demand and time to event
 */
public class DynamicPricingStrategy implements PricingStrategy {
    
    private static final BigDecimal MIN_MULTIPLIER = new BigDecimal("0.8");  // 20% discount min
    private static final BigDecimal MAX_MULTIPLIER = new BigDecimal("2.0");  // 100% surge max
    
    @Override
    public BigDecimal calculatePrice(Concert concert, List<Seat> seats) {
        BigDecimal demandMultiplier = calculateDemandMultiplier(concert);
        BigDecimal timeMultiplier = calculateTimeMultiplier(concert);
        
        BigDecimal combinedMultiplier = demandMultiplier.multiply(timeMultiplier)
            .setScale(2, RoundingMode.HALF_UP);
        
        // Ensure multiplier stays within bounds
        if (combinedMultiplier.compareTo(MIN_MULTIPLIER) < 0) {
            combinedMultiplier = MIN_MULTIPLIER;
        } else if (combinedMultiplier.compareTo(MAX_MULTIPLIER) > 0) {
            combinedMultiplier = MAX_MULTIPLIER;
        }
        
        final BigDecimal finalMultiplier = combinedMultiplier;
        
        return seats.stream()
            .map(seat -> concert.getSectionPrice(seat.getSectionType())
                .multiply(finalMultiplier)
                .setScale(2, RoundingMode.HALF_UP))
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    /**
     * Calculate multiplier based on demand (percentage of seats sold)
     */
    private BigDecimal calculateDemandMultiplier(Concert concert) {
        int totalSeats = concert.getTotalSeats();
        int bookedSeats = concert.getBookedSeatsCount();
        
        if (totalSeats == 0) return BigDecimal.ONE;
        
        double occupancyRate = (double) bookedSeats / totalSeats;
        
        // Price increases as more seats are sold
        if (occupancyRate > 0.9) {
            return new BigDecimal("1.5"); // 50% surge when >90% sold
        } else if (occupancyRate > 0.75) {
            return new BigDecimal("1.3"); // 30% surge when >75% sold
        } else if (occupancyRate > 0.5) {
            return new BigDecimal("1.15"); // 15% surge when >50% sold
        } else if (occupancyRate < 0.2) {
            return new BigDecimal("0.9"); // 10% discount when <20% sold
        }
        
        return BigDecimal.ONE;
    }
    
    /**
     * Calculate multiplier based on time to event
     */
    private BigDecimal calculateTimeMultiplier(Concert concert) {
        long daysToEvent = ChronoUnit.DAYS.between(LocalDateTime.now(), concert.getDateTime());
        
        if (daysToEvent <= 1) {
            return new BigDecimal("1.25"); // 25% surge for last-minute bookings
        } else if (daysToEvent <= 7) {
            return new BigDecimal("1.1"); // 10% surge within a week
        } else if (daysToEvent > 60) {
            return new BigDecimal("0.95"); // 5% early bird discount
        }
        
        return BigDecimal.ONE;
    }
    
    @Override
    public String getStrategyName() {
        return "Dynamic Pricing";
    }
}



