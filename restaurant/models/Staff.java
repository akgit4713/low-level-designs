package restaurant.models;

import restaurant.enums.StaffRole;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

/**
 * Represents a staff member of the restaurant
 */
public class Staff {
    private final String id;
    private final String name;
    private final String email;
    private final String phone;
    private final StaffRole role;
    private final LocalDate hireDate;
    private final BigDecimal baseHourlyRate;
    
    // Performance tracking
    private int ordersServed;
    private double averageRating;
    private int totalRatings;
    
    // Schedule - simplified as start and end time for each day
    private final Map<String, WorkShift> weeklySchedule;

    public Staff(String id, String name, String email, String phone, 
                 StaffRole role, BigDecimal baseHourlyRate) {
        this.id = Objects.requireNonNull(id, "Staff ID cannot be null");
        this.name = Objects.requireNonNull(name, "Name cannot be null");
        this.email = email;
        this.phone = phone;
        this.role = Objects.requireNonNull(role, "Role cannot be null");
        this.baseHourlyRate = Objects.requireNonNull(baseHourlyRate, "Hourly rate cannot be null");
        this.hireDate = LocalDate.now();
        this.weeklySchedule = new HashMap<>();
        this.ordersServed = 0;
        this.averageRating = 0.0;
        this.totalRatings = 0;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public StaffRole getRole() {
        return role;
    }

    public LocalDate getHireDate() {
        return hireDate;
    }

    public BigDecimal getEffectiveHourlyRate() {
        return baseHourlyRate.multiply(BigDecimal.valueOf(role.getPayMultiplier()));
    }

    public int getOrdersServed() {
        return ordersServed;
    }

    public double getAverageRating() {
        return averageRating;
    }

    /**
     * Record a completed order served by this staff member
     */
    public void recordOrderServed() {
        this.ordersServed++;
    }

    /**
     * Add a customer rating for this staff member
     */
    public void addRating(double rating) {
        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }
        double totalScore = averageRating * totalRatings + rating;
        totalRatings++;
        averageRating = totalScore / totalRatings;
    }

    /**
     * Set work schedule for a day of the week
     */
    public void setSchedule(String dayOfWeek, LocalTime startTime, LocalTime endTime) {
        weeklySchedule.put(dayOfWeek.toUpperCase(), new WorkShift(startTime, endTime));
    }

    /**
     * Check if staff is scheduled to work at given time
     */
    public boolean isWorkingAt(String dayOfWeek, LocalTime time) {
        WorkShift shift = weeklySchedule.get(dayOfWeek.toUpperCase());
        if (shift == null) return false;
        return !time.isBefore(shift.startTime) && !time.isAfter(shift.endTime);
    }

    public Map<String, WorkShift> getWeeklySchedule() {
        return Collections.unmodifiableMap(weeklySchedule);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Staff staff = (Staff) o;
        return Objects.equals(id, staff.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("Staff{id='%s', name='%s', role=%s, rating=%.1f}",
            id, name, role, averageRating);
    }

    /**
     * Represents a work shift
     */
    public static class WorkShift {
        private final LocalTime startTime;
        private final LocalTime endTime;

        public WorkShift(LocalTime startTime, LocalTime endTime) {
            this.startTime = startTime;
            this.endTime = endTime;
        }

        public LocalTime getStartTime() {
            return startTime;
        }

        public LocalTime getEndTime() {
            return endTime;
        }

        @Override
        public String toString() {
            return startTime + " - " + endTime;
        }
    }
}

