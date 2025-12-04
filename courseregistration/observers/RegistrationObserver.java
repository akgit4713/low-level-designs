package courseregistration.observers;

import courseregistration.models.Course;
import courseregistration.models.Registration;
import courseregistration.models.Student;

/**
 * Observer interface for registration events.
 * Follows the Observer pattern for extensible notification handling.
 */
public interface RegistrationObserver {
    
    /**
     * Called when a student successfully registers for a course.
     */
    void onRegistrationCreated(Registration registration, Student student, Course course);
    
    /**
     * Called when a registration is dropped.
     */
    void onRegistrationDropped(Registration registration, Student student, Course course);
    
    /**
     * Called when a course reaches full capacity.
     */
    void onCourseFullCapacity(Course course);
    
    /**
     * Called when a course has available seats again (after being full).
     */
    void onCourseSeatsAvailable(Course course);
}



