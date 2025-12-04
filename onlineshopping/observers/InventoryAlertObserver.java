package onlineshopping.observers;

import onlineshopping.models.Inventory;

/**
 * Observer that sends alerts for inventory events
 */
public class InventoryAlertObserver implements InventoryObserver {

    @Override
    public void onStockChanged(Inventory inventory, int oldQuantity, int newQuantity) {
        System.out.printf("[INVENTORY] Product %s: Stock changed from %d to %d%n",
            inventory.getProductId(), oldQuantity, newQuantity);
    }

    @Override
    public void onLowStock(Inventory inventory) {
        sendAlert(String.format(
            "LOW STOCK ALERT: Product %s has only %d units remaining (threshold: %d)",
            inventory.getProductId(),
            inventory.getAvailableQuantity(),
            inventory.getLowStockThreshold()));
    }

    @Override
    public void onOutOfStock(Inventory inventory) {
        sendAlert(String.format(
            "OUT OF STOCK: Product %s is now out of stock!",
            inventory.getProductId()));
    }

    @Override
    public void onRestocked(Inventory inventory) {
        sendNotification(String.format(
            "RESTOCKED: Product %s has been restocked with %d units",
            inventory.getProductId(),
            inventory.getAvailableQuantity()));
    }

    private void sendAlert(String message) {
        // In a real implementation, this would send an alert to operations team
        System.out.println("[ALERT] " + message);
    }

    private void sendNotification(String message) {
        // In a real implementation, this would notify interested parties
        System.out.println("[NOTIFICATION] " + message);
    }
}



