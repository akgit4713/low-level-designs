package courseregistration.repositories;

import courseregistration.models.Student;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Student entities.
 */
public interface StudentRepository extends Repository<Student, String> {
    
    /**
     * Finds a student by their university student ID.
     */
    Optional<Student> findByStudentId(String studentId);
    
    /**
     * Finds a student by email.
     */
    Optional<Student> findByEmail(String email);
    
    /**
     * Finds all students in a department.
     */
    List<Student> findByDepartment(String department);
    
    /**
     * Searches students by name (case-insensitive partial match).
     */
    List<Student> searchByName(String name);
}



