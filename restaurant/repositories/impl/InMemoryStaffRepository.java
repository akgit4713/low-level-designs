package restaurant.repositories.impl;

import restaurant.enums.StaffRole;
import restaurant.models.Staff;
import restaurant.repositories.Repository;

import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Thread-safe in-memory repository for staff
 */
public class InMemoryStaffRepository implements Repository<Staff, String> {
    
    private final Map<String, Staff> staff = new ConcurrentHashMap<>();
    
    @Override
    public Staff save(Staff entity) {
        staff.put(entity.getId(), entity);
        return entity;
    }
    
    @Override
    public Optional<Staff> findById(String id) {
        return Optional.ofNullable(staff.get(id));
    }
    
    @Override
    public List<Staff> findAll() {
        return new ArrayList<>(staff.values());
    }
    
    @Override
    public boolean deleteById(String id) {
        return staff.remove(id) != null;
    }
    
    @Override
    public boolean existsById(String id) {
        return staff.containsKey(id);
    }
    
    @Override
    public long count() {
        return staff.size();
    }
    
    /**
     * Find staff by role
     */
    public List<Staff> findByRole(StaffRole role) {
        return staff.values().stream()
            .filter(s -> s.getRole() == role)
            .collect(Collectors.toList());
    }
    
    /**
     * Find available staff for a given day and time
     */
    public List<Staff> findAvailable(String dayOfWeek, LocalTime time) {
        return staff.values().stream()
            .filter(s -> s.isWorkingAt(dayOfWeek, time))
            .collect(Collectors.toList());
    }
    
    /**
     * Find available waiters
     */
    public List<Staff> findAvailableWaiters(String dayOfWeek, LocalTime time) {
        return staff.values().stream()
            .filter(s -> s.getRole() == StaffRole.WAITER)
            .filter(s -> s.isWorkingAt(dayOfWeek, time))
            .collect(Collectors.toList());
    }
    
    /**
     * Get top performing staff by rating
     */
    public List<Staff> findTopPerformers(int limit) {
        return staff.values().stream()
            .sorted(Comparator.comparingDouble(Staff::getAverageRating).reversed())
            .limit(limit)
            .collect(Collectors.toList());
    }
}

