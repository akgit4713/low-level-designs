package librarymanagement.strategies.borrowing;

import librarymanagement.models.Member;
import librarymanagement.models.BookCopy;

/**
 * Rule that checks if the book copy is available for borrowing.
 */
public class BookAvailabilityRule implements BorrowingRule {
    
    @Override
    public ValidationResult validate(Member member, BookCopy bookCopy, int currentBorrowCount) {
        if (!bookCopy.isAvailable()) {
            return ValidationResult.failure(
                    "Book is not available. Current status: " + bookCopy.getStatus(),
                    getRuleName());
        }
        
        return ValidationResult.success();
    }

    @Override
    public String getRuleName() {
        return "BookAvailabilityRule";
    }
}



