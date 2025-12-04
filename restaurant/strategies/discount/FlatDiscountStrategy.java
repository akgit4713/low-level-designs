package restaurant.strategies.discount;

import restaurant.models.Order;
import java.math.BigDecimal;

/**
 * Flat amount discount (e.g., $20 off)
 */
public class FlatDiscountStrategy implements DiscountStrategy {
    
    private final String name;
    private final BigDecimal discountAmount;
    private final BigDecimal minimumOrderAmount;
    
    public FlatDiscountStrategy(String name, BigDecimal discountAmount) {
        this(name, discountAmount, BigDecimal.ZERO);
    }
    
    public FlatDiscountStrategy(String name, BigDecimal discountAmount, BigDecimal minimumOrderAmount) {
        this.name = name;
        this.discountAmount = discountAmount;
        this.minimumOrderAmount = minimumOrderAmount;
    }
    
    @Override
    public BigDecimal calculateDiscount(Order order, BigDecimal subtotal) {
        if (!isApplicable(order)) {
            return BigDecimal.ZERO;
        }
        // Don't let discount exceed subtotal
        return discountAmount.min(subtotal);
    }
    
    @Override
    public boolean isApplicable(Order order) {
        return order.calculateSubtotal().compareTo(minimumOrderAmount) >= 0;
    }
    
    @Override
    public String getDiscountName() {
        return name;
    }
    
    @Override
    public String getDescription() {
        String desc = discountAmount + " off";
        if (minimumOrderAmount.compareTo(BigDecimal.ZERO) > 0) {
            desc += " on orders above " + minimumOrderAmount;
        }
        return desc;
    }
}

