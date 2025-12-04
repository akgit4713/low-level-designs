package librarymanagement.strategies.borrowing;

import librarymanagement.models.Member;
import librarymanagement.models.BookCopy;

/**
 * Rule that checks if member has reached maximum borrowing limit.
 */
public class MaxBooksRule implements BorrowingRule {
    
    @Override
    public ValidationResult validate(Member member, BookCopy bookCopy, int currentBorrowCount) {
        int maxAllowed = member.getMaxBooksAllowed();
        
        if (currentBorrowCount >= maxAllowed) {
            return ValidationResult.failure(
                    String.format("Maximum borrow limit reached. Current: %d, Max: %d", 
                            currentBorrowCount, maxAllowed),
                    getRuleName());
        }
        
        return ValidationResult.success();
    }

    @Override
    public String getRuleName() {
        return "MaxBooksRule";
    }
}



