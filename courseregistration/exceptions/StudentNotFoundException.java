package courseregistration.exceptions;

/**
 * Exception thrown when a student is not found.
 */
public class StudentNotFoundException extends CourseRegistrationException {
    
    private final String studentId;
    
    public StudentNotFoundException(String studentId) {
        super("Student not found: " + studentId);
        this.studentId = studentId;
    }
    
    public String getStudentId() {
        return studentId;
    }
}



