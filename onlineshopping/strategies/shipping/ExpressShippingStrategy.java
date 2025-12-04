package onlineshopping.strategies.shipping;

import onlineshopping.models.Address;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Express shipping - 2-3 business days
 */
public class ExpressShippingStrategy implements ShippingStrategy {
    
    private static final BigDecimal BASE_RATE = new BigDecimal("12.99");
    private static final BigDecimal PER_KG_RATE = new BigDecimal("2.00");
    private static final int DELIVERY_DAYS = 2;

    @Override
    public BigDecimal calculateCost(Address destination, BigDecimal orderValue, double weight) {
        // Base rate + weight-based charge
        BigDecimal weightCharge = PER_KG_RATE.multiply(BigDecimal.valueOf(Math.max(1, weight)));
        return BASE_RATE.add(weightCharge);
    }

    @Override
    public LocalDateTime getEstimatedDelivery() {
        return LocalDateTime.now().plusDays(DELIVERY_DAYS);
    }

    @Override
    public String getName() {
        return "Express Shipping (2-3 business days)";
    }

    @Override
    public boolean isAvailable(Address destination) {
        // Express shipping available in most areas
        return destination != null;
    }
}



