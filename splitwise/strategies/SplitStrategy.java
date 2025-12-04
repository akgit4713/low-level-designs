package splitwise.strategies;

import splitwise.models.Split;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Strategy interface for splitting expenses.
 * Implementations define how an expense is divided among participants.
 */
public interface SplitStrategy {
    
    /**
     * Calculate splits for an expense.
     * 
     * @param totalAmount The total expense amount
     * @param participantIds List of participant user IDs
     * @param splitDetails Additional details for the split (percentages or exact amounts)
     * @return List of Split objects representing each participant's share
     */
    List<Split> calculateSplits(
            BigDecimal totalAmount,
            List<String> participantIds,
            Map<String, BigDecimal> splitDetails
    );
    
    /**
     * Validate the split details before calculation.
     * 
     * @param totalAmount The total expense amount
     * @param participantIds List of participant user IDs
     * @param splitDetails Additional details for the split
     * @return true if valid, throws exception otherwise
     */
    boolean validate(
            BigDecimal totalAmount,
            List<String> participantIds,
            Map<String, BigDecimal> splitDetails
    );
}



