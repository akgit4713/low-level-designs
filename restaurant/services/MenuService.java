package restaurant.services;

import restaurant.enums.MenuCategory;
import restaurant.models.MenuItem;
import java.util.List;
import java.util.Optional;

/**
 * Service interface for menu management
 */
public interface MenuService {
    
    /**
     * Add a new menu item
     */
    MenuItem addMenuItem(MenuItem item);
    
    /**
     * Get menu item by ID
     */
    Optional<MenuItem> getMenuItem(String itemId);
    
    /**
     * Get all menu items
     */
    List<MenuItem> getAllMenuItems();
    
    /**
     * Get menu items by category
     */
    List<MenuItem> getMenuByCategory(MenuCategory category);
    
    /**
     * Get available menu items
     */
    List<MenuItem> getAvailableMenu();
    
    /**
     * Search menu items by name
     */
    List<MenuItem> searchMenu(String query);
    
    /**
     * Update menu item availability
     */
    void updateAvailability(String itemId, boolean available);
    
    /**
     * Remove menu item
     */
    boolean removeMenuItem(String itemId);
    
    /**
     * Get vegetarian menu items
     */
    List<MenuItem> getVegetarianMenu();
}

