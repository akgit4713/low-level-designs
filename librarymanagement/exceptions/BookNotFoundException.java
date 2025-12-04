package librarymanagement.exceptions;

/**
 * Thrown when a requested book or book copy is not found.
 */
public class BookNotFoundException extends LibraryException {
    
    public BookNotFoundException(String message) {
        super(message);
    }

    public BookNotFoundException(String bookId) {
        super("Book not found with ID: " + bookId);
    }
}



