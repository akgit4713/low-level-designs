package restaurant.strategies.tax;

import restaurant.enums.OrderType;
import restaurant.models.Order;
import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Service charge for dine-in orders
 */
public class ServiceChargeStrategy implements TaxStrategy {
    
    private final BigDecimal rate;
    
    public ServiceChargeStrategy(BigDecimal rate) {
        this.rate = rate;
    }
    
    /**
     * Default 10% service charge
     */
    public static ServiceChargeStrategy standard() {
        return new ServiceChargeStrategy(new BigDecimal("0.10"));
    }
    
    @Override
    public BigDecimal calculateTax(Order order, BigDecimal taxableAmount) {
        if (!isApplicable(order)) {
            return BigDecimal.ZERO;
        }
        return taxableAmount.multiply(rate).setScale(2, RoundingMode.HALF_UP);
    }
    
    @Override
    public BigDecimal getTaxRate() {
        return rate;
    }
    
    @Override
    public String getTaxName() {
        return "Service Charge";
    }
    
    @Override
    public boolean isApplicable(Order order) {
        // Service charge only for dine-in orders
        return order.getOrderType() == OrderType.DINE_IN;
    }
}

