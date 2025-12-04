package courseregistration.observers;

import courseregistration.models.Course;
import courseregistration.models.Registration;
import courseregistration.models.Student;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Observer that maintains an audit log of all registration events.
 */
public class AuditLogObserver implements RegistrationObserver {
    
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final List<AuditEntry> auditLog = Collections.synchronizedList(new ArrayList<>());
    
    public record AuditEntry(LocalDateTime timestamp, String action, String details) {
        @Override
        public String toString() {
            return String.format("[%s] %s: %s", 
                    timestamp.format(FORMATTER), action, details);
        }
    }
    
    @Override
    public void onRegistrationCreated(Registration registration, Student student, Course course) {
        String details = String.format("Student %s (%s) registered for %s (%s)",
                student.getName(), student.getStudentId(),
                course.getName(), course.getCourseCode());
        logEvent("REGISTRATION_CREATED", details);
    }
    
    @Override
    public void onRegistrationDropped(Registration registration, Student student, Course course) {
        String details = String.format("Student %s (%s) dropped %s (%s)",
                student.getName(), student.getStudentId(),
                course.getName(), course.getCourseCode());
        logEvent("REGISTRATION_DROPPED", details);
    }
    
    @Override
    public void onCourseFullCapacity(Course course) {
        String details = String.format("Course %s (%s) reached full capacity (%d students)",
                course.getName(), course.getCourseCode(), course.getMaxCapacity());
        logEvent("COURSE_FULL", details);
    }
    
    @Override
    public void onCourseSeatsAvailable(Course course) {
        String details = String.format("Course %s (%s) has %d available seats",
                course.getName(), course.getCourseCode(), course.getAvailableSeats());
        logEvent("SEATS_AVAILABLE", details);
    }
    
    private void logEvent(String action, String details) {
        AuditEntry entry = new AuditEntry(LocalDateTime.now(), action, details);
        auditLog.add(entry);
        System.out.println("📋 [AUDIT] " + entry);
    }
    
    public List<AuditEntry> getAuditLog() {
        return Collections.unmodifiableList(auditLog);
    }
    
    public List<AuditEntry> getRecentEntries(int count) {
        int size = auditLog.size();
        int start = Math.max(0, size - count);
        return auditLog.subList(start, size);
    }
    
    public void printAuditLog() {
        System.out.println("\n═══════════════════════════════════════════════════════════════════");
        System.out.println("                           AUDIT LOG");
        System.out.println("═══════════════════════════════════════════════════════════════════");
        if (auditLog.isEmpty()) {
            System.out.println("No audit entries.");
        } else {
            auditLog.forEach(entry -> System.out.println("  " + entry));
        }
        System.out.println("═══════════════════════════════════════════════════════════════════\n");
    }
}



