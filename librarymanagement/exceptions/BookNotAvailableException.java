package librarymanagement.exceptions;

/**
 * Thrown when a book is not available for borrowing.
 */
public class BookNotAvailableException extends BorrowingException {
    
    public BookNotAvailableException(String bookCopyId) {
        super("Book copy is not available for borrowing: " + bookCopyId);
    }

    public BookNotAvailableException(String bookCopyId, String reason) {
        super("Book copy " + bookCopyId + " is not available: " + reason);
    }
}



