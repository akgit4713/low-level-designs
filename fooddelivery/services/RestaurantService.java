package fooddelivery.services;

import fooddelivery.enums.CuisineType;
import fooddelivery.enums.RestaurantStatus;
import fooddelivery.models.Location;
import fooddelivery.models.MenuItem;
import fooddelivery.models.Restaurant;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Service interface for restaurant operations.
 */
public interface RestaurantService {
    Restaurant registerRestaurant(String name, String ownerId, Location location);
    Optional<Restaurant> getRestaurantById(String restaurantId);
    List<Restaurant> getRestaurantsByOwner(String ownerId);
    List<Restaurant> searchRestaurants(Location customerLocation);
    List<Restaurant> searchRestaurantsByCuisine(CuisineType cuisineType, Location customerLocation);
    
    // Menu management
    MenuItem addMenuItem(String restaurantId, String name, String description, 
                         BigDecimal price, CuisineType cuisineType);
    void updateMenuItem(String restaurantId, String itemId, BigDecimal newPrice);
    void markItemOutOfStock(String restaurantId, String itemId);
    void markItemAvailable(String restaurantId, String itemId);
    void removeMenuItem(String restaurantId, String itemId);
    List<MenuItem> getMenu(String restaurantId);
    List<MenuItem> getAvailableMenu(String restaurantId);
    
    // Restaurant status
    void updateRestaurantStatus(String restaurantId, RestaurantStatus status);
    void setAcceptingOrders(String restaurantId, boolean accepting);
    void addRating(String restaurantId, double rating);
}



