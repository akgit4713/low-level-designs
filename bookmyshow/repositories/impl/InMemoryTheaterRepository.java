package bookmyshow.repositories.impl;

import bookmyshow.models.Theater;
import bookmyshow.repositories.TheaterRepository;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * In-memory implementation of TheaterRepository.
 * Thread-safe using ConcurrentHashMap.
 */
public class InMemoryTheaterRepository implements TheaterRepository {
    private final Map<String, Theater> theaters = new ConcurrentHashMap<>();

    @Override
    public void save(Theater theater) {
        theaters.put(theater.getId(), theater);
    }

    @Override
    public Optional<Theater> findById(String id) {
        return Optional.ofNullable(theaters.get(id));
    }

    @Override
    public List<Theater> findAll() {
        return theaters.values().stream().toList();
    }

    @Override
    public List<Theater> findByCityId(String cityId) {
        return theaters.values().stream()
            .filter(t -> t.getCityId().equals(cityId))
            .collect(Collectors.toList());
    }

    @Override
    public List<Theater> findByName(String name) {
        return theaters.values().stream()
            .filter(t -> t.getName().toLowerCase().contains(name.toLowerCase()))
            .collect(Collectors.toList());
    }

    @Override
    public void delete(String id) {
        theaters.remove(id);
    }

    @Override
    public boolean exists(String id) {
        return theaters.containsKey(id);
    }
}



