package onlineshopping.repositories;

import java.util.List;
import java.util.Optional;

/**
 * Generic repository interface for data access
 * Follows Repository Pattern - abstracts data access from business logic
 */
public interface Repository<T, ID> {
    
    /**
     * Save an entity
     */
    T save(T entity);
    
    /**
     * Find entity by ID
     */
    Optional<T> findById(ID id);
    
    /**
     * Get all entities
     */
    List<T> findAll();
    
    /**
     * Delete entity by ID
     */
    boolean deleteById(ID id);
    
    /**
     * Check if entity exists
     */
    boolean existsById(ID id);
    
    /**
     * Count all entities
     */
    long count();
}



