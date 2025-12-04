package librarymanagement.exceptions;

/**
 * Thrown when a borrowing operation fails.
 */
public class BorrowingException extends LibraryException {
    
    public BorrowingException(String message) {
        super(message);
    }

    public BorrowingException(String message, Throwable cause) {
        super(message, cause);
    }
}



