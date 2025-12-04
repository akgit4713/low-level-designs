package restaurant.services.impl;

import restaurant.enums.StaffRole;
import restaurant.models.Staff;
import restaurant.repositories.impl.InMemoryStaffRepository;
import restaurant.services.StaffService;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

/**
 * Implementation of StaffService
 */
public class StaffServiceImpl implements StaffService {
    
    private final InMemoryStaffRepository staffRepository;
    
    public StaffServiceImpl(InMemoryStaffRepository staffRepository) {
        this.staffRepository = staffRepository;
    }
    
    @Override
    public Staff addStaff(Staff staff) {
        return staffRepository.save(staff);
    }
    
    @Override
    public Optional<Staff> getStaff(String staffId) {
        return staffRepository.findById(staffId);
    }
    
    @Override
    public List<Staff> getAllStaff() {
        return staffRepository.findAll();
    }
    
    @Override
    public List<Staff> getStaffByRole(StaffRole role) {
        return staffRepository.findByRole(role);
    }
    
    @Override
    public List<Staff> getAvailableStaff(String dayOfWeek, LocalTime time) {
        return staffRepository.findAvailable(dayOfWeek, time);
    }
    
    @Override
    public List<Staff> getAvailableWaiters(String dayOfWeek, LocalTime time) {
        return staffRepository.findAvailableWaiters(dayOfWeek, time);
    }
    
    @Override
    public void setSchedule(String staffId, String dayOfWeek, LocalTime start, LocalTime end) {
        staffRepository.findById(staffId).ifPresent(staff -> 
            staff.setSchedule(dayOfWeek, start, end)
        );
    }
    
    @Override
    public void recordOrderServed(String staffId) {
        staffRepository.findById(staffId).ifPresent(Staff::recordOrderServed);
    }
    
    @Override
    public void addStaffRating(String staffId, double rating) {
        staffRepository.findById(staffId).ifPresent(staff -> staff.addRating(rating));
    }
    
    @Override
    public List<Staff> getTopPerformers(int limit) {
        return staffRepository.findTopPerformers(limit);
    }
    
    @Override
    public boolean removeStaff(String staffId) {
        return staffRepository.deleteById(staffId);
    }
}

