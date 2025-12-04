package courseregistration.exceptions;

/**
 * Exception thrown when a course is not found.
 */
public class CourseNotFoundException extends CourseRegistrationException {
    
    private final String courseCode;
    
    public CourseNotFoundException(String courseCode) {
        super("Course not found: " + courseCode);
        this.courseCode = courseCode;
    }
    
    public String getCourseCode() {
        return courseCode;
    }
}



