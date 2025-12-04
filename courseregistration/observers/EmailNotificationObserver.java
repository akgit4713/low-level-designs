package courseregistration.observers;

import courseregistration.models.Course;
import courseregistration.models.Registration;
import courseregistration.models.Student;

/**
 * Observer that sends email notifications for registration events.
 * In a real system, this would integrate with an email service.
 */
public class EmailNotificationObserver implements RegistrationObserver {
    
    @Override
    public void onRegistrationCreated(Registration registration, Student student, Course course) {
        System.out.println("📧 [EMAIL] Sending confirmation to " + student.getEmail() + ":");
        System.out.println("   Subject: Registration Confirmed - " + course.getCourseCode());
        System.out.println("   Dear " + student.getName() + ",");
        System.out.println("   You have successfully registered for " + course.getName() + 
                          " (" + course.getCourseCode() + ")");
        System.out.println("   Instructor: " + course.getInstructor());
        System.out.println();
    }
    
    @Override
    public void onRegistrationDropped(Registration registration, Student student, Course course) {
        System.out.println("📧 [EMAIL] Sending notification to " + student.getEmail() + ":");
        System.out.println("   Subject: Course Dropped - " + course.getCourseCode());
        System.out.println("   Dear " + student.getName() + ",");
        System.out.println("   You have been dropped from " + course.getName() + 
                          " (" + course.getCourseCode() + ")");
        System.out.println();
    }
    
    @Override
    public void onCourseFullCapacity(Course course) {
        System.out.println("📧 [EMAIL] Notifying instructor " + course.getInstructor() + ":");
        System.out.println("   Subject: Course Full - " + course.getCourseCode());
        System.out.println("   Course " + course.getName() + " has reached full capacity.");
        System.out.println();
    }
    
    @Override
    public void onCourseSeatsAvailable(Course course) {
        System.out.println("📧 [EMAIL] Notifying waitlisted students:");
        System.out.println("   Subject: Seats Available - " + course.getCourseCode());
        System.out.println("   Seats are now available in " + course.getName());
        System.out.println();
    }
}



