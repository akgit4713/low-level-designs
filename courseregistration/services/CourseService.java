package courseregistration.services;

import courseregistration.models.Course;
import courseregistration.strategies.search.CourseSearchStrategy;

import java.util.List;
import java.util.Optional;

/**
 * Service interface for course operations.
 */
public interface CourseService {
    
    /**
     * Creates a new course.
     */
    Course createCourse(String courseCode, String name, String instructor, int maxCapacity);
    
    /**
     * Creates a new course with additional details.
     */
    Course createCourse(String courseCode, String name, String instructor, int maxCapacity,
                        String department, int credits);
    
    /**
     * Gets a course by internal ID.
     */
    Optional<Course> getCourseById(String id);
    
    /**
     * Gets a course by course code.
     */
    Optional<Course> getCourseByCourseCode(String courseCode);
    
    /**
     * Gets all courses.
     */
    List<Course> getAllCourses();
    
    /**
     * Searches courses by code.
     */
    List<Course> searchByCode(String code);
    
    /**
     * Searches courses by name.
     */
    List<Course> searchByName(String name);
    
    /**
     * Searches courses by instructor.
     */
    List<Course> searchByInstructor(String instructor);
    
    /**
     * Gets all courses with available seats.
     */
    List<Course> getAvailableCourses();
    
    /**
     * Searches courses using a custom strategy.
     */
    List<Course> search(CourseSearchStrategy strategy);
    
    /**
     * Updates course information.
     */
    Course updateCourse(String id, String name, String instructor, String department, int credits);
    
    /**
     * Deletes a course.
     */
    boolean deleteCourse(String id);
}



