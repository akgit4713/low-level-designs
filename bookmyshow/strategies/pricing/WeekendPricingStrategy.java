package bookmyshow.strategies.pricing;

import bookmyshow.models.Show;
import bookmyshow.models.ShowSeat;
import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.util.List;

/**
 * Weekend pricing strategy with additional surcharge on weekends.
 */
public class WeekendPricingStrategy implements PricingStrategy {
    
    private static final BigDecimal WEEKEND_MULTIPLIER = BigDecimal.valueOf(1.25);  // 25% extra on weekends
    private final PricingStrategy basePricingStrategy;

    public WeekendPricingStrategy() {
        this.basePricingStrategy = new BasePricingStrategy();
    }

    public WeekendPricingStrategy(PricingStrategy basePricingStrategy) {
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
        
        if (isWeekend(show)) {
            return basePrice.multiply(WEEKEND_MULTIPLIER);
        }
        return basePrice;
    }

    private boolean isWeekend(Show show) {
        DayOfWeek day = show.getStartTime().getDayOfWeek();
        return day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY;
    }

    @Override
    public String getStrategyName() {
        return "Weekend Pricing (25% surcharge on weekends)";
    }
}



