package fooddelivery.repositories;

import fooddelivery.enums.CuisineType;
import fooddelivery.enums.RestaurantStatus;
import fooddelivery.models.Restaurant;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Restaurant entities.
 */
public interface RestaurantRepository {
    Restaurant save(Restaurant restaurant);
    Optional<Restaurant> findById(String id);
    List<Restaurant> findByOwnerId(String ownerId);
    List<Restaurant> findByStatus(RestaurantStatus status);
    List<Restaurant> findByCuisineType(CuisineType cuisineType);
    List<Restaurant> findByCity(String city);
    List<Restaurant> findAll();
    void delete(String id);
    boolean existsById(String id);
}



