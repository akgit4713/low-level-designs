package courseregistration.repositories;

import courseregistration.models.Course;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Course entities.
 */
public interface CourseRepository extends Repository<Course, String> {
    
    /**
     * Finds a course by its course code.
     */
    Optional<Course> findByCourseCode(String courseCode);
    
    /**
     * Finds all courses by instructor.
     */
    List<Course> findByInstructor(String instructor);
    
    /**
     * Finds all courses in a department.
     */
    List<Course> findByDepartment(String department);
    
    /**
     * Searches courses by name (case-insensitive partial match).
     */
    List<Course> searchByName(String name);
    
    /**
     * Finds all courses with available seats.
     */
    List<Course> findAvailableCourses();
    
    /**
     * Finds all courses that are full.
     */
    List<Course> findFullCourses();
}



