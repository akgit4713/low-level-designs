package fooddelivery.strategies.search;

import fooddelivery.enums.CuisineType;
import fooddelivery.models.Location;
import fooddelivery.models.Restaurant;
import java.util.Comparator;
import java.util.List;

/**
 * Search strategy that filters restaurants by cuisine type.
 */
public class CuisineFilterStrategy implements RestaurantSearchStrategy {
    
    private final CuisineType cuisineType;
    private final double maxDistanceKm;
    
    public CuisineFilterStrategy(CuisineType cuisineType) {
        this.cuisineType = cuisineType;
        this.maxDistanceKm = 20.0;
    }
    
    public CuisineFilterStrategy(CuisineType cuisineType, double maxDistanceKm) {
        this.cuisineType = cuisineType;
        this.maxDistanceKm = maxDistanceKm;
    }

    @Override
    public List<Restaurant> search(List<Restaurant> restaurants, Location customerLocation) {
        return restaurants.stream()
                .filter(Restaurant::isAcceptingOrders)
                .filter(r -> r.getCuisineTypes().contains(cuisineType))
                .filter(r -> r.getLocation().distanceTo(customerLocation) <= maxDistanceKm)
                .sorted(Comparator.comparingDouble(Restaurant::getRating).reversed())
                .toList();
    }

    @Override
    public String getStrategyName() {
        return cuisineType.name() + " Restaurants";
    }
}



