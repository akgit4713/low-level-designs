package courseregistration.models;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * Represents a student in the university course registration system.
 */
public class Student {
    
    private final String id;
    private final String studentId; // University-assigned student ID (e.g., "STU2024001")
    private String name;
    private String email;
    private String department;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    public Student(String studentId, String name, String email, String department) {
        this.id = UUID.randomUUID().toString();
        this.studentId = Objects.requireNonNull(studentId, "Student ID cannot be null");
        this.name = Objects.requireNonNull(name, "Name cannot be null");
        this.email = Objects.requireNonNull(email, "Email cannot be null");
        this.department = department;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
    }
    
    public String getId() {
        return id;
    }
    
    public String getStudentId() {
        return studentId;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = Objects.requireNonNull(name, "Name cannot be null");
        this.updatedAt = LocalDateTime.now();
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = Objects.requireNonNull(email, "Email cannot be null");
        this.updatedAt = LocalDateTime.now();
    }
    
    public String getDepartment() {
        return department;
    }
    
    public void setDepartment(String department) {
        this.department = department;
        this.updatedAt = LocalDateTime.now();
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Student student = (Student) o;
        return Objects.equals(id, student.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    
    @Override
    public String toString() {
        return String.format("Student[id=%s, studentId=%s, name=%s, dept=%s]",
                id, studentId, name, department);
    }
}



