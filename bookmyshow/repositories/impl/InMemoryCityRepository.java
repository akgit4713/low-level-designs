package bookmyshow.repositories.impl;

import bookmyshow.models.City;
import bookmyshow.repositories.CityRepository;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory implementation of CityRepository.
 * Thread-safe using ConcurrentHashMap.
 */
public class InMemoryCityRepository implements CityRepository {
    private final Map<String, City> cities = new ConcurrentHashMap<>();
    private final Map<String, String> nameIndex = new ConcurrentHashMap<>();  // name -> cityId

    @Override
    public void save(City city) {
        cities.put(city.getId(), city);
        nameIndex.put(city.getName().toLowerCase(), city.getId());
    }

    @Override
    public Optional<City> findById(String id) {
        return Optional.ofNullable(cities.get(id));
    }

    @Override
    public Optional<City> findByName(String name) {
        String cityId = nameIndex.get(name.toLowerCase());
        return cityId != null ? findById(cityId) : Optional.empty();
    }

    @Override
    public List<City> findAll() {
        return cities.values().stream().toList();
    }

    @Override
    public void delete(String id) {
        City city = cities.remove(id);
        if (city != null) {
            nameIndex.remove(city.getName().toLowerCase());
        }
    }

    @Override
    public boolean exists(String id) {
        return cities.containsKey(id);
    }
}



