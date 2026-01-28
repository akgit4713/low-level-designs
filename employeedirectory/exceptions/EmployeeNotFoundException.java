package employeedirectory.exceptions;

/**
 * Exception thrown when an employee is not found.
 */
public class EmployeeNotFoundException extends EmployeeDirectoryException {
    
    public EmployeeNotFoundException(String employeeId) {
        super("Employee not found with ID: " + employeeId);
    }
}
