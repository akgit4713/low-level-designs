package courseregistration.exceptions;

/**
 * Exception thrown when a registration is not found.
 */
public class RegistrationNotFoundException extends CourseRegistrationException {
    
    private final String registrationId;
    
    public RegistrationNotFoundException(String registrationId) {
        super("Registration not found: " + registrationId);
        this.registrationId = registrationId;
    }
    
    public String getRegistrationId() {
        return registrationId;
    }
}



