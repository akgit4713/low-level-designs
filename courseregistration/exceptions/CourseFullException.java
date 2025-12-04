package courseregistration.exceptions;

/**
 * Exception thrown when attempting to register for a course that has reached its maximum capacity.
 */
public class CourseFullException extends CourseRegistrationException {
    
    private final String courseCode;
    private final int maxCapacity;
    
    public CourseFullException(String courseCode, int maxCapacity) {
        super("Course " + courseCode + " is full. Maximum capacity: " + maxCapacity);
        this.courseCode = courseCode;
        this.maxCapacity = maxCapacity;
    }
    
    public String getCourseCode() {
        return courseCode;
    }
    
    public int getMaxCapacity() {
        return maxCapacity;
    }
}



