package bookmyshow.strategies.pricing;

import bookmyshow.models.Show;
import bookmyshow.models.ShowSeat;
import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.List;

/**
 * Peak hour pricing strategy with surcharge during prime time slots.
 */
public class PeakHourPricingStrategy implements PricingStrategy {
    
    private static final LocalTime EVENING_PEAK_START = LocalTime.of(18, 0);
    private static final LocalTime EVENING_PEAK_END = LocalTime.of(22, 0);
    private static final BigDecimal PEAK_MULTIPLIER = BigDecimal.valueOf(1.20);  // 20% extra during peak
    
    private final PricingStrategy basePricingStrategy;

    public PeakHourPricingStrategy() {
        this.basePricingStrategy = new BasePricingStrategy();
    }

    public PeakHourPricingStrategy(PricingStrategy basePricingStrategy) {
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
        
        if (isPeakHour(show)) {
            return basePrice.multiply(PEAK_MULTIPLIER);
        }
        return basePrice;
    }

    private boolean isPeakHour(Show show) {
        LocalTime showTime = show.getStartTime().toLocalTime();
        return !showTime.isBefore(EVENING_PEAK_START) && showTime.isBefore(EVENING_PEAK_END);
    }

    @Override
    public String getStrategyName() {
        return "Peak Hour Pricing (20% surcharge 6PM-10PM)";
    }
}



