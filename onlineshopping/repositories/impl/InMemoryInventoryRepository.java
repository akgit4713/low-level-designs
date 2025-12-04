package onlineshopping.repositories.impl;

import onlineshopping.models.Inventory;
import onlineshopping.repositories.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * In-memory implementation of inventory repository
 */
public class InMemoryInventoryRepository implements Repository<Inventory, String> {
    
    private final Map<String, Inventory> inventory = new ConcurrentHashMap<>();

    @Override
    public Inventory save(Inventory item) {
        inventory.put(item.getProductId(), item);
        return item;
    }

    @Override
    public Optional<Inventory> findById(String productId) {
        return Optional.ofNullable(inventory.get(productId));
    }

    @Override
    public List<Inventory> findAll() {
        return new ArrayList<>(inventory.values());
    }

    @Override
    public boolean deleteById(String productId) {
        return inventory.remove(productId) != null;
    }

    @Override
    public boolean existsById(String productId) {
        return inventory.containsKey(productId);
    }

    @Override
    public long count() {
        return inventory.size();
    }

    /**
     * Find low stock items
     */
    public List<Inventory> findLowStock() {
        return inventory.values().stream()
            .filter(Inventory::isLowStock)
            .collect(Collectors.toList());
    }

    /**
     * Find out of stock items
     */
    public List<Inventory> findOutOfStock() {
        return inventory.values().stream()
            .filter(Inventory::isOutOfStock)
            .collect(Collectors.toList());
    }

    /**
     * Get or create inventory for a product
     */
    public Inventory getOrCreate(String productId, int initialQuantity) {
        return inventory.computeIfAbsent(productId, id -> new Inventory(id, initialQuantity));
    }
}



