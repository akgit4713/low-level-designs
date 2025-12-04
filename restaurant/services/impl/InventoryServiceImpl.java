package restaurant.services.impl;

import restaurant.exceptions.InventoryException;
import restaurant.models.*;
import restaurant.observers.InventoryObserver;
import restaurant.repositories.impl.InMemoryInventoryRepository;
import restaurant.services.InventoryService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implementation of InventoryService with Observer pattern support
 */
public class InventoryServiceImpl implements InventoryService {
    
    private final InMemoryInventoryRepository inventoryRepository;
    private final List<InventoryObserver> observers = new ArrayList<>();
    
    public InventoryServiceImpl(InMemoryInventoryRepository inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }
    
    @Override
    public InventoryItem addInventoryItem(Ingredient ingredient, double quantity,
                                           double reorderLevel, double reorderQuantity) {
        InventoryItem item = new InventoryItem(ingredient, quantity, reorderLevel, reorderQuantity);
        return inventoryRepository.save(item);
    }
    
    @Override
    public Optional<InventoryItem> getInventoryItem(String ingredientId) {
        return inventoryRepository.findById(ingredientId);
    }
    
    @Override
    public List<InventoryItem> getAllInventory() {
        return inventoryRepository.findAll();
    }
    
    @Override
    public boolean canFulfillOrder(Order order) {
        for (OrderItem orderItem : order.getItems()) {
            MenuItem menuItem = orderItem.getMenuItem();
            for (IngredientRequirement req : menuItem.getIngredients()) {
                double totalRequired = req.getQuantity() * orderItem.getQuantity();
                Optional<InventoryItem> inventoryItem = inventoryRepository.findById(
                    req.getIngredient().getId()
                );
                
                if (inventoryItem.isEmpty() || !inventoryItem.get().hasSufficientStock(totalRequired)) {
                    return false;
                }
            }
        }
        return true;
    }
    
    @Override
    public void consumeForOrder(Order order) {
        for (OrderItem orderItem : order.getItems()) {
            MenuItem menuItem = orderItem.getMenuItem();
            for (IngredientRequirement req : menuItem.getIngredients()) {
                double totalRequired = req.getQuantity() * orderItem.getQuantity();
                
                InventoryItem inventoryItem = inventoryRepository.findById(req.getIngredient().getId())
                    .orElseThrow(() -> InventoryException.ingredientNotFound(req.getIngredient().getId()));
                
                double previousQuantity = inventoryItem.getQuantity();
                if (!inventoryItem.consume(totalRequired)) {
                    throw InventoryException.insufficientStock(
                        req.getIngredient().getName(), totalRequired, inventoryItem.getQuantity()
                    );
                }
                
                notifyStockConsumed(inventoryItem, totalRequired);
                
                // Check for low stock after consumption
                if (inventoryItem.needsReorder() && previousQuantity > inventoryItem.getReorderLevel()) {
                    notifyLowStock(inventoryItem);
                }
                
                // Check if depleted
                if (inventoryItem.getQuantity() <= 0) {
                    notifyStockDepleted(inventoryItem);
                }
            }
        }
    }
    
    @Override
    public void restock(String ingredientId, double quantity) {
        InventoryItem item = inventoryRepository.findById(ingredientId)
            .orElseThrow(() -> InventoryException.ingredientNotFound(ingredientId));
        
        item.restock(quantity);
        notifyStockRestocked(item, quantity);
    }
    
    @Override
    public List<InventoryItem> getLowStockItems() {
        return inventoryRepository.findLowStock();
    }
    
    @Override
    public void addObserver(InventoryObserver observer) {
        observers.add(observer);
    }
    
    @Override
    public void removeObserver(InventoryObserver observer) {
        observers.remove(observer);
    }
    
    // Observer notifications
    
    private void notifyLowStock(InventoryItem item) {
        for (InventoryObserver observer : observers) {
            observer.onLowStock(item);
        }
    }
    
    private void notifyStockConsumed(InventoryItem item, double quantity) {
        for (InventoryObserver observer : observers) {
            observer.onStockConsumed(item, quantity);
        }
    }
    
    private void notifyStockRestocked(InventoryItem item, double quantity) {
        for (InventoryObserver observer : observers) {
            observer.onStockRestocked(item, quantity);
        }
    }
    
    private void notifyStockDepleted(InventoryItem item) {
        for (InventoryObserver observer : observers) {
            observer.onStockDepleted(item);
        }
    }
}

