package concertbooking.repositories.impl;

import concertbooking.enums.ConcertStatus;
import concertbooking.models.Concert;
import concertbooking.repositories.ConcertRepository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Thread-safe in-memory implementation of ConcertRepository
 */
public class InMemoryConcertRepository implements ConcertRepository {
    
    private final Map<String, Concert> concerts = new ConcurrentHashMap<>();
    
    @Override
    public Concert save(Concert entity) {
        concerts.put(entity.getId(), entity);
        return entity;
    }
    
    @Override
    public Optional<Concert> findById(String id) {
        return Optional.ofNullable(concerts.get(id));
    }
    
    @Override
    public List<Concert> findAll() {
        return new ArrayList<>(concerts.values());
    }
    
    @Override
    public boolean deleteById(String id) {
        return concerts.remove(id) != null;
    }
    
    @Override
    public boolean existsById(String id) {
        return concerts.containsKey(id);
    }
    
    @Override
    public long count() {
        return concerts.size();
    }
    
    @Override
    public List<Concert> findByStatus(ConcertStatus status) {
        return concerts.values().stream()
            .filter(c -> c.getStatus() == status)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<Concert> findByArtist(String artist) {
        return concerts.values().stream()
            .filter(c -> c.getArtist().toLowerCase().contains(artist.toLowerCase()))
            .collect(Collectors.toList());
    }
    
    @Override
    public List<Concert> findByVenueCity(String city) {
        return concerts.values().stream()
            .filter(c -> c.getVenue().getCity().equalsIgnoreCase(city))
            .collect(Collectors.toList());
    }
    
    @Override
    public List<Concert> findByDateRange(LocalDateTime start, LocalDateTime end) {
        return concerts.values().stream()
            .filter(c -> !c.getDateTime().isBefore(start) && !c.getDateTime().isAfter(end))
            .sorted(Comparator.comparing(Concert::getDateTime))
            .collect(Collectors.toList());
    }
    
    @Override
    public List<Concert> findUpcoming() {
        LocalDateTime now = LocalDateTime.now();
        return concerts.values().stream()
            .filter(c -> c.getDateTime().isAfter(now))
            .filter(c -> c.getStatus() != ConcertStatus.CANCELLED)
            .sorted(Comparator.comparing(Concert::getDateTime))
            .collect(Collectors.toList());
    }
    
    @Override
    public List<Concert> findBookable() {
        return concerts.values().stream()
            .filter(Concert::isBookable)
            .sorted(Comparator.comparing(Concert::getDateTime))
            .collect(Collectors.toList());
    }
}



