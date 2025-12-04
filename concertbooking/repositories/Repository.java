package concertbooking.repositories;

import java.util.List;
import java.util.Optional;

/**
 * Base repository interface for data access
 */
public interface Repository<T, ID> {
    
    T save(T entity);
    
    Optional<T> findById(ID id);
    
    List<T> findAll();
    
    boolean deleteById(ID id);
    
    boolean existsById(ID id);
    
    long count();
}



