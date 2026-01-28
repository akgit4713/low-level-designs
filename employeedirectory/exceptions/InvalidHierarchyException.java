package employeedirectory.exceptions;

/**
 * Exception thrown when an invalid hierarchy operation is attempted.
 */
public class InvalidHierarchyException extends EmployeeDirectoryException {
    
    public InvalidHierarchyException(String message) {
        super(message);
    }
}
