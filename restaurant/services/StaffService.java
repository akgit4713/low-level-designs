package restaurant.services;

import restaurant.enums.StaffRole;
import restaurant.models.Staff;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

/**
 * Service interface for staff management
 */
public interface StaffService {
    
    /**
     * Add new staff member
     */
    Staff addStaff(Staff staff);
    
    /**
     * Get staff by ID
     */
    Optional<Staff> getStaff(String staffId);
    
    /**
     * Get all staff
     */
    List<Staff> getAllStaff();
    
    /**
     * Get staff by role
     */
    List<Staff> getStaffByRole(StaffRole role);
    
    /**
     * Get available staff for given day and time
     */
    List<Staff> getAvailableStaff(String dayOfWeek, LocalTime time);
    
    /**
     * Get available waiters
     */
    List<Staff> getAvailableWaiters(String dayOfWeek, LocalTime time);
    
    /**
     * Set staff schedule
     */
    void setSchedule(String staffId, String dayOfWeek, LocalTime start, LocalTime end);
    
    /**
     * Record order served by staff
     */
    void recordOrderServed(String staffId);
    
    /**
     * Add rating for staff
     */
    void addStaffRating(String staffId, double rating);
    
    /**
     * Get top performers
     */
    List<Staff> getTopPerformers(int limit);
    
    /**
     * Remove staff
     */
    boolean removeStaff(String staffId);
}

