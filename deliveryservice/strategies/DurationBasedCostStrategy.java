package deliveryservice.strategies;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDateTime;

/**
 * Calculates cost based on the duration of the delivery.
 * Cost = duration in minutes * rate per minute
 * 
 * Uses BigDecimal for precise financial calculations.
 */
public class DurationBasedCostStrategy implements CostCalculationStrategy {
    
    private static final int SCALE = 2;  // 2 decimal places for currency
    private static final RoundingMode ROUNDING_MODE = RoundingMode.HALF_UP;
    
    private final BigDecimal ratePerMinute;

    /**
     * Creates a duration-based cost strategy with default rate of 1.0 per minute.
     */
    public DurationBasedCostStrategy() {
        this(BigDecimal.ONE);
    }

    /**
     * Creates a duration-based cost strategy with custom rate.
     * 
     * @param ratePerMinute the cost rate per minute
     */
    public DurationBasedCostStrategy(BigDecimal ratePerMinute) {
        if (ratePerMinute == null) {
            throw new IllegalArgumentException("Rate per minute cannot be null");
        }
        if (ratePerMinute.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Rate per minute cannot be negative");
        }
        this.ratePerMinute = ratePerMinute;
    }

    /**
     * Convenience constructor accepting double (converted to BigDecimal).
     * Use String-based BigDecimal constructor for exact values when possible.
     * 
     * @param ratePerMinute the cost rate per minute
     */
    public DurationBasedCostStrategy(String ratePerMinute) {
        this(new BigDecimal(ratePerMinute));
    }

    @Override
    public BigDecimal calculateCost(LocalDateTime startTime, LocalDateTime endTime) {
        if (startTime == null || endTime == null) {
            throw new IllegalArgumentException("Start time and end time cannot be null");
        }
        if (endTime.isBefore(startTime)) {
            throw new IllegalArgumentException("End time cannot be before start time");
        }
        
        Duration duration = Duration.between(startTime, endTime);
        long minutes = duration.toMinutes();
        
        return ratePerMinute
                .multiply(BigDecimal.valueOf(minutes))
                .setScale(SCALE, ROUNDING_MODE);
    }

    public BigDecimal getRatePerMinute() {
        return ratePerMinute;
    }
}
