package restaurant.observers;

import restaurant.enums.OrderStatus;
import restaurant.models.Order;

/**
 * Observer that displays orders on kitchen display system
 */
public class KitchenDisplayObserver implements OrderObserver {
    
    @Override
    public void onOrderCreated(Order order) {
        System.out.println("\n🍳 [KITCHEN DISPLAY] New Order Received!");
        System.out.println("   Order ID: " + order.getId());
        System.out.println("   Items:");
        order.getItems().forEach(item -> 
            System.out.println("   - " + item.getQuantity() + "x " + item.getMenuItem().getName() +
                (item.getSpecialInstructions().isEmpty() ? "" : " [" + item.getSpecialInstructions() + "]"))
        );
        System.out.println("   Est. Prep Time: " + order.getEstimatedPrepTime() + " mins");
    }
    
    @Override
    public void onOrderStatusChanged(Order order, OrderStatus oldStatus, OrderStatus newStatus) {
        System.out.println("\n📋 [KITCHEN DISPLAY] Order " + order.getId() + ": " + 
            oldStatus + " → " + newStatus);
    }
    
    @Override
    public void onOrderCompleted(Order order) {
        System.out.println("\n✅ [KITCHEN DISPLAY] Order " + order.getId() + " COMPLETED");
    }
    
    @Override
    public void onOrderCancelled(Order order) {
        System.out.println("\n❌ [KITCHEN DISPLAY] Order " + order.getId() + " CANCELLED - Stop preparation!");
    }
}

