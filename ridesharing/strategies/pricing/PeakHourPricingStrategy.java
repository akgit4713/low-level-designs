package ridesharing.strategies.pricing;

import ridesharing.enums.RideType;
import ridesharing.models.Fare;

import java.time.LocalTime;

/**
 * Time-based pricing strategy with peak hour adjustments.
 */
public class PeakHourPricingStrategy implements PricingStrategy {
    
    private final PricingStrategy basePricingStrategy;
    
    // Peak hours: 7-9 AM and 5-8 PM
    private static final LocalTime MORNING_PEAK_START = LocalTime.of(7, 0);
    private static final LocalTime MORNING_PEAK_END = LocalTime.of(9, 0);
    private static final LocalTime EVENING_PEAK_START = LocalTime.of(17, 0);
    private static final LocalTime EVENING_PEAK_END = LocalTime.of(20, 0);
    
    private static final double PEAK_MULTIPLIER = 1.25;
    private static final double OFF_PEAK_MULTIPLIER = 0.9; // Discount during off-peak

    public PeakHourPricingStrategy(PricingStrategy basePricingStrategy) {
        this.basePricingStrategy = basePricingStrategy;
    }

    @Override
    public Fare calculateFare(double distanceKm, long durationMinutes, RideType rideType) {
        Fare baseFare = basePricingStrategy.calculateFare(distanceKm, durationMinutes, rideType);
        double multiplier = getCurrentMultiplier();
        
        return Fare.builder()
                .baseFare(baseFare.getBaseFare())
                .distanceFare(baseFare.getDistanceFare())
                .timeFare(baseFare.getTimeFare())
                .surgeMultiplier(multiplier)
                .rideType(rideType)
                .build();
    }

    private double getCurrentMultiplier() {
        LocalTime now = LocalTime.now();
        
        if (isPeakHour(now)) {
            return PEAK_MULTIPLIER;
        } else if (isOffPeakHour(now)) {
            return OFF_PEAK_MULTIPLIER;
        }
        return 1.0;
    }

    private boolean isPeakHour(LocalTime time) {
        return (time.isAfter(MORNING_PEAK_START) && time.isBefore(MORNING_PEAK_END)) ||
               (time.isAfter(EVENING_PEAK_START) && time.isBefore(EVENING_PEAK_END));
    }

    private boolean isOffPeakHour(LocalTime time) {
        // Late night hours: 11 PM - 5 AM
        return time.isAfter(LocalTime.of(23, 0)) || time.isBefore(LocalTime.of(5, 0));
    }

    @Override
    public String getStrategyName() {
        return "Peak Hour Pricing";
    }
}



