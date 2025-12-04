package airline.strategies.pricing;

import airline.enums.SeatClass;
import airline.models.Flight;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * Dynamic pricing that adjusts based on:
 * - Seat availability (scarcity pricing)
 * - Time until departure (last-minute pricing)
 */
public class DynamicPricingStrategy implements PricingStrategy {
    
    private final double scarcityThreshold; // Below this % availability, increase price
    private final double scarcityMultiplier;
    private final int lastMinuteDays; // Days before departure for last-minute pricing
    private final double lastMinuteMultiplier;

    public DynamicPricingStrategy() {
        this(0.30, 1.25, 7, 1.15);
    }

    public DynamicPricingStrategy(double scarcityThreshold, double scarcityMultiplier,
                                   int lastMinuteDays, double lastMinuteMultiplier) {
        this.scarcityThreshold = scarcityThreshold;
        this.scarcityMultiplier = scarcityMultiplier;
        this.lastMinuteDays = lastMinuteDays;
        this.lastMinuteMultiplier = lastMinuteMultiplier;
    }

    @Override
    public BigDecimal calculatePrice(Flight flight, SeatClass seatClass) {
        BigDecimal basePrice = getBasePrice(flight, seatClass);
        
        // Apply scarcity multiplier
        double availabilityRatio = getAvailabilityRatio(flight, seatClass);
        if (availabilityRatio < scarcityThreshold) {
            basePrice = basePrice.multiply(BigDecimal.valueOf(scarcityMultiplier));
        }
        
        // Apply last-minute multiplier
        long daysUntilDeparture = ChronoUnit.DAYS.between(LocalDateTime.now(), flight.getDepartureTime());
        if (daysUntilDeparture >= 0 && daysUntilDeparture <= lastMinuteDays) {
            basePrice = basePrice.multiply(BigDecimal.valueOf(lastMinuteMultiplier));
        }
        
        return basePrice.setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal getBasePrice(Flight flight, SeatClass seatClass) {
        BigDecimal basePrice = flight.getBasePrice(seatClass);
        if (basePrice == null || basePrice.compareTo(BigDecimal.ZERO) == 0) {
            basePrice = flight.getBasePrice(SeatClass.ECONOMY);
            if (basePrice == null) {
                basePrice = new BigDecimal("100.00");
            }
            return basePrice.multiply(seatClass.getPriceMultiplier());
        }
        return basePrice;
    }

    private double getAvailabilityRatio(Flight flight, SeatClass seatClass) {
        int total = flight.getAircraft().getSeatCount(seatClass);
        if (total == 0) return 1.0;
        int available = flight.getAvailableSeatCount(seatClass);
        return (double) available / total;
    }

    @Override
    public String getDescription() {
        return String.format("Dynamic pricing (scarcity: %.0f%% threshold, last-minute: %d days)",
                scarcityThreshold * 100, lastMinuteDays);
    }
}



