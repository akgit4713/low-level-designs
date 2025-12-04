package ridesharing.strategies.pricing;

import ridesharing.enums.RideType;
import ridesharing.models.Fare;

/**
 * Standard pricing strategy with fixed rates.
 */
public class StandardPricingStrategy implements PricingStrategy {
    
    private static final double BASE_FARE = 2.50;
    private static final double RATE_PER_KM = 1.50;
    private static final double RATE_PER_MINUTE = 0.25;
    private static final double MINIMUM_FARE = 5.00;

    @Override
    public Fare calculateFare(double distanceKm, long durationMinutes, RideType rideType) {
        double distanceFare = distanceKm * RATE_PER_KM;
        double timeFare = durationMinutes * RATE_PER_MINUTE;
        
        Fare fare = Fare.builder()
                .baseFare(BASE_FARE)
                .distanceFare(distanceFare)
                .timeFare(timeFare)
                .surgeMultiplier(1.0)
                .rideType(rideType)
                .build();
        
        // Ensure minimum fare
        if (fare.getTotalAmount() < MINIMUM_FARE) {
            double adjustment = MINIMUM_FARE - fare.getTotalAmount();
            return Fare.builder()
                    .baseFare(BASE_FARE + adjustment)
                    .distanceFare(distanceFare)
                    .timeFare(timeFare)
                    .surgeMultiplier(1.0)
                    .rideType(rideType)
                    .build();
        }
        
        return fare;
    }

    @Override
    public String getStrategyName() {
        return "Standard Pricing";
    }
}



