package onlineshopping.strategies.pricing;

import onlineshopping.models.Cart;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.NavigableMap;
import java.util.TreeMap;

/**
 * Tiered pricing strategy - higher discounts for larger orders
 */
public class TieredPricingStrategy implements PricingStrategy {
    
    private final String name;
    private final NavigableMap<BigDecimal, Integer> tiers; // amount threshold -> percentage

    public TieredPricingStrategy(String name) {
        this.name = name;
        this.tiers = new TreeMap<>();
    }

    /**
     * Add a tier: orders above threshold get the specified percentage off
     */
    public TieredPricingStrategy addTier(BigDecimal threshold, int percentage) {
        tiers.put(threshold, percentage);
        return this;
    }

    @Override
    public BigDecimal calculateDiscount(Cart cart, BigDecimal subtotal) {
        if (!isApplicable(cart, subtotal)) {
            return BigDecimal.ZERO;
        }
        
        // Find the highest applicable tier
        var entry = tiers.floorEntry(subtotal);
        if (entry == null) {
            return BigDecimal.ZERO;
        }
        
        int percentage = entry.getValue();
        return subtotal
            .multiply(BigDecimal.valueOf(percentage))
            .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDescription() {
        StringBuilder sb = new StringBuilder("Tiered discount: ");
        tiers.forEach((threshold, percentage) -> 
            sb.append(String.format("$%s+ → %d%% | ", threshold, percentage)));
        return sb.toString().replaceAll(" \\| $", "");
    }

    @Override
    public boolean isApplicable(Cart cart, BigDecimal subtotal) {
        return !tiers.isEmpty() && subtotal.compareTo(tiers.firstKey()) >= 0;
    }

    /**
     * Create a default tiered strategy
     */
    public static TieredPricingStrategy createDefault() {
        return new TieredPricingStrategy("Volume Discount")
            .addTier(new BigDecimal("100"), 5)
            .addTier(new BigDecimal("500"), 10)
            .addTier(new BigDecimal("1000"), 15)
            .addTier(new BigDecimal("5000"), 20);
    }
}



