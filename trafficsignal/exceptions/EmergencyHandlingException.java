package trafficsignal.exceptions;

/**
 * Exception thrown when emergency handling fails.
 */
public class EmergencyHandlingException extends TrafficSignalException {
    
    public EmergencyHandlingException(String message) {
        super(message);
    }

    public EmergencyHandlingException(String message, Throwable cause) {
        super(message, cause);
    }
}



