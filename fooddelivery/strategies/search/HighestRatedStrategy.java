package fooddelivery.strategies.search;

import fooddelivery.models.Location;
import fooddelivery.models.Restaurant;
import java.util.Comparator;
import java.util.List;

/**
 * Search strategy that sorts restaurants by rating (highest first).
 */
public class HighestRatedStrategy implements RestaurantSearchStrategy {
    
    private final double minRating;
    private final double maxDistanceKm;
    
    public HighestRatedStrategy() {
        this.minRating = 0.0;
        this.maxDistanceKm = 20.0;
    }
    
    public HighestRatedStrategy(double minRating, double maxDistanceKm) {
        this.minRating = minRating;
        this.maxDistanceKm = maxDistanceKm;
    }

    @Override
    public List<Restaurant> search(List<Restaurant> restaurants, Location customerLocation) {
        return restaurants.stream()
                .filter(Restaurant::isAcceptingOrders)
                .filter(r -> r.getRating() >= minRating)
                .filter(r -> r.getLocation().distanceTo(customerLocation) <= maxDistanceKm)
                .sorted(Comparator.comparingDouble(Restaurant::getRating).reversed())
                .toList();
    }

    @Override
    public String getStrategyName() {
        return "Top Rated";
    }
}



