package onlineshopping.services;

import onlineshopping.models.Inventory;
import onlineshopping.observers.InventoryObserver;

import java.util.List;
import java.util.Optional;

/**
 * Service interface for inventory management
 */
public interface InventoryService {
    
    /**
     * Set inventory for a product
     */
    Inventory setInventory(String productId, int quantity);
    
    /**
     * Get inventory for a product
     */
    Optional<Inventory> getInventory(String productId);
    
    /**
     * Add stock
     */
    void addStock(String productId, int quantity);
    
    /**
     * Check stock availability
     */
    boolean checkAvailability(String productId, int quantity);
    
    /**
     * Reserve stock for order
     */
    boolean reserveStock(String productId, int quantity);
    
    /**
     * Release reserved stock
     */
    void releaseStock(String productId, int quantity);
    
    /**
     * Confirm stock deduction (after successful order)
     */
    void confirmDeduction(String productId, int quantity);
    
    /**
     * Get low stock items
     */
    List<Inventory> getLowStockItems();
    
    /**
     * Get out of stock items
     */
    List<Inventory> getOutOfStockItems();
    
    /**
     * Set low stock threshold
     */
    void setLowStockThreshold(String productId, int threshold);
    
    /**
     * Register inventory observer
     */
    void addObserver(InventoryObserver observer);
    
    /**
     * Remove inventory observer
     */
    void removeObserver(InventoryObserver observer);
}



