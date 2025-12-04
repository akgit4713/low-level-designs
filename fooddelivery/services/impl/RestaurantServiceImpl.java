package fooddelivery.services.impl;

import fooddelivery.enums.CuisineType;
import fooddelivery.enums.MenuItemStatus;
import fooddelivery.enums.RestaurantStatus;
import fooddelivery.exceptions.MenuException;
import fooddelivery.exceptions.RestaurantException;
import fooddelivery.models.Location;
import fooddelivery.models.MenuItem;
import fooddelivery.models.Restaurant;
import fooddelivery.repositories.MenuItemRepository;
import fooddelivery.repositories.RestaurantRepository;
import fooddelivery.services.RestaurantService;
import fooddelivery.strategies.search.RestaurantSearchStrategy;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Implementation of RestaurantService.
 */
public class RestaurantServiceImpl implements RestaurantService {
    
    private final RestaurantRepository restaurantRepository;
    private final MenuItemRepository menuItemRepository;
    private final RestaurantSearchStrategy searchStrategy;
    
    public RestaurantServiceImpl(RestaurantRepository restaurantRepository,
                                  MenuItemRepository menuItemRepository,
                                  RestaurantSearchStrategy searchStrategy) {
        this.restaurantRepository = restaurantRepository;
        this.menuItemRepository = menuItemRepository;
        this.searchStrategy = searchStrategy;
    }

    @Override
    public Restaurant registerRestaurant(String name, String ownerId, Location location) {
        String id = "REST-" + UUID.randomUUID().toString().substring(0, 8);
        Restaurant restaurant = new Restaurant(id, name, ownerId, location);
        
        return restaurantRepository.save(restaurant);
    }

    @Override
    public Optional<Restaurant> getRestaurantById(String restaurantId) {
        return restaurantRepository.findById(restaurantId);
    }

    @Override
    public List<Restaurant> getRestaurantsByOwner(String ownerId) {
        return restaurantRepository.findByOwnerId(ownerId);
    }

    @Override
    public List<Restaurant> searchRestaurants(Location customerLocation) {
        List<Restaurant> allRestaurants = restaurantRepository.findAll();
        return searchStrategy.search(allRestaurants, customerLocation);
    }

    @Override
    public List<Restaurant> searchRestaurantsByCuisine(CuisineType cuisineType, Location customerLocation) {
        List<Restaurant> cuisineRestaurants = restaurantRepository.findByCuisineType(cuisineType);
        return searchStrategy.search(cuisineRestaurants, customerLocation);
    }

    @Override
    public MenuItem addMenuItem(String restaurantId, String name, String description, 
                                 BigDecimal price, CuisineType cuisineType) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new RestaurantException("Restaurant not found: " + restaurantId));
        
        String itemId = "ITEM-" + UUID.randomUUID().toString().substring(0, 8);
        MenuItem item = new MenuItem(itemId, restaurantId, name, description, price, cuisineType);
        
        restaurant.addMenuItem(item);
        restaurantRepository.save(restaurant);
        menuItemRepository.save(item);
        
        return item;
    }

    @Override
    public void updateMenuItem(String restaurantId, String itemId, BigDecimal newPrice) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new RestaurantException("Restaurant not found: " + restaurantId));
        
        MenuItem item = restaurant.getMenuItem(itemId);
        if (item == null) {
            throw new MenuException("Menu item not found: " + itemId);
        }
        
        item.setPrice(newPrice);
        menuItemRepository.save(item);
    }

    @Override
    public void markItemOutOfStock(String restaurantId, String itemId) {
        updateItemStatus(restaurantId, itemId, MenuItemStatus.OUT_OF_STOCK);
    }

    @Override
    public void markItemAvailable(String restaurantId, String itemId) {
        updateItemStatus(restaurantId, itemId, MenuItemStatus.AVAILABLE);
    }
    
    private void updateItemStatus(String restaurantId, String itemId, MenuItemStatus status) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new RestaurantException("Restaurant not found: " + restaurantId));
        
        MenuItem item = restaurant.getMenuItem(itemId);
        if (item == null) {
            throw new MenuException("Menu item not found: " + itemId);
        }
        
        item.setStatus(status);
        menuItemRepository.save(item);
    }

    @Override
    public void removeMenuItem(String restaurantId, String itemId) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new RestaurantException("Restaurant not found: " + restaurantId));
        
        restaurant.removeMenuItem(itemId);
        restaurantRepository.save(restaurant);
        menuItemRepository.delete(itemId);
    }

    @Override
    public List<MenuItem> getMenu(String restaurantId) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new RestaurantException("Restaurant not found: " + restaurantId));
        
        return restaurant.getMenu();
    }

    @Override
    public List<MenuItem> getAvailableMenu(String restaurantId) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new RestaurantException("Restaurant not found: " + restaurantId));
        
        return restaurant.getAvailableItems();
    }

    @Override
    public void updateRestaurantStatus(String restaurantId, RestaurantStatus status) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new RestaurantException("Restaurant not found: " + restaurantId));
        
        restaurant.setStatus(status);
        restaurantRepository.save(restaurant);
    }

    @Override
    public void setAcceptingOrders(String restaurantId, boolean accepting) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new RestaurantException("Restaurant not found: " + restaurantId));
        
        restaurant.setAcceptingOrders(accepting);
        restaurantRepository.save(restaurant);
    }

    @Override
    public void addRating(String restaurantId, double rating) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new RestaurantException("Restaurant not found: " + restaurantId));
        
        restaurant.addRating(rating);
        restaurantRepository.save(restaurant);
    }
}



