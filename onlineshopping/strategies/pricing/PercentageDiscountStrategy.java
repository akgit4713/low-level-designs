package onlineshopping.strategies.pricing;

import onlineshopping.models.Cart;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Percentage-based discount strategy
 */
public class PercentageDiscountStrategy implements PricingStrategy {
    
    private final String name;
    private final int percentage;
    private final BigDecimal minimumAmount;
    private final BigDecimal maxDiscount;

    public PercentageDiscountStrategy(String name, int percentage) {
        this(name, percentage, BigDecimal.ZERO, null);
    }

    public PercentageDiscountStrategy(String name, int percentage, 
                                       BigDecimal minimumAmount, BigDecimal maxDiscount) {
        this.name = name;
        this.percentage = percentage;
        this.minimumAmount = minimumAmount;
        this.maxDiscount = maxDiscount;
    }

    @Override
    public BigDecimal calculateDiscount(Cart cart, BigDecimal subtotal) {
        if (!isApplicable(cart, subtotal)) {
            return BigDecimal.ZERO;
        }
        
        BigDecimal discount = subtotal
            .multiply(BigDecimal.valueOf(percentage))
            .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        
        if (maxDiscount != null && discount.compareTo(maxDiscount) > 0) {
            return maxDiscount;
        }
        
        return discount;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDescription() {
        String desc = percentage + "% off";
        if (minimumAmount.compareTo(BigDecimal.ZERO) > 0) {
            desc += " on orders above $" + minimumAmount;
        }
        if (maxDiscount != null) {
            desc += " (max $" + maxDiscount + ")";
        }
        return desc;
    }

    @Override
    public boolean isApplicable(Cart cart, BigDecimal subtotal) {
        return subtotal.compareTo(minimumAmount) >= 0;
    }
}



