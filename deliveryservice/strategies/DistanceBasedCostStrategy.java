package deliveryservice.strategies;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDateTime;

/**
 * Example of an extensible cost strategy based on distance.
 * This demonstrates how the system can be extended with different pricing models.
 * 
 * Note: In a real implementation, you would pass distance as a parameter
 * or calculate it from location data.
 * 
 * Uses BigDecimal for precise financial calculations.
 */
public class DistanceBasedCostStrategy implements CostCalculationStrategy {
    
    private static final int SCALE = 2;
    private static final RoundingMode ROUNDING_MODE = RoundingMode.HALF_UP;
    private static final BigDecimal MINUTES_PER_HOUR = BigDecimal.valueOf(60);
    
    private final BigDecimal baseFare;
    private final BigDecimal ratePerKm;
    private final BigDecimal estimatedSpeedKmPerHour;

    public DistanceBasedCostStrategy(String baseFare, String ratePerKm, String estimatedSpeedKmPerHour) {
        this.baseFare = new BigDecimal(baseFare);
        this.ratePerKm = new BigDecimal(ratePerKm);
        this.estimatedSpeedKmPerHour = new BigDecimal(estimatedSpeedKmPerHour);
    }

    public DistanceBasedCostStrategy(BigDecimal baseFare, BigDecimal ratePerKm, BigDecimal estimatedSpeedKmPerHour) {
        this.baseFare = baseFare;
        this.ratePerKm = ratePerKm;
        this.estimatedSpeedKmPerHour = estimatedSpeedKmPerHour;
    }

    @Override
    public BigDecimal calculateCost(LocalDateTime startTime, LocalDateTime endTime) {
        if (startTime == null || endTime == null) {
            throw new IllegalArgumentException("Start time and end time cannot be null");
        }
        if (endTime.isBefore(startTime)) {
            throw new IllegalArgumentException("End time cannot be before start time");
        }

        // Estimate distance from duration (simplified)
        Duration duration = Duration.between(startTime, endTime);
        BigDecimal minutes = BigDecimal.valueOf(duration.toMinutes());
        BigDecimal hours = minutes.divide(MINUTES_PER_HOUR, 10, ROUNDING_MODE);
        BigDecimal estimatedDistance = hours.multiply(estimatedSpeedKmPerHour);
        
        return baseFare
                .add(estimatedDistance.multiply(ratePerKm))
                .setScale(SCALE, ROUNDING_MODE);
    }
}
