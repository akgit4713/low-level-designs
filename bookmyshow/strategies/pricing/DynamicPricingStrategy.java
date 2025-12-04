package bookmyshow.strategies.pricing;

import bookmyshow.models.Show;
import bookmyshow.models.ShowSeat;
import java.math.BigDecimal;
import java.util.List;

/**
 * Dynamic pricing based on seat occupancy/demand.
 * Higher prices when show is filling up.
 */
public class DynamicPricingStrategy implements PricingStrategy {
    
    private static final double HIGH_DEMAND_THRESHOLD = 0.70;  // 70% filled
    private static final double VERY_HIGH_DEMAND_THRESHOLD = 0.90;  // 90% filled
    private static final BigDecimal HIGH_DEMAND_MULTIPLIER = BigDecimal.valueOf(1.15);
    private static final BigDecimal VERY_HIGH_DEMAND_MULTIPLIER = BigDecimal.valueOf(1.30);
    
    private final PricingStrategy basePricingStrategy;

    public DynamicPricingStrategy() {
        this.basePricingStrategy = new BasePricingStrategy();
    }

    public DynamicPricingStrategy(PricingStrategy basePricingStrategy) {
        this.basePricingStrategy = basePricingStrategy;
    }

    @Override
    public BigDecimal calculateTotalPrice(Show show, List<ShowSeat> showSeats) {
        return showSeats.stream()
            .map(seat -> calculateSeatPrice(show, seat))
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public BigDecimal calculateSeatPrice(Show show, ShowSeat showSeat) {
        BigDecimal basePrice = basePricingStrategy.calculateSeatPrice(show, showSeat);
        BigDecimal multiplier = getDemandMultiplier(show);
        return basePrice.multiply(multiplier);
    }

    private BigDecimal getDemandMultiplier(Show show) {
        double occupancy = getOccupancyRate(show);
        
        if (occupancy >= VERY_HIGH_DEMAND_THRESHOLD) {
            return VERY_HIGH_DEMAND_MULTIPLIER;
        } else if (occupancy >= HIGH_DEMAND_THRESHOLD) {
            return HIGH_DEMAND_MULTIPLIER;
        }
        return BigDecimal.ONE;
    }

    private double getOccupancyRate(Show show) {
        int total = show.getTotalSeatCount();
        int booked = show.getBookedSeatCount();
        return total > 0 ? (double) booked / total : 0.0;
    }

    @Override
    public String getStrategyName() {
        return "Dynamic Pricing (demand-based)";
    }
}



