package librarymanagement.exceptions;

/**
 * Thrown when a requested member is not found.
 */
public class MemberNotFoundException extends LibraryException {
    
    public MemberNotFoundException(String message) {
        super(message);
    }

    public MemberNotFoundException(String memberId, boolean byId) {
        super("Member not found with ID: " + memberId);
    }
}



