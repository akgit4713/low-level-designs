package courseregistration.repositories;

import java.util.List;
import java.util.Optional;

/**
 * Generic repository interface for basic CRUD operations.
 * @param <T> Entity type
 * @param <ID> ID type
 */
public interface Repository<T, ID> {
    
    /**
     * Saves an entity.
     */
    T save(T entity);
    
    /**
     * Finds an entity by its ID.
     */
    Optional<T> findById(ID id);
    
    /**
     * Returns all entities.
     */
    List<T> findAll();
    
    /**
     * Deletes an entity by its ID.
     */
    boolean deleteById(ID id);
    
    /**
     * Checks if an entity exists by ID.
     */
    boolean existsById(ID id);
    
    /**
     * Returns the count of all entities.
     */
    long count();
}



