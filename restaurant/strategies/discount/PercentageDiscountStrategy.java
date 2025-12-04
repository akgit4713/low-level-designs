package restaurant.strategies.discount;

import restaurant.models.Order;
import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Percentage-based discount (e.g., 10% off)
 */
public class PercentageDiscountStrategy implements DiscountStrategy {
    
    private final String name;
    private final BigDecimal percentage;
    private final BigDecimal minimumOrderAmount;
    
    public PercentageDiscountStrategy(String name, BigDecimal percentage) {
        this(name, percentage, BigDecimal.ZERO);
    }
    
    public PercentageDiscountStrategy(String name, BigDecimal percentage, BigDecimal minimumOrderAmount) {
        this.name = name;
        this.percentage = percentage;
        this.minimumOrderAmount = minimumOrderAmount;
    }
    
    @Override
    public BigDecimal calculateDiscount(Order order, BigDecimal subtotal) {
        if (!isApplicable(order)) {
            return BigDecimal.ZERO;
        }
        return subtotal.multiply(percentage)
            .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
    }
    
    @Override
    public boolean isApplicable(Order order) {
        return order.calculateSubtotal().compareTo(minimumOrderAmount) >= 0;
    }
    
    @Override
    public String getDiscountName() {
        return name + " (" + percentage + "%)";
    }
    
    @Override
    public String getDescription() {
        String desc = percentage + "% discount";
        if (minimumOrderAmount.compareTo(BigDecimal.ZERO) > 0) {
            desc += " on orders above " + minimumOrderAmount;
        }
        return desc;
    }
}

