package airline.repositories;

import java.util.List;
import java.util.Optional;

/**
 * Generic repository interface for CRUD operations.
 * 
 * @param <T> The entity type
 * @param <ID> The ID type
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
    boolean deleteById(ID id);
    
    /**
     * Checks if an entity exists.
     */
    boolean existsById(ID id);
    
    /**
     * Returns the count of entities.
     */
    long count();
}



