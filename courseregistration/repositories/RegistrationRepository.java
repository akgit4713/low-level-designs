package courseregistration.repositories;

import courseregistration.enums.RegistrationStatus;
import courseregistration.models.Registration;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Registration entities.
 */
public interface RegistrationRepository extends Repository<Registration, String> {
    
    /**
     * Finds all registrations for a student.
     */
    List<Registration> findByStudentId(String studentId);
    
    /**
     * Finds all registrations for a course.
     */
    List<Registration> findByCourseId(String courseId);
    
    /**
     * Finds a registration by student ID and course ID.
     */
    Optional<Registration> findByStudentIdAndCourseId(String studentId, String courseId);
    
    /**
     * Finds all registrations with a specific status.
     */
    List<Registration> findByStatus(RegistrationStatus status);
    
    /**
     * Finds all active registrations for a student.
     */
    List<Registration> findActiveByStudentId(String studentId);
    
    /**
     * Finds all active registrations for a course.
     */
    List<Registration> findActiveByCourseId(String courseId);
    
    /**
     * Checks if a student has an active registration for a course.
     */
    boolean hasActiveRegistration(String studentId, String courseId);
}



