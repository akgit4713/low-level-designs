package courseregistration.services;

import courseregistration.models.Student;

import java.util.List;
import java.util.Optional;

/**
 * Service interface for student operations.
 */
public interface StudentService {
    
    /**
     * Creates a new student.
     */
    Student createStudent(String studentId, String name, String email, String department);
    
    /**
     * Gets a student by internal ID.
     */
    Optional<Student> getStudentById(String id);
    
    /**
     * Gets a student by university student ID.
     */
    Optional<Student> getStudentByStudentId(String studentId);
    
    /**
     * Gets all students.
     */
    List<Student> getAllStudents();
    
    /**
     * Gets all students in a department.
     */
    List<Student> getStudentsByDepartment(String department);
    
    /**
     * Searches students by name.
     */
    List<Student> searchByName(String name);
    
    /**
     * Updates a student's information.
     */
    Student updateStudent(String id, String name, String email, String department);
    
    /**
     * Deletes a student.
     */
    boolean deleteStudent(String id);
}



