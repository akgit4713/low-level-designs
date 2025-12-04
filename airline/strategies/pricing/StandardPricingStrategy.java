package airline.strategies.pricing;

import airline.enums.SeatClass;
import airline.models.Flight;

import java.math.BigDecimal;

/**
 * Standard pricing based on base price and seat class multiplier.
 */
public class StandardPricingStrategy implements PricingStrategy {

    @Override
    public BigDecimal calculatePrice(Flight flight, SeatClass seatClass) {
        BigDecimal basePrice = flight.getBasePrice(seatClass);
        if (basePrice == null || basePrice.compareTo(BigDecimal.ZERO) == 0) {
            // Fallback: use economy base price with multiplier
            basePrice = flight.getBasePrice(SeatClass.ECONOMY);
            if (basePrice == null) {
                basePrice = new BigDecimal("100.00"); // Default base
            }
            return basePrice.multiply(seatClass.getPriceMultiplier());
        }
        return basePrice;
    }

    @Override
    public String getDescription() {
        return "Standard pricing based on base fare";
    }
}



