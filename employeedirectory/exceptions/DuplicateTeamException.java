package employeedirectory.exceptions;

/**
 * Exception thrown when trying to add a team with duplicate ID.
 */
public class DuplicateTeamException extends EmployeeDirectoryException {
    
    public DuplicateTeamException(String teamId) {
        super("Team already exists with ID: " + teamId);
    }
}
