package librarymanagement.exceptions;

/**
 * Thrown when a member tries to borrow more books than allowed.
 */
public class MaxBooksExceededException extends BorrowingException {
    
    private final int currentCount;
    private final int maxAllowed;

    public MaxBooksExceededException(int currentCount, int maxAllowed) {
        super(String.format("Cannot borrow more books. Current: %d, Maximum allowed: %d", 
                currentCount, maxAllowed));
        this.currentCount = currentCount;
        this.maxAllowed = maxAllowed;
    }

    public int getCurrentCount() {
        return currentCount;
    }

    public int getMaxAllowed() {
        return maxAllowed;
    }
}



