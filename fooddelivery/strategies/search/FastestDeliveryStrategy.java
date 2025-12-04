package fooddelivery.strategies.search;

import fooddelivery.models.Location;
import fooddelivery.models.Restaurant;
import java.util.Comparator;
import java.util.List;

/**
 * Search strategy that sorts restaurants by estimated delivery time.
 */
public class FastestDeliveryStrategy implements RestaurantSearchStrategy {
    
    private static final double AVG_DELIVERY_SPEED_KM_PER_MIN = 0.5; // ~30 km/h
    private final double maxDistanceKm;
    
    public FastestDeliveryStrategy() {
        this.maxDistanceKm = 15.0;
    }
    
    public FastestDeliveryStrategy(double maxDistanceKm) {
        this.maxDistanceKm = maxDistanceKm;
    }

    @Override
    public List<Restaurant> search(List<Restaurant> restaurants, Location customerLocation) {
        return restaurants.stream()
                .filter(Restaurant::isAcceptingOrders)
                .filter(r -> r.getLocation().distanceTo(customerLocation) <= maxDistanceKm)
                .sorted(Comparator.comparingInt(r -> estimateDeliveryTime(r, customerLocation)))
                .toList();
    }
    
    private int estimateDeliveryTime(Restaurant restaurant, Location customerLocation) {
        double distance = restaurant.getLocation().distanceTo(customerLocation);
        int travelTime = (int) Math.ceil(distance / AVG_DELIVERY_SPEED_KM_PER_MIN);
        return restaurant.getAvgDeliveryTimeMinutes() + travelTime;
    }

    @Override
    public String getStrategyName() {
        return "Fastest Delivery";
    }
}



