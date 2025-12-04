package bookmyshow.repositories;

import bookmyshow.models.City;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for City entity operations.
 */
public interface CityRepository {
    void save(City city);
    Optional<City> findById(String id);
    Optional<City> findByName(String name);
    List<City> findAll();
    void delete(String id);
    boolean exists(String id);
}



