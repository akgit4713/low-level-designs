package deliveryservice.strategies;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Strategy interface for calculating delivery cost.
 * Allows different cost calculation algorithms to be plugged in.
 * 
 * Uses BigDecimal to avoid floating-point precision issues in financial calculations.
 */
public interface CostCalculationStrategy {
    
    /**
     * Calculate the cost of a delivery based on start and end times.
     * 
     * @param startTime the delivery start time
     * @param endTime the delivery end time
     * @return the calculated cost as BigDecimal for precision
     */
    BigDecimal calculateCost(LocalDateTime startTime, LocalDateTime endTime);
}
