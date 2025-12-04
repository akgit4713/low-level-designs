package fooddelivery.factories;

import fooddelivery.enums.CuisineType;
import fooddelivery.strategies.search.*;

/**
 * Factory for creating restaurant search strategy instances.
 */
public class SearchStrategyFactory {
    
    public enum SearchType {
        NEAREST,
        HIGHEST_RATED,
        FASTEST_DELIVERY,
        BY_CUISINE
    }
    
    public RestaurantSearchStrategy createStrategy(SearchType type) {
        return switch (type) {
            case NEAREST -> new NearestRestaurantStrategy();
            case HIGHEST_RATED -> new HighestRatedStrategy();
            case FASTEST_DELIVERY -> new FastestDeliveryStrategy();
            default -> new NearestRestaurantStrategy();
        };
    }
    
    public RestaurantSearchStrategy createCuisineStrategy(CuisineType cuisineType) {
        return new CuisineFilterStrategy(cuisineType);
    }
    
    public RestaurantSearchStrategy createCustomNearestStrategy(double maxDistanceKm) {
        return new NearestRestaurantStrategy(maxDistanceKm);
    }
    
    public RestaurantSearchStrategy createCustomRatedStrategy(double minRating, double maxDistanceKm) {
        return new HighestRatedStrategy(minRating, maxDistanceKm);
    }
}



