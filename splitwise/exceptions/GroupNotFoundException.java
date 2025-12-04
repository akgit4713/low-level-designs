package splitwise.exceptions;

/**
 * Exception thrown when a requested group is not found.
 */
public class GroupNotFoundException extends SplitwiseException {
    
    public GroupNotFoundException(String groupId) {
        super("Group not found with ID: " + groupId);
    }
}



