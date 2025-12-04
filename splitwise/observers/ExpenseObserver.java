package splitwise.observers;

import splitwise.models.Expense;

/**
 * Observer interface for expense events.
 * Implementations can react to expense additions and deletions.
 */
public interface ExpenseObserver {
    
    /**
     * Called when a new expense is added.
     */
    void onExpenseAdded(Expense expense);
    
    /**
     * Called when an expense is deleted.
     */
    void onExpenseDeleted(Expense expense);
}



