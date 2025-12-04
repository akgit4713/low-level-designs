package concertbooking.repositories.impl;

import concertbooking.models.WaitlistEntry;
import concertbooking.repositories.WaitlistRepository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Thread-safe in-memory implementation of WaitlistRepository
 */
public class InMemoryWaitlistRepository implements WaitlistRepository {
    
    private final Map<String, WaitlistEntry> entries = new ConcurrentHashMap<>();
    
    @Override
    public WaitlistEntry save(WaitlistEntry entity) {
        entries.put(entity.getId(), entity);
        return entity;
    }
    
    @Override
    public Optional<WaitlistEntry> findById(String id) {
        return Optional.ofNullable(entries.get(id));
    }
    
    @Override
    public List<WaitlistEntry> findAll() {
        return new ArrayList<>(entries.values());
    }
    
    @Override
    public boolean deleteById(String id) {
        return entries.remove(id) != null;
    }
    
    @Override
    public boolean existsById(String id) {
        return entries.containsKey(id);
    }
    
    @Override
    public long count() {
        return entries.size();
    }
    
    @Override
    public List<WaitlistEntry> findByConcertId(String concertId) {
        return entries.values().stream()
            .filter(e -> e.getConcertId().equals(concertId))
            .collect(Collectors.toList());
    }
    
    @Override
    public List<WaitlistEntry> findByUserId(String userId) {
        return entries.values().stream()
            .filter(e -> e.getUserId().equals(userId))
            .collect(Collectors.toList());
    }
    
    @Override
    public Optional<WaitlistEntry> findByUserIdAndConcertId(String userId, String concertId) {
        return entries.values().stream()
            .filter(e -> e.getUserId().equals(userId) && e.getConcertId().equals(concertId))
            .findFirst();
    }
    
    @Override
    public List<WaitlistEntry> findByConcertIdOrderedByCreatedAt(String concertId) {
        return entries.values().stream()
            .filter(e -> e.getConcertId().equals(concertId))
            .sorted(Comparator.comparing(WaitlistEntry::getCreatedAt))
            .collect(Collectors.toList());
    }
    
    @Override
    public List<WaitlistEntry> findUnnotifiedByConcertId(String concertId) {
        return entries.values().stream()
            .filter(e -> e.getConcertId().equals(concertId) && !e.isNotified())
            .sorted(Comparator.comparing(WaitlistEntry::getCreatedAt))
            .collect(Collectors.toList());
    }
}



