package splitwise.repositories;

import splitwise.models.Expense;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Expense persistence operations.
 */
public interface ExpenseRepository {
    
    /**
     * Save an expense (create or update).
     */
    Expense save(Expense expense);
    
    /**
     * Find an expense by ID.
     */
    Optional<Expense> findById(String expenseId);
    
    /**
     * Get all expenses.
     */
    List<Expense> findAll();
    
    /**
     * Find all expenses in a group.
     */
    List<Expense> findByGroupId(String groupId);
    
    /**
     * Find all expenses paid by a user.
     */
    List<Expense> findByPayerId(String userId);
    
    /**
     * Find all expenses where a user is a participant.
     */
    List<Expense> findByParticipantId(String userId);
    
    /**
     * Delete an expense by ID.
     */
    void deleteById(String expenseId);
    
    /**
     * Check if an expense exists by ID.
     */
    boolean existsById(String expenseId);
}



