package onlineshopping.services.impl;

import onlineshopping.exceptions.InventoryException;
import onlineshopping.models.Inventory;
import onlineshopping.observers.InventoryObserver;
import onlineshopping.repositories.impl.InMemoryInventoryRepository;
import onlineshopping.services.InventoryService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implementation of InventoryService
 */
public class InventoryServiceImpl implements InventoryService {
    
    private final InMemoryInventoryRepository inventoryRepository;
    private final List<InventoryObserver> observers = new ArrayList<>();

    public InventoryServiceImpl(InMemoryInventoryRepository inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }

    @Override
    public Inventory setInventory(String productId, int quantity) {
        Inventory inventory = inventoryRepository.getOrCreate(productId, quantity);
        
        int oldQuantity = inventory.getTotalQuantity();
        if (quantity != oldQuantity) {
            inventory.setTotalQuantity(quantity);
            notifyStockChanged(inventory, oldQuantity, quantity);
            
            if (oldQuantity == 0 && quantity > 0) {
                notifyRestocked(inventory);
            }
        }
        
        checkAndNotifyLowStock(inventory);
        return inventoryRepository.save(inventory);
    }

    @Override
    public Optional<Inventory> getInventory(String productId) {
        return inventoryRepository.findById(productId);
    }

    @Override
    public void addStock(String productId, int quantity) {
        Inventory inventory = inventoryRepository.findById(productId)
            .orElseThrow(() -> InventoryException.notFound(productId));
        
        boolean wasOutOfStock = inventory.isOutOfStock();
        int oldQuantity = inventory.getTotalQuantity();
        
        inventory.addStock(quantity);
        inventoryRepository.save(inventory);
        
        notifyStockChanged(inventory, oldQuantity, inventory.getTotalQuantity());
        
        if (wasOutOfStock && !inventory.isOutOfStock()) {
            notifyRestocked(inventory);
        }
    }

    @Override
    public boolean checkAvailability(String productId, int quantity) {
        return inventoryRepository.findById(productId)
            .map(inv -> inv.isAvailable(quantity))
            .orElse(false);
    }

    @Override
    public boolean reserveStock(String productId, int quantity) {
        Inventory inventory = inventoryRepository.findById(productId)
            .orElseThrow(() -> InventoryException.notFound(productId));
        
        boolean reserved = inventory.reserve(quantity);
        if (reserved) {
            inventoryRepository.save(inventory);
            checkAndNotifyLowStock(inventory);
        }
        
        return reserved;
    }

    @Override
    public void releaseStock(String productId, int quantity) {
        Inventory inventory = inventoryRepository.findById(productId)
            .orElseThrow(() -> InventoryException.notFound(productId));
        
        inventory.releaseReservation(quantity);
        inventoryRepository.save(inventory);
    }

    @Override
    public void confirmDeduction(String productId, int quantity) {
        Inventory inventory = inventoryRepository.findById(productId)
            .orElseThrow(() -> InventoryException.notFound(productId));
        
        int oldQuantity = inventory.getTotalQuantity();
        inventory.confirmDeduction(quantity);
        inventoryRepository.save(inventory);
        
        notifyStockChanged(inventory, oldQuantity, inventory.getTotalQuantity());
        checkAndNotifyLowStock(inventory);
        
        if (inventory.isOutOfStock()) {
            notifyOutOfStock(inventory);
        }
    }

    @Override
    public List<Inventory> getLowStockItems() {
        return inventoryRepository.findLowStock();
    }

    @Override
    public List<Inventory> getOutOfStockItems() {
        return inventoryRepository.findOutOfStock();
    }

    @Override
    public void setLowStockThreshold(String productId, int threshold) {
        Inventory inventory = inventoryRepository.findById(productId)
            .orElseThrow(() -> InventoryException.notFound(productId));
        
        inventory.setLowStockThreshold(threshold);
        inventoryRepository.save(inventory);
    }

    @Override
    public void addObserver(InventoryObserver observer) {
        observers.add(observer);
    }

    @Override
    public void removeObserver(InventoryObserver observer) {
        observers.remove(observer);
    }

    private void notifyStockChanged(Inventory inventory, int oldQuantity, int newQuantity) {
        for (InventoryObserver observer : observers) {
            observer.onStockChanged(inventory, oldQuantity, newQuantity);
        }
    }

    private void notifyLowStock(Inventory inventory) {
        for (InventoryObserver observer : observers) {
            observer.onLowStock(inventory);
        }
    }

    private void notifyOutOfStock(Inventory inventory) {
        for (InventoryObserver observer : observers) {
            observer.onOutOfStock(inventory);
        }
    }

    private void notifyRestocked(Inventory inventory) {
        for (InventoryObserver observer : observers) {
            observer.onRestocked(inventory);
        }
    }

    private void checkAndNotifyLowStock(Inventory inventory) {
        if (inventory.isLowStock() && !inventory.isOutOfStock()) {
            notifyLowStock(inventory);
        }
    }
}



