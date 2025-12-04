package hotelmanagement.strategies.pricing;

import hotelmanagement.models.Room;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Standard pricing strategy - uses room's base rate
 */
public class StandardPricingStrategy implements PricingStrategy {
    
    @Override
    public BigDecimal calculateRate(Room room, LocalDate date) {
        return room.getBaseRate();
    }
    
    @Override
    public String getStrategyName() {
        return "Standard Pricing";
    }
}



