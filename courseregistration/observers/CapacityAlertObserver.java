package courseregistration.observers;

import courseregistration.models.Course;
import courseregistration.models.Registration;
import courseregistration.models.Student;

/**
 * Observer that alerts when courses are nearing capacity.
 */
public class CapacityAlertObserver implements RegistrationObserver {
    
    private final double warningThreshold; // e.g., 0.9 for 90% full
    
    public CapacityAlertObserver() {
        this(0.9);
    }
    
    public CapacityAlertObserver(double warningThreshold) {
        if (warningThreshold <= 0 || warningThreshold > 1) {
            throw new IllegalArgumentException("Threshold must be between 0 and 1");
        }
        this.warningThreshold = warningThreshold;
    }
    
    @Override
    public void onRegistrationCreated(Registration registration, Student student, Course course) {
        checkCapacityWarning(course);
    }
    
    @Override
    public void onRegistrationDropped(Registration registration, Student student, Course course) {
        // No action needed
    }
    
    @Override
    public void onCourseFullCapacity(Course course) {
        System.out.println("⚠️  [ALERT] Course " + course.getCourseCode() + " is now FULL!");
        System.out.println("   Enrollment: " + course.getCurrentEnrollment() + "/" + course.getMaxCapacity());
    }
    
    @Override
    public void onCourseSeatsAvailable(Course course) {
        System.out.println("✅ [ALERT] Course " + course.getCourseCode() + " has available seats again.");
        System.out.println("   Enrollment: " + course.getCurrentEnrollment() + "/" + course.getMaxCapacity());
    }
    
    private void checkCapacityWarning(Course course) {
        double fillRate = (double) course.getCurrentEnrollment() / course.getMaxCapacity();
        if (fillRate >= warningThreshold && !course.isFull()) {
            int percentFull = (int) (fillRate * 100);
            System.out.println("⚠️  [WARNING] Course " + course.getCourseCode() + 
                             " is " + percentFull + "% full!");
            System.out.println("   Only " + course.getAvailableSeats() + " seat(s) remaining.");
        }
    }
}



