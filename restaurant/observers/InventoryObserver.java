package restaurant.observers;

import restaurant.models.InventoryItem;

/**
 * Observer interface for inventory changes
 */
public interface InventoryObserver {
    
    /**
     * Called when stock level falls below reorder level
     */
    void onLowStock(InventoryItem item);
    
    /**
     * Called when stock is consumed
     */
    void onStockConsumed(InventoryItem item, double quantity);
    
    /**
     * Called when stock is restocked
     */
    void onStockRestocked(InventoryItem item, double quantity);
    
    /**
     * Called when stock is depleted
     */
    void onStockDepleted(InventoryItem item);
}

