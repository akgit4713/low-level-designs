package hotelmanagement.strategies.pricing;

import hotelmanagement.models.Room;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.LocalDate;

/**
 * Weekend pricing strategy - applies higher rates on weekends (Friday-Sunday)
 */
public class WeekendPricingStrategy implements PricingStrategy {
    
    private final BigDecimal weekendMultiplier;
    
    public WeekendPricingStrategy() {
        this(new BigDecimal("1.25")); // 25% premium on weekends by default
    }
    
    public WeekendPricingStrategy(BigDecimal weekendMultiplier) {
        this.weekendMultiplier = weekendMultiplier;
    }
    
    @Override
    public BigDecimal calculateRate(Room room, LocalDate date) {
        BigDecimal baseRate = room.getBaseRate();
        
        if (isWeekend(date)) {
            return baseRate.multiply(weekendMultiplier).setScale(2, RoundingMode.HALF_UP);
        }
        
        return baseRate;
    }
    
    private boolean isWeekend(LocalDate date) {
        DayOfWeek day = date.getDayOfWeek();
        return day == DayOfWeek.FRIDAY || day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY;
    }
    
    @Override
    public String getStrategyName() {
        return "Weekend Pricing (+" + weekendMultiplier.subtract(BigDecimal.ONE)
            .multiply(new BigDecimal("100")).intValue() + "% on weekends)";
    }
}



