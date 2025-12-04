package carrental.strategies.pricing;

import carrental.models.Car;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;

/**
 * Weekend pricing strategy - applies a surcharge for weekend days.
 */
public class WeekendPricingStrategy implements PricingStrategy {

    private static final BigDecimal WEEKEND_MULTIPLIER = BigDecimal.valueOf(1.25); // 25% surcharge

    @Override
    public BigDecimal calculatePrice(Car car, LocalDate startDate, LocalDate endDate) {
        BigDecimal totalPrice = BigDecimal.ZERO;
        BigDecimal dailyRate = car.getEffectivePricePerDay();
        
        LocalDate current = startDate;
        while (!current.isAfter(endDate)) {
            if (isWeekend(current)) {
                totalPrice = totalPrice.add(dailyRate.multiply(WEEKEND_MULTIPLIER));
            } else {
                totalPrice = totalPrice.add(dailyRate);
            }
            current = current.plusDays(1);
        }
        
        return totalPrice;
    }

    private boolean isWeekend(LocalDate date) {
        DayOfWeek day = date.getDayOfWeek();
        return day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY;
    }

    @Override
    public String getStrategyName() {
        return "Weekend Surge Pricing";
    }
}



