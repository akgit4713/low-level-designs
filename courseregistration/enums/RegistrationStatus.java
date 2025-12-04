package courseregistration.enums;

/**
 * Represents the status of a course registration.
 */
public enum RegistrationStatus {
    PENDING("Pending", "Registration is pending approval"),
    CONFIRMED("Confirmed", "Registration is confirmed"),
    WAITLISTED("Waitlisted", "Student is on the waiting list"),
    DROPPED("Dropped", "Student dropped the course"),
    CANCELLED("Cancelled", "Registration was cancelled");
    
    private final String displayName;
    private final String description;
    
    RegistrationStatus(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public String getDescription() {
        return description;
    }
    
    public boolean isActive() {
        return this == CONFIRMED || this == PENDING || this == WAITLISTED;
    }
}



