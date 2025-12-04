package splitwise.repositories.impl;

import splitwise.models.Expense;
import splitwise.repositories.ExpenseRepository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * In-memory implementation of ExpenseRepository.
 * Thread-safe using ConcurrentHashMap.
 */
public class InMemoryExpenseRepository implements ExpenseRepository {
    
    private final Map<String, Expense> expenses = new ConcurrentHashMap<>();
    
    @Override
    public Expense save(Expense expense) {
        expenses.put(expense.getId(), expense);
        return expense;
    }
    
    @Override
    public Optional<Expense> findById(String expenseId) {
        return Optional.ofNullable(expenses.get(expenseId));
    }
    
    @Override
    public List<Expense> findAll() {
        return new ArrayList<>(expenses.values());
    }
    
    @Override
    public List<Expense> findByGroupId(String groupId) {
        return expenses.values().stream()
                .filter(expense -> expense.getGroupId().equals(groupId))
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Expense> findByPayerId(String userId) {
        return expenses.values().stream()
                .filter(expense -> expense.getPayerId().equals(userId))
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Expense> findByParticipantId(String userId) {
        return expenses.values().stream()
                .filter(expense -> expense.getParticipantIds().contains(userId))
                .collect(Collectors.toList());
    }
    
    @Override
    public void deleteById(String expenseId) {
        expenses.remove(expenseId);
    }
    
    @Override
    public boolean existsById(String expenseId) {
        return expenses.containsKey(expenseId);
    }
}



