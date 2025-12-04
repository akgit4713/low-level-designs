package courseregistration.exceptions;

/**
 * Exception thrown when a student attempts to register for a course they're already registered in.
 */
public class DuplicateRegistrationException extends CourseRegistrationException {
    
    private final String studentId;
    private final String courseCode;
    
    public DuplicateRegistrationException(String studentId, String courseCode) {
        super("Student " + studentId + " is already registered for course " + courseCode);
        this.studentId = studentId;
        this.courseCode = courseCode;
    }
    
    public String getStudentId() {
        return studentId;
    }
    
    public String getCourseCode() {
        return courseCode;
    }
}



