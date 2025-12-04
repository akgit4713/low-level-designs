package onlineshopping.observers;

import onlineshopping.models.Inventory;

/**
 * Observer interface for inventory events
 */
public interface InventoryObserver {
    
    /**
     * Called when stock level changes
     */
    void onStockChanged(Inventory inventory, int oldQuantity, int newQuantity);
    
    /**
     * Called when stock reaches low threshold
     */
    void onLowStock(Inventory inventory);
    
    /**
     * Called when product goes out of stock
     */
    void onOutOfStock(Inventory inventory);
    
    /**
     * Called when out-of-stock product is restocked
     */
    void onRestocked(Inventory inventory);
}



