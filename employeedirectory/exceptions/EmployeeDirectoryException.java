package employeedirectory.exceptions;

/**
 * Base exception for all Employee Directory related exceptions.
 */
public class EmployeeDirectoryException extends RuntimeException {
    
    public EmployeeDirectoryException(String message) {
        super(message);
    }

    public EmployeeDirectoryException(String message, Throwable cause) {
        super(message, cause);
    }
}
