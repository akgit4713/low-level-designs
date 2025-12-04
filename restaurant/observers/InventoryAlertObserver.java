package restaurant.observers;

import restaurant.models.InventoryItem;

/**
 * Observer that alerts when inventory needs attention
 */
public class InventoryAlertObserver implements InventoryObserver {
    
    @Override
    public void onLowStock(InventoryItem item) {
        System.out.println("\n⚠️ [INVENTORY ALERT] Low stock warning!");
        System.out.println("   Ingredient: " + item.getIngredient().getName());
        System.out.println("   Current: " + item.getQuantity() + " " + item.getIngredient().getUnit());
        System.out.println("   Reorder Level: " + item.getReorderLevel() + " " + item.getIngredient().getUnit());
        System.out.println("   Suggested Reorder: " + item.getReorderQuantity() + " " + item.getIngredient().getUnit());
    }
    
    @Override
    public void onStockConsumed(InventoryItem item, double quantity) {
        // Silent tracking - can be enabled for debugging
    }
    
    @Override
    public void onStockRestocked(InventoryItem item, double quantity) {
        System.out.println("\n📦 [INVENTORY] Restocked: " + item.getIngredient().getName() + 
            " +" + quantity + " " + item.getIngredient().getUnit());
    }
    
    @Override
    public void onStockDepleted(InventoryItem item) {
        System.out.println("\n🚨 [INVENTORY ALERT] CRITICAL - Stock depleted!");
        System.out.println("   Ingredient: " + item.getIngredient().getName());
        System.out.println("   Action Required: Immediate reorder needed!");
    }
}

