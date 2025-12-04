package carrental.strategies.pricing;

import carrental.models.Car;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * Standard pricing strategy - calculates price based on
 * car's effective daily rate multiplied by number of days.
 */
public class StandardPricingStrategy implements PricingStrategy {

    @Override
    public BigDecimal calculatePrice(Car car, LocalDate startDate, LocalDate endDate) {
        long days = ChronoUnit.DAYS.between(startDate, endDate) + 1;
        return car.getEffectivePricePerDay().multiply(BigDecimal.valueOf(days));
    }

    @Override
    public String getStrategyName() {
        return "Standard Pricing";
    }
}



