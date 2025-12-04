package courseregistration.services;

import courseregistration.models.Course;
import courseregistration.models.Registration;
import courseregistration.observers.RegistrationObserver;

import java.util.List;

/**
 * Service interface for registration operations.
 * Handles concurrent registration with thread-safety guarantees.
 */
public interface RegistrationService {
    
    /**
     * Registers a student for a course.
     * Thread-safe: handles concurrent registrations.
     * 
     * @param studentId Internal student ID
     * @param courseId Internal course ID
     * @return The created registration
     * @throws courseregistration.exceptions.CourseFullException if course is at capacity
     * @throws courseregistration.exceptions.DuplicateRegistrationException if already registered
     * @throws courseregistration.exceptions.StudentNotFoundException if student not found
     * @throws courseregistration.exceptions.CourseNotFoundException if course not found
     */
    Registration registerStudentForCourse(String studentId, String courseId);
    
    /**
     * Registers a student for a course using course code.
     */
    Registration registerStudentForCourseByCode(String studentId, String courseCode);
    
    /**
     * Drops a student from a course.
     */
    Registration dropRegistration(String studentId, String courseId);
    
    /**
     * Gets all courses a student is registered for.
     */
    List<Course> getStudentCourses(String studentId);
    
    /**
     * Gets all registrations for a student.
     */
    List<Registration> getStudentRegistrations(String studentId);
    
    /**
     * Gets all active registrations for a course.
     */
    List<Registration> getCourseRegistrations(String courseId);
    
    /**
     * Checks if a student is registered for a course.
     */
    boolean isStudentRegistered(String studentId, String courseId);
    
    /**
     * Registers an observer for registration events.
     */
    void registerObserver(RegistrationObserver observer);
    
    /**
     * Unregisters an observer.
     */
    void unregisterObserver(RegistrationObserver observer);
}



