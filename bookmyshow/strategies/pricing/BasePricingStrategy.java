package bookmyshow.strategies.pricing;

import bookmyshow.models.Show;
import bookmyshow.models.ShowSeat;
import java.math.BigDecimal;
import java.util.List;

/**
 * Base pricing strategy using show base price and seat type multipliers.
 */
public class BasePricingStrategy implements PricingStrategy {

    @Override
    public BigDecimal calculateTotalPrice(Show show, List<ShowSeat> showSeats) {
        return showSeats.stream()
            .map(seat -> calculateSeatPrice(show, seat))
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public BigDecimal calculateSeatPrice(Show show, ShowSeat showSeat) {
        BigDecimal basePrice = show.getBasePrice();
        double multiplier = showSeat.getSeat().getSeatType().getPriceMultiplier();
        return basePrice.multiply(BigDecimal.valueOf(multiplier));
    }

    @Override
    public String getStrategyName() {
        return "Base Pricing";
    }
}



