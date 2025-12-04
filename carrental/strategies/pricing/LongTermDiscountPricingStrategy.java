package carrental.strategies.pricing;

import carrental.models.Car;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * Long-term discount pricing strategy - applies discounts for longer rentals.
 * 
 * Discount tiers:
 * - 3-6 days: 5% discount
 * - 7-13 days: 10% discount
 * - 14-29 days: 15% discount
 * - 30+ days: 20% discount
 */
public class LongTermDiscountPricingStrategy implements PricingStrategy {

    @Override
    public BigDecimal calculatePrice(Car car, LocalDate startDate, LocalDate endDate) {
        long days = ChronoUnit.DAYS.between(startDate, endDate) + 1;
        BigDecimal basePrice = car.getEffectivePricePerDay().multiply(BigDecimal.valueOf(days));
        
        BigDecimal discountMultiplier = getDiscountMultiplier(days);
        return basePrice.multiply(discountMultiplier).setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal getDiscountMultiplier(long days) {
        if (days >= 30) {
            return BigDecimal.valueOf(0.80); // 20% discount
        } else if (days >= 14) {
            return BigDecimal.valueOf(0.85); // 15% discount
        } else if (days >= 7) {
            return BigDecimal.valueOf(0.90); // 10% discount
        } else if (days >= 3) {
            return BigDecimal.valueOf(0.95); // 5% discount
        }
        return BigDecimal.ONE; // No discount
    }

    @Override
    public String getStrategyName() {
        return "Long-Term Discount Pricing";
    }
}



