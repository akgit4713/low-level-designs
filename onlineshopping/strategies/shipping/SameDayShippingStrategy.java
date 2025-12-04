package onlineshopping.strategies.shipping;

import onlineshopping.models.Address;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Set;

/**
 * Same-day shipping - available in select cities before cutoff time
 */
public class SameDayShippingStrategy implements ShippingStrategy {
    
    private static final BigDecimal BASE_RATE = new BigDecimal("24.99");
    private static final LocalTime CUTOFF_TIME = LocalTime.of(14, 0); // 2 PM
    private static final Set<String> AVAILABLE_CITIES = Set.of(
        "NEW YORK", "NYC", "LOS ANGELES", "LA", "CHICAGO", "SAN FRANCISCO", "SF",
        "SEATTLE", "BOSTON", "MIAMI", "AUSTIN", "DENVER"
    );

    @Override
    public BigDecimal calculateCost(Address destination, BigDecimal orderValue, double weight) {
        if (!isAvailable(destination)) {
            throw new IllegalArgumentException("Same-day shipping not available for this address");
        }
        return BASE_RATE;
    }

    @Override
    public LocalDateTime getEstimatedDelivery() {
        LocalDateTime now = LocalDateTime.now();
        if (now.toLocalTime().isBefore(CUTOFF_TIME)) {
            return now.toLocalDate().atTime(21, 0); // Today by 9 PM
        } else {
            return now.toLocalDate().plusDays(1).atTime(21, 0); // Tomorrow by 9 PM
        }
    }

    @Override
    public String getName() {
        return "Same-Day Delivery (order by 2 PM)";
    }

    @Override
    public boolean isAvailable(Address destination) {
        if (destination == null) {
            return false;
        }
        
        // Check if city is in the available list
        String city = destination.getCity().toUpperCase();
        boolean cityAvailable = AVAILABLE_CITIES.contains(city);
        
        // Check if order is placed before cutoff
        boolean beforeCutoff = LocalTime.now().isBefore(CUTOFF_TIME);
        
        return cityAvailable && beforeCutoff;
    }

    /**
     * Get cutoff time for same-day delivery
     */
    public static LocalTime getCutoffTime() {
        return CUTOFF_TIME;
    }

    /**
     * Get list of available cities
     */
    public static Set<String> getAvailableCities() {
        return AVAILABLE_CITIES;
    }
}



