package splitwise.enums;

/**
 * Represents the current status of an expense.
 */
public enum ExpenseStatus {
    /**
     * Expense is active and affects balances
     */
    ACTIVE,
    
    /**
     * Expense has been fully settled
     */
    SETTLED,
    
    /**
     * Expense has been deleted/cancelled
     */
    DELETED
}



