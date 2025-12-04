package fooddelivery.repositories.impl;

import fooddelivery.enums.CuisineType;
import fooddelivery.models.MenuItem;
import fooddelivery.repositories.MenuItemRepository;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Thread-safe in-memory implementation of MenuItemRepository.
 */
public class InMemoryMenuItemRepository implements MenuItemRepository {
    private final Map<String, MenuItem> menuItems = new ConcurrentHashMap<>();

    @Override
    public MenuItem save(MenuItem menuItem) {
        menuItems.put(menuItem.getId(), menuItem);
        return menuItem;
    }

    @Override
    public Optional<MenuItem> findById(String id) {
        return Optional.ofNullable(menuItems.get(id));
    }

    @Override
    public List<MenuItem> findByRestaurantId(String restaurantId) {
        return menuItems.values().stream()
                .filter(item -> item.getRestaurantId().equals(restaurantId))
                .toList();
    }

    @Override
    public List<MenuItem> findByCuisineType(CuisineType cuisineType) {
        return menuItems.values().stream()
                .filter(item -> item.getCuisineType() == cuisineType)
                .toList();
    }

    @Override
    public List<MenuItem> findAvailableByRestaurantId(String restaurantId) {
        return menuItems.values().stream()
                .filter(item -> item.getRestaurantId().equals(restaurantId))
                .filter(MenuItem::isAvailable)
                .toList();
    }

    @Override
    public void delete(String id) {
        menuItems.remove(id);
    }

    @Override
    public boolean existsById(String id) {
        return menuItems.containsKey(id);
    }
}



