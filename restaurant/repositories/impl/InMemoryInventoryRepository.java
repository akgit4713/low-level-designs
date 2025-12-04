package restaurant.repositories.impl;

import restaurant.models.InventoryItem;
import restaurant.repositories.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Thread-safe in-memory repository for inventory
 */
public class InMemoryInventoryRepository implements Repository<InventoryItem, String> {
    
    // Key is ingredient ID
    private final Map<String, InventoryItem> inventory = new ConcurrentHashMap<>();
    
    @Override
    public InventoryItem save(InventoryItem entity) {
        inventory.put(entity.getIngredient().getId(), entity);
        return entity;
    }
    
    @Override
    public Optional<InventoryItem> findById(String ingredientId) {
        return Optional.ofNullable(inventory.get(ingredientId));
    }
    
    @Override
    public List<InventoryItem> findAll() {
        return new ArrayList<>(inventory.values());
    }
    
    @Override
    public boolean deleteById(String ingredientId) {
        return inventory.remove(ingredientId) != null;
    }
    
    @Override
    public boolean existsById(String ingredientId) {
        return inventory.containsKey(ingredientId);
    }
    
    @Override
    public long count() {
        return inventory.size();
    }
    
    /**
     * Find items that need reordering
     */
    public List<InventoryItem> findLowStock() {
        return inventory.values().stream()
            .filter(InventoryItem::needsReorder)
            .collect(Collectors.toList());
    }
    
    /**
     * Find items by ingredient name (partial match)
     */
    public List<InventoryItem> searchByIngredientName(String query) {
        String lowerQuery = query.toLowerCase();
        return inventory.values().stream()
            .filter(item -> item.getIngredient().getName().toLowerCase().contains(lowerQuery))
            .collect(Collectors.toList());
    }
    
    /**
     * Get items sorted by stock percentage (lowest first)
     */
    public List<InventoryItem> findAllSortedByStockLevel() {
        return inventory.values().stream()
            .sorted(Comparator.comparingDouble(InventoryItem::getStockPercentage))
            .collect(Collectors.toList());
    }
}

