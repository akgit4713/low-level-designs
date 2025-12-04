package bookmyshow.repositories;

import bookmyshow.models.Theater;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Theater entity operations.
 */
public interface TheaterRepository {
    void save(Theater theater);
    Optional<Theater> findById(String id);
    List<Theater> findAll();
    List<Theater> findByCityId(String cityId);
    List<Theater> findByName(String name);
    void delete(String id);
    boolean exists(String id);
}



