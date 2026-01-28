package employeedirectory.exceptions;

/**
 * Exception thrown when trying to add an employee with duplicate ID.
 */
public class DuplicateEmployeeException extends EmployeeDirectoryException {
    
    public DuplicateEmployeeException(String employeeId) {
        super("Employee already exists with ID: " + employeeId);
    }
}
