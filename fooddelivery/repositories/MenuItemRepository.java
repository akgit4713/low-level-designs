package fooddelivery.repositories;

import fooddelivery.enums.CuisineType;
import fooddelivery.models.MenuItem;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for MenuItem entities.
 */
public interface MenuItemRepository {
    MenuItem save(MenuItem menuItem);
    Optional<MenuItem> findById(String id);
    List<MenuItem> findByRestaurantId(String restaurantId);
    List<MenuItem> findByCuisineType(CuisineType cuisineType);
    List<MenuItem> findAvailableByRestaurantId(String restaurantId);
    void delete(String id);
    boolean existsById(String id);
}



