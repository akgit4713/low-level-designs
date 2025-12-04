package librarymanagement.strategies.fine;

import librarymanagement.models.BorrowRecord;
import java.math.BigDecimal;

/**
 * Strategy interface for calculating fines on overdue books.
 * Follows Open/Closed Principle - new fine calculation methods can be added
 * without modifying existing code.
 */
public interface FineCalculationStrategy {
    
    /**
     * Calculates the fine amount for an overdue book.
     * 
     * @param record The borrow record to calculate fine for
     * @return The fine amount, or ZERO if not overdue
     */
    BigDecimal calculateFine(BorrowRecord record);
    
    /**
     * Returns a description of this fine calculation strategy.
     */
    String getDescription();
}



