package librarymanagement.exceptions;

/**
 * Thrown when a member is not eligible to borrow books.
 */
public class MemberNotEligibleException extends BorrowingException {
    
    public MemberNotEligibleException(String memberId, String reason) {
        super("Member " + memberId + " is not eligible to borrow: " + reason);
    }
}



