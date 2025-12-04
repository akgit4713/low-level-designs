package carrental.repositories;

import java.util.List;
import java.util.Optional;

/**
 * Generic repository interface following Repository pattern.
 * Provides basic CRUD operations for all entities.
 * 
 * @param <T> Entity type
 * @param <ID> ID type
 */
public interface Repository<T, ID> {
    
    /**
     * Saves an entity to the repository.
     */
    T save(T entity);
    
    /**
     * Finds an entity by its ID.
     */
    Optional<T> findById(ID id);
    
    /**
     * Returns all entities in the repository.
     */
    List<T> findAll();
    
    /**
     * Deletes an entity by its ID.
     */
    boolean deleteById(ID id);
    
    /**
     * Checks if an entity exists with the given ID.
     */
    boolean existsById(ID id);
    
    /**
     * Returns the count of entities.
     */
    long count();
}



