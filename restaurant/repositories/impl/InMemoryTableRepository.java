package restaurant.repositories.impl;

import restaurant.enums.TableStatus;
import restaurant.models.Table;
import restaurant.repositories.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Thread-safe in-memory repository for tables
 */
public class InMemoryTableRepository implements Repository<Table, String> {
    
    private final Map<String, Table> tables = new ConcurrentHashMap<>();
    
    @Override
    public Table save(Table entity) {
        tables.put(entity.getId(), entity);
        return entity;
    }
    
    @Override
    public Optional<Table> findById(String id) {
        return Optional.ofNullable(tables.get(id));
    }
    
    @Override
    public List<Table> findAll() {
        return new ArrayList<>(tables.values());
    }
    
    @Override
    public boolean deleteById(String id) {
        return tables.remove(id) != null;
    }
    
    @Override
    public boolean existsById(String id) {
        return tables.containsKey(id);
    }
    
    @Override
    public long count() {
        return tables.size();
    }
    
    /**
     * Find tables by status
     */
    public List<Table> findByStatus(TableStatus status) {
        return tables.values().stream()
            .filter(table -> table.getStatus() == status)
            .collect(Collectors.toList());
    }
    
    /**
     * Find available tables
     */
    public List<Table> findAvailable() {
        return findByStatus(TableStatus.AVAILABLE);
    }
    
    /**
     * Find available tables that can accommodate party size
     */
    public List<Table> findAvailableForPartySize(int partySize) {
        return tables.values().stream()
            .filter(table -> table.getStatus() == TableStatus.AVAILABLE)
            .filter(table -> table.canAccommodate(partySize))
            .sorted(Comparator.comparingInt(Table::getCapacity))
            .collect(Collectors.toList());
    }
    
    /**
     * Find table by number
     */
    public Optional<Table> findByTableNumber(int tableNumber) {
        return tables.values().stream()
            .filter(table -> table.getTableNumber() == tableNumber)
            .findFirst();
    }
}

