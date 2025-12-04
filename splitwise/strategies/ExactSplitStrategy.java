package splitwise.strategies;

import splitwise.exceptions.InvalidSplitException;
import splitwise.models.Split;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Strategy for splitting expenses by exact amounts.
 * Exact amounts must sum to the total expense.
 */
public class ExactSplitStrategy implements SplitStrategy {
    
    private static final BigDecimal TOLERANCE = new BigDecimal("0.01");
    
    @Override
    public List<Split> calculateSplits(
            BigDecimal totalAmount,
            List<String> participantIds,
            Map<String, BigDecimal> splitDetails) {
        
        validate(totalAmount, participantIds, splitDetails);
        
        List<Split> splits = new ArrayList<>();
        
        for (String participantId : participantIds) {
            BigDecimal exactAmount = splitDetails.get(participantId);
            splits.add(new Split(participantId, exactAmount));
        }
        
        return splits;
    }
    
    @Override
    public boolean validate(
            BigDecimal totalAmount,
            List<String> participantIds,
            Map<String, BigDecimal> splitDetails) {
        
        if (totalAmount == null || totalAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidSplitException("Total amount must be positive");
        }
        
        if (participantIds == null || participantIds.isEmpty()) {
            throw new InvalidSplitException("At least one participant is required");
        }
        
        if (splitDetails == null || splitDetails.isEmpty()) {
            throw new InvalidSplitException("Exact amounts are required");
        }
        
        // Verify all participants have exact amounts
        BigDecimal totalExact = BigDecimal.ZERO;
        for (String participantId : participantIds) {
            if (!splitDetails.containsKey(participantId)) {
                throw new InvalidSplitException(
                        "Exact amount not specified for participant: " + participantId
                );
            }
            BigDecimal amount = splitDetails.get(participantId);
            if (amount == null || amount.compareTo(BigDecimal.ZERO) < 0) {
                throw new InvalidSplitException(
                        "Amount must be non-negative for participant: " + participantId
                );
            }
            totalExact = totalExact.add(amount);
        }
        
        // Verify exact amounts sum to total
        if (totalExact.subtract(totalAmount).abs().compareTo(TOLERANCE) > 0) {
            throw new InvalidSplitException(
                    "Exact amounts must sum to total. Expected: " + totalAmount + 
                    ", Got: " + totalExact
            );
        }
        
        return true;
    }
}



