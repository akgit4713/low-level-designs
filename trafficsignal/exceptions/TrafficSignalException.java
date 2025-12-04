package trafficsignal.exceptions;

/**
 * Base exception for traffic signal system errors.
 */
public class TrafficSignalException extends RuntimeException {
    
    public TrafficSignalException(String message) {
        super(message);
    }

    public TrafficSignalException(String message, Throwable cause) {
        super(message, cause);
    }
}



