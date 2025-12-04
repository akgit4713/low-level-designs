package fooddelivery.repositories.impl;

import fooddelivery.enums.CuisineType;
import fooddelivery.enums.RestaurantStatus;
import fooddelivery.models.Restaurant;
import fooddelivery.repositories.RestaurantRepository;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Thread-safe in-memory implementation of RestaurantRepository.
 */
public class InMemoryRestaurantRepository implements RestaurantRepository {
    private final Map<String, Restaurant> restaurants = new ConcurrentHashMap<>();

    @Override
    public Restaurant save(Restaurant restaurant) {
        restaurants.put(restaurant.getId(), restaurant);
        return restaurant;
    }

    @Override
    public Optional<Restaurant> findById(String id) {
        return Optional.ofNullable(restaurants.get(id));
    }

    @Override
    public List<Restaurant> findByOwnerId(String ownerId) {
        return restaurants.values().stream()
                .filter(r -> r.getOwnerId().equals(ownerId))
                .toList();
    }

    @Override
    public List<Restaurant> findByStatus(RestaurantStatus status) {
        return restaurants.values().stream()
                .filter(r -> r.getStatus() == status)
                .toList();
    }

    @Override
    public List<Restaurant> findByCuisineType(CuisineType cuisineType) {
        return restaurants.values().stream()
                .filter(r -> r.getCuisineTypes().contains(cuisineType))
                .toList();
    }

    @Override
    public List<Restaurant> findByCity(String city) {
        return restaurants.values().stream()
                .filter(r -> r.getLocation().getCity().equalsIgnoreCase(city))
                .toList();
    }

    @Override
    public List<Restaurant> findAll() {
        return new ArrayList<>(restaurants.values());
    }

    @Override
    public void delete(String id) {
        restaurants.remove(id);
    }

    @Override
    public boolean existsById(String id) {
        return restaurants.containsKey(id);
    }
}



