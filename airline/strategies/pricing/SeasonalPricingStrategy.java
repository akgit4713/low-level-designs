package airline.strategies.pricing;

import airline.enums.SeatClass;
import airline.models.Flight;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Month;

/**
 * Seasonal pricing that adjusts based on travel season.
 */
public class SeasonalPricingStrategy implements PricingStrategy {
    
    private final double peakSeasonMultiplier;
    private final double offSeasonMultiplier;

    public SeasonalPricingStrategy() {
        this(1.30, 0.85);
    }

    public SeasonalPricingStrategy(double peakSeasonMultiplier, double offSeasonMultiplier) {
        this.peakSeasonMultiplier = peakSeasonMultiplier;
        this.offSeasonMultiplier = offSeasonMultiplier;
    }

    @Override
    public BigDecimal calculatePrice(Flight flight, SeatClass seatClass) {
        BigDecimal basePrice = getBasePrice(flight, seatClass);
        
        Month month = flight.getDepartureTime().getMonth();
        double multiplier = getSeasonMultiplier(month);
        
        return basePrice.multiply(BigDecimal.valueOf(multiplier))
                .setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal getBasePrice(Flight flight, SeatClass seatClass) {
        BigDecimal basePrice = flight.getBasePrice(seatClass);
        if (basePrice == null || basePrice.compareTo(BigDecimal.ZERO) == 0) {
            basePrice = flight.getBasePrice(SeatClass.ECONOMY);
            if (basePrice == null) {
                basePrice = new BigDecimal("100.00");
            }
            return basePrice.multiply(seatClass.getPriceMultiplier());
        }
        return basePrice;
    }

    private double getSeasonMultiplier(Month month) {
        return switch (month) {
            // Peak seasons: Summer, Winter holidays
            case JUNE, JULY, AUGUST, DECEMBER -> peakSeasonMultiplier;
            // Off-peak seasons
            case JANUARY, FEBRUARY, SEPTEMBER, NOVEMBER -> offSeasonMultiplier;
            // Regular seasons
            default -> 1.0;
        };
    }

    @Override
    public String getDescription() {
        return String.format("Seasonal pricing (peak: +%.0f%%, off-peak: -%.0f%%)",
                (peakSeasonMultiplier - 1) * 100, (1 - offSeasonMultiplier) * 100);
    }
}



