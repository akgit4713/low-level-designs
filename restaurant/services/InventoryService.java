package restaurant.services;

import restaurant.models.Ingredient;
import restaurant.models.InventoryItem;
import restaurant.models.Order;
import restaurant.observers.InventoryObserver;

import java.util.List;
import java.util.Optional;

/**
 * Service interface for inventory management
 */
public interface InventoryService {
    
    /**
     * Add ingredient to inventory
     */
    InventoryItem addInventoryItem(Ingredient ingredient, double quantity, 
                                    double reorderLevel, double reorderQuantity);
    
    /**
     * Get inventory item by ingredient ID
     */
    Optional<InventoryItem> getInventoryItem(String ingredientId);
    
    /**
     * Get all inventory items
     */
    List<InventoryItem> getAllInventory();
    
    /**
     * Check if order can be fulfilled with current inventory
     */
    boolean canFulfillOrder(Order order);
    
    /**
     * Consume ingredients for an order
     */
    void consumeForOrder(Order order);
    
    /**
     * Restock an ingredient
     */
    void restock(String ingredientId, double quantity);
    
    /**
     * Get items that need reordering
     */
    List<InventoryItem> getLowStockItems();
    
    /**
     * Add inventory observer
     */
    void addObserver(InventoryObserver observer);
    
    /**
     * Remove inventory observer
     */
    void removeObserver(InventoryObserver observer);
}

