package employeedirectory.exceptions;

/**
 * Exception thrown when a team is not found.
 */
public class TeamNotFoundException extends EmployeeDirectoryException {
    
    public TeamNotFoundException(String teamId) {
        super("Team not found with ID: " + teamId);
    }
}
