package restaurant.strategies.tax;

import restaurant.models.Order;
import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Standard tax calculation (GST, VAT, Sales Tax, etc.)
 */
public class StandardTaxStrategy implements TaxStrategy {
    
    private final String name;
    private final BigDecimal rate;
    
    public StandardTaxStrategy(String name, BigDecimal rate) {
        this.name = name;
        this.rate = rate;
    }
    
    /**
     * Create GST tax (18%)
     */
    public static StandardTaxStrategy gst() {
        return new StandardTaxStrategy("GST", new BigDecimal("0.18"));
    }
    
    /**
     * Create VAT tax (configurable)
     */
    public static StandardTaxStrategy vat(BigDecimal rate) {
        return new StandardTaxStrategy("VAT", rate);
    }
    
    /**
     * Create Sales Tax (configurable)
     */
    public static StandardTaxStrategy salesTax(BigDecimal rate) {
        return new StandardTaxStrategy("Sales Tax", rate);
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
        return name;
    }
    
    @Override
    public boolean isApplicable(Order order) {
        // Standard tax applies to all orders
        return true;
    }
}

