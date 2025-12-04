package hotelmanagement.strategies.pricing;

import hotelmanagement.models.Room;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.Month;
import java.util.EnumMap;
import java.util.Map;

/**
 * Seasonal pricing strategy - applies different rates based on time of year
 */
public class SeasonalPricingStrategy implements PricingStrategy {
    
    private final Map<Season, BigDecimal> seasonMultipliers;
    
    public enum Season {
        PEAK,      // High demand season
        HIGH,      // Above average demand
        REGULAR,   // Normal demand
        LOW        // Low demand season
    }
    
    public SeasonalPricingStrategy() {
        this.seasonMultipliers = new EnumMap<>(Season.class);
        // Default multipliers
        seasonMultipliers.put(Season.PEAK, new BigDecimal("1.50"));    // +50%
        seasonMultipliers.put(Season.HIGH, new BigDecimal("1.25"));    // +25%
        seasonMultipliers.put(Season.REGULAR, BigDecimal.ONE);         // Standard
        seasonMultipliers.put(Season.LOW, new BigDecimal("0.80"));     // -20%
    }
    
    public SeasonalPricingStrategy(Map<Season, BigDecimal> multipliers) {
        this.seasonMultipliers = new EnumMap<>(multipliers);
    }
    
    @Override
    public BigDecimal calculateRate(Room room, LocalDate date) {
        Season season = getSeason(date);
        BigDecimal multiplier = seasonMultipliers.getOrDefault(season, BigDecimal.ONE);
        
        return room.getBaseRate()
            .multiply(multiplier)
            .setScale(2, RoundingMode.HALF_UP);
    }
    
    /**
     * Determine the season for a given date
     * This is a simplified version - in production, this would be configurable
     */
    protected Season getSeason(LocalDate date) {
        Month month = date.getMonth();
        
        return switch (month) {
            case JUNE, JULY, AUGUST -> Season.PEAK;              // Summer peak
            case DECEMBER -> Season.PEAK;                        // Holiday peak
            case MARCH, APRIL, MAY -> Season.HIGH;               // Spring
            case SEPTEMBER, OCTOBER, NOVEMBER -> Season.REGULAR; // Fall
            case JANUARY, FEBRUARY -> Season.LOW;                // Winter slow season
        };
    }
    
    public void setMultiplier(Season season, BigDecimal multiplier) {
        seasonMultipliers.put(season, multiplier);
    }
    
    @Override
    public String getStrategyName() {
        return "Seasonal Pricing";
    }
}



