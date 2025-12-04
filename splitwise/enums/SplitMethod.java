package splitwise.enums;

/**
 * Defines the different methods available for splitting an expense.
 */
public enum SplitMethod {
    /**
     * Split equally among all participants
     */
    EQUAL,
    
    /**
     * Split by percentage (must sum to 100%)
     */
    PERCENTAGE,
    
    /**
     * Split by exact amounts (must sum to total)
     */
    EXACT
}



