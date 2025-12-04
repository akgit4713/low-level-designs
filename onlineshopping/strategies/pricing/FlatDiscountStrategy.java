package onlineshopping.strategies.pricing;

import onlineshopping.models.Cart;

import java.math.BigDecimal;

/**
 * Flat amount discount strategy
 */
public class FlatDiscountStrategy implements PricingStrategy {
    
    private final String name;
    private final BigDecimal discountAmount;
    private final BigDecimal minimumAmount;

    public FlatDiscountStrategy(String name, BigDecimal discountAmount) {
        this(name, discountAmount, BigDecimal.ZERO);
    }

    public FlatDiscountStrategy(String name, BigDecimal discountAmount, BigDecimal minimumAmount) {
        this.name = name;
        this.discountAmount = discountAmount;
        this.minimumAmount = minimumAmount;
    }

    @Override
    public BigDecimal calculateDiscount(Cart cart, BigDecimal subtotal) {
        if (!isApplicable(cart, subtotal)) {
            return BigDecimal.ZERO;
        }
        
        // Don't let discount exceed subtotal
        if (discountAmount.compareTo(subtotal) > 0) {
            return subtotal;
        }
        
        return discountAmount;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDescription() {
        String desc = "$" + discountAmount + " off";
        if (minimumAmount.compareTo(BigDecimal.ZERO) > 0) {
            desc += " on orders above $" + minimumAmount;
        }
        return desc;
    }

    @Override
    public boolean isApplicable(Cart cart, BigDecimal subtotal) {
        return subtotal.compareTo(minimumAmount) >= 0;
    }
}



