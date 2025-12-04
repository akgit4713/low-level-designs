package restaurant.repositories.impl;

import restaurant.enums.MenuCategory;
import restaurant.models.MenuItem;
import restaurant.repositories.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Thread-safe in-memory repository for menu items
 */
public class InMemoryMenuRepository implements Repository<MenuItem, String> {
    
    private final Map<String, MenuItem> menuItems = new ConcurrentHashMap<>();
    
    @Override
    public MenuItem save(MenuItem entity) {
        menuItems.put(entity.getId(), entity);
        return entity;
    }
    
    @Override
    public Optional<MenuItem> findById(String id) {
        return Optional.ofNullable(menuItems.get(id));
    }
    
    @Override
    public List<MenuItem> findAll() {
        return new ArrayList<>(menuItems.values());
    }
    
    @Override
    public boolean deleteById(String id) {
        return menuItems.remove(id) != null;
    }
    
    @Override
    public boolean existsById(String id) {
        return menuItems.containsKey(id);
    }
    
    @Override
    public long count() {
        return menuItems.size();
    }
    
    /**
     * Find menu items by category
     */
    public List<MenuItem> findByCategory(MenuCategory category) {
        return menuItems.values().stream()
            .filter(item -> item.getCategory() == category)
            .collect(Collectors.toList());
    }
    
    /**
     * Find available menu items
     */
    public List<MenuItem> findAvailable() {
        return menuItems.values().stream()
            .filter(MenuItem::isAvailable)
            .collect(Collectors.toList());
    }
    
    /**
     * Find vegetarian items
     */
    public List<MenuItem> findVegetarian() {
        return menuItems.values().stream()
            .filter(MenuItem::isVegetarian)
            .collect(Collectors.toList());
    }
    
    /**
     * Search menu items by name
     */
    public List<MenuItem> searchByName(String query) {
        String lowerQuery = query.toLowerCase();
        return menuItems.values().stream()
            .filter(item -> item.getName().toLowerCase().contains(lowerQuery))
            .collect(Collectors.toList());
    }
}

