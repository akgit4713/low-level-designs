package concertbooking.strategies.pricing;

import concertbooking.models.Concert;
import concertbooking.models.Seat;

import java.math.BigDecimal;
import java.util.List;

/**
 * Standard pricing strategy - uses section-based pricing
 */
public class StandardPricingStrategy implements PricingStrategy {
    
    @Override
    public BigDecimal calculatePrice(Concert concert, List<Seat> seats) {
        return seats.stream()
            .map(seat -> concert.getSectionPrice(seat.getSectionType()))
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    @Override
    public String getStrategyName() {
        return "Standard Pricing";
    }
}



