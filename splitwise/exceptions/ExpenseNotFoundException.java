package splitwise.exceptions;

/**
 * Exception thrown when a requested expense is not found.
 */
public class ExpenseNotFoundException extends SplitwiseException {
    
    public ExpenseNotFoundException(String expenseId) {
        super("Expense not found with ID: " + expenseId);
    }
}



