package fooddelivery.strategies.search;

import fooddelivery.models.Location;
import fooddelivery.models.Restaurant;
import java.util.Comparator;
import java.util.List;

/**
 * Search strategy that sorts restaurants by distance from customer.
 */
public class NearestRestaurantStrategy implements RestaurantSearchStrategy {
    
    private final double maxDistanceKm;
    
    public NearestRestaurantStrategy() {
        this.maxDistanceKm = 15.0; // Default 15km radius
    }
    
    public NearestRestaurantStrategy(double maxDistanceKm) {
        this.maxDistanceKm = maxDistanceKm;
    }

    @Override
    public List<Restaurant> search(List<Restaurant> restaurants, Location customerLocation) {
        return restaurants.stream()
                .filter(Restaurant::isAcceptingOrders)
                .filter(r -> r.getLocation().distanceTo(customerLocation) <= maxDistanceKm)
                .sorted(Comparator.comparingDouble(
                    r -> r.getLocation().distanceTo(customerLocation)))
                .toList();
    }

    @Override
    public String getStrategyName() {
        return "Nearest First";
    }
}



