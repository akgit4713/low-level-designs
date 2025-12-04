package librarymanagement.strategies.borrowing;

import librarymanagement.models.Member;
import librarymanagement.models.BookCopy;

/**
 * Interface for borrowing rules that validate if a member can borrow a book.
 * Uses Chain of Responsibility pattern for composing multiple rules.
 */
public interface BorrowingRule {
    
    /**
     * Validates if the member can borrow the book.
     * 
     * @param member The member attempting to borrow
     * @param bookCopy The book copy to borrow
     * @param currentBorrowCount Current number of active borrows for the member
     * @return ValidationResult indicating success or failure with reason
     */
    ValidationResult validate(Member member, BookCopy bookCopy, int currentBorrowCount);
    
    /**
     * Returns the name of this rule for logging/debugging.
     */
    String getRuleName();
}



