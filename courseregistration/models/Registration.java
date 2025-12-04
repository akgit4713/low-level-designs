package courseregistration.models;

import courseregistration.enums.RegistrationStatus;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * Represents a student's registration for a course.
 */
public class Registration {
    
    private final String id;
    private final String studentId;
    private final String courseId;
    private RegistrationStatus status;
    private final LocalDateTime registeredAt;
    private LocalDateTime updatedAt;
    private LocalDateTime droppedAt;
    private String notes;
    
    public Registration(String studentId, String courseId) {
        this.id = UUID.randomUUID().toString();
        this.studentId = Objects.requireNonNull(studentId, "Student ID cannot be null");
        this.courseId = Objects.requireNonNull(courseId, "Course ID cannot be null");
        this.status = RegistrationStatus.CONFIRMED;
        this.registeredAt = LocalDateTime.now();
        this.updatedAt = this.registeredAt;
    }
    
    public Registration(String studentId, String courseId, RegistrationStatus status) {
        this(studentId, courseId);
        this.status = status;
    }
    
    public String getId() {
        return id;
    }
    
    public String getStudentId() {
        return studentId;
    }
    
    public String getCourseId() {
        return courseId;
    }
    
    public RegistrationStatus getStatus() {
        return status;
    }
    
    public void setStatus(RegistrationStatus status) {
        this.status = Objects.requireNonNull(status, "Status cannot be null");
        this.updatedAt = LocalDateTime.now();
        
        if (status == RegistrationStatus.DROPPED) {
            this.droppedAt = LocalDateTime.now();
        }
    }
    
    public LocalDateTime getRegisteredAt() {
        return registeredAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public LocalDateTime getDroppedAt() {
        return droppedAt;
    }
    
    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
        this.updatedAt = LocalDateTime.now();
    }
    
    public boolean isActive() {
        return status.isActive();
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Registration that = (Registration) o;
        return Objects.equals(id, that.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    
    @Override
    public String toString() {
        return String.format("Registration[id=%s, studentId=%s, courseId=%s, status=%s]",
                id, studentId, courseId, status);
    }
}



