package splitwise.services;

import splitwise.enums.SplitMethod;
import splitwise.models.Expense;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Service interface for expense management operations.
 */
public interface ExpenseService {
    
    /**
     * Add an expense to a group.
     */
    Expense addExpense(
            String groupId,
            String payerId,
            BigDecimal amount,
            String description,
            List<String> participantIds,
            SplitMethod splitMethod,
            Map<String, BigDecimal> splitDetails
    );
    
    /**
     * Get expense by ID.
     */
    Expense getExpense(String expenseId);
    
    /**
     * Get all expenses in a group.
     */
    List<Expense> getGroupExpenses(String groupId);
    
    /**
     * Get all expenses involving a user.
     */
    List<Expense> getUserExpenses(String userId);
    
    /**
     * Get expenses paid by a user.
     */
    List<Expense> getExpensesPaidByUser(String userId);
    
    /**
     * Delete an expense.
     */
    void deleteExpense(String expenseId);
}



