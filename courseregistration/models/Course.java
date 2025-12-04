package courseregistration.models;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Represents a course in the university course registration system.
 * Thread-safe for concurrent registration operations.
 */
public class Course {
    
    private final String id;
    private final String courseCode;     // e.g., "CS101"
    private String name;
    private String description;
    private String instructor;
    private final int maxCapacity;
    private final AtomicInteger currentEnrollment;
    private String department;
    private int credits;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Lock for thread-safe registration
    private final ReentrantLock registrationLock;
    
    public Course(String courseCode, String name, String instructor, int maxCapacity) {
        this.id = UUID.randomUUID().toString();
        this.courseCode = Objects.requireNonNull(courseCode, "Course code cannot be null");
        this.name = Objects.requireNonNull(name, "Course name cannot be null");
        this.instructor = Objects.requireNonNull(instructor, "Instructor cannot be null");
        
        if (maxCapacity <= 0) {
            throw new IllegalArgumentException("Max capacity must be positive");
        }
        this.maxCapacity = maxCapacity;
        this.currentEnrollment = new AtomicInteger(0);
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
        this.registrationLock = new ReentrantLock();
        this.credits = 3; // default credits
    }
    
    public Course(String courseCode, String name, String instructor, int maxCapacity, 
                  String department, int credits) {
        this(courseCode, name, instructor, maxCapacity);
        this.department = department;
        this.credits = credits;
    }
    
    public String getId() {
        return id;
    }
    
    public String getCourseCode() {
        return courseCode;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = Objects.requireNonNull(name, "Course name cannot be null");
        this.updatedAt = LocalDateTime.now();
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
        this.updatedAt = LocalDateTime.now();
    }
    
    public String getInstructor() {
        return instructor;
    }
    
    public void setInstructor(String instructor) {
        this.instructor = Objects.requireNonNull(instructor, "Instructor cannot be null");
        this.updatedAt = LocalDateTime.now();
    }
    
    public int getMaxCapacity() {
        return maxCapacity;
    }
    
    public int getCurrentEnrollment() {
        return currentEnrollment.get();
    }
    
    public int getAvailableSeats() {
        return maxCapacity - currentEnrollment.get();
    }
    
    public boolean isFull() {
        return currentEnrollment.get() >= maxCapacity;
    }
    
    public String getDepartment() {
        return department;
    }
    
    public void setDepartment(String department) {
        this.department = department;
        this.updatedAt = LocalDateTime.now();
    }
    
    public int getCredits() {
        return credits;
    }
    
    public void setCredits(int credits) {
        if (credits < 0) {
            throw new IllegalArgumentException("Credits cannot be negative");
        }
        this.credits = credits;
        this.updatedAt = LocalDateTime.now();
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    /**
     * Acquires the registration lock for thread-safe operations.
     */
    public void lock() {
        registrationLock.lock();
    }
    
    /**
     * Releases the registration lock.
     */
    public void unlock() {
        registrationLock.unlock();
    }
    
    /**
     * Attempts to increment enrollment. Thread-safe.
     * @return true if enrollment was incremented, false if course is full
     */
    public boolean tryIncrementEnrollment() {
        while (true) {
            int current = currentEnrollment.get();
            if (current >= maxCapacity) {
                return false;
            }
            if (currentEnrollment.compareAndSet(current, current + 1)) {
                this.updatedAt = LocalDateTime.now();
                return true;
            }
        }
    }
    
    /**
     * Decrements enrollment count. Thread-safe.
     */
    public void decrementEnrollment() {
        currentEnrollment.updateAndGet(current -> Math.max(0, current - 1));
        this.updatedAt = LocalDateTime.now();
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Course course = (Course) o;
        return Objects.equals(id, course.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    
    @Override
    public String toString() {
        return String.format("Course[code=%s, name=%s, instructor=%s, enrollment=%d/%d]",
                courseCode, name, instructor, currentEnrollment.get(), maxCapacity);
    }
}



