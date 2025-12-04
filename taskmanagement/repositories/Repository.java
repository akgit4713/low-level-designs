package taskmanagement.repositories;

import java.util.List;
import java.util.Optional;

/**
 * Generic repository interface for CRUD operations.
 * @param <T> Entity type
 * @param <ID> ID type
 */
public interface Repository<T, ID> {
    
    /**
     * Saves an entity.
     */
    T save(T entity);
    
    /**
     * Finds an entity by ID.
     */
    Optional<T> findById(ID id);
    
    /**
     * Returns all entities.
     */
    List<T> findAll();
    
    /**
     * Deletes an entity by ID.
     */
    boolean delete(ID id);
    
    /**
     * Checks if an entity exists by ID.
     */
    boolean existsById(ID id);
    
    /**
     * Returns the count of all entities.
     */
    long count();
    
    /**
     * Deletes all entities.
     */
    void deleteAll();
}



