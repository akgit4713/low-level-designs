package ridesharing.strategies.pricing;

import ridesharing.enums.RideType;
import ridesharing.models.Fare;

/**
 * Dynamic surge pricing strategy based on demand.
 * Wraps another pricing strategy and applies surge multiplier.
 * Follows Decorator Pattern.
 */
public class SurgePricingStrategy implements PricingStrategy {
    
    private final PricingStrategy basePricingStrategy;
    private final double surgeMultiplier;
    private static final double MAX_SURGE = 3.0;
    private static final double MIN_SURGE = 1.0;

    public SurgePricingStrategy(PricingStrategy basePricingStrategy, double surgeMultiplier) {
        this.basePricingStrategy = basePricingStrategy;
        this.surgeMultiplier = Math.min(MAX_SURGE, Math.max(MIN_SURGE, surgeMultiplier));
    }

    /**
     * Factory method to calculate surge based on supply/demand ratio.
     */
    public static SurgePricingStrategy fromDemand(PricingStrategy basePricingStrategy, 
                                                   int availableDrivers, 
                                                   int pendingRequests) {
        double ratio = pendingRequests > 0 ? (double) pendingRequests / Math.max(1, availableDrivers) : 1.0;
        double surge = calculateSurgeFromRatio(ratio);
        return new SurgePricingStrategy(basePricingStrategy, surge);
    }

    private static double calculateSurgeFromRatio(double ratio) {
        if (ratio <= 1.0) return 1.0;
        if (ratio <= 2.0) return 1.25;
        if (ratio <= 3.0) return 1.5;
        if (ratio <= 4.0) return 2.0;
        return 2.5;
    }

    @Override
    public Fare calculateFare(double distanceKm, long durationMinutes, RideType rideType) {
        Fare baseFare = basePricingStrategy.calculateFare(distanceKm, durationMinutes, rideType);
        
        return Fare.builder()
                .baseFare(baseFare.getBaseFare())
                .distanceFare(baseFare.getDistanceFare())
                .timeFare(baseFare.getTimeFare())
                .surgeMultiplier(surgeMultiplier)
                .discount(baseFare.getDiscount())
                .rideType(rideType)
                .build();
    }

    @Override
    public String getStrategyName() {
        return String.format("Surge Pricing (%.2fx)", surgeMultiplier);
    }

    public double getSurgeMultiplier() {
        return surgeMultiplier;
    }
}



