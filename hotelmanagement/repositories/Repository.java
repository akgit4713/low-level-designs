package hotelmanagement.repositories;

import java.util.List;
import java.util.Optional;

/**
 * Generic repository interface for CRUD operations
 * Follows Repository Pattern for data access abstraction
 */
public interface Repository<T, ID> {
    
    /**
     * Save or update an entity
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



