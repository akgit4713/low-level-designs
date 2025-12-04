package splitwise.services.impl;

import splitwise.enums.SplitMethod;
import splitwise.enums.TransactionType;
import splitwise.exceptions.ExpenseNotFoundException;
import splitwise.exceptions.SplitwiseException;
import splitwise.factories.SplitStrategyFactory;
import splitwise.models.Expense;
import splitwise.models.Split;
import splitwise.models.Transaction;
import splitwise.observers.ExpenseObserver;
import splitwise.repositories.ExpenseRepository;
import splitwise.repositories.TransactionRepository;
import splitwise.services.BalanceService;
import splitwise.services.ExpenseService;
import splitwise.services.GroupService;
import splitwise.strategies.SplitStrategy;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Implementation of ExpenseService.
 */
public class ExpenseServiceImpl implements ExpenseService {
    
    private final ExpenseRepository expenseRepository;
    private final TransactionRepository transactionRepository;
    private final GroupService groupService;
    private final BalanceService balanceService;
    private final SplitStrategyFactory splitStrategyFactory;
    private final List<ExpenseObserver> observers;
    
    public ExpenseServiceImpl(
            ExpenseRepository expenseRepository,
            TransactionRepository transactionRepository,
            GroupService groupService,
            BalanceService balanceService,
            SplitStrategyFactory splitStrategyFactory) {
        this.expenseRepository = expenseRepository;
        this.transactionRepository = transactionRepository;
        this.groupService = groupService;
        this.balanceService = balanceService;
        this.splitStrategyFactory = splitStrategyFactory;
        this.observers = new ArrayList<>();
    }
    
    /**
     * Add an observer for expense events.
     */
    public void addObserver(ExpenseObserver observer) {
        observers.add(observer);
    }
    
    /**
     * Remove an observer.
     */
    public void removeObserver(ExpenseObserver observer) {
        observers.remove(observer);
    }
    
    @Override
    public Expense addExpense(
            String groupId,
            String payerId,
            BigDecimal amount,
            String description,
            List<String> participantIds,
            SplitMethod splitMethod,
            Map<String, BigDecimal> splitDetails) {
        
        // Validate group exists and payer is a member
        if (!groupService.isMember(groupId, payerId)) {
            throw new SplitwiseException("Payer is not a member of the group");
        }
        
        // Validate all participants are members
        for (String participantId : participantIds) {
            if (!groupService.isMember(groupId, participantId)) {
                throw new SplitwiseException(
                        "Participant is not a member of the group: " + participantId
                );
            }
        }
        
        // Get the split strategy and calculate splits
        SplitStrategy strategy = splitStrategyFactory.getStrategy(splitMethod);
        List<Split> splits = strategy.calculateSplits(amount, participantIds, splitDetails);
        
        // Create the expense
        Expense expense = new Expense.Builder()
                .groupId(groupId)
                .payerId(payerId)
                .amount(amount)
                .description(description)
                .splitMethod(splitMethod)
                .splits(splits)
                .build();
        
        // Save expense
        expense = expenseRepository.save(expense);
        
        // Update balances and create transactions
        for (Split split : splits) {
            String participantId = split.getUserId();
            BigDecimal splitAmount = split.getAmount();
            
            if (!participantId.equals(payerId)) {
                // Participant owes the payer
                balanceService.updateBalance(participantId, payerId, splitAmount);
                
                // Create transaction record
                Transaction tx = new Transaction.Builder()
                        .fromUserId(participantId)
                        .toUserId(payerId)
                        .amount(splitAmount)
                        .type(TransactionType.EXPENSE)
                        .referenceId(expense.getId())
                        .description("Share of: " + description)
                        .build();
                transactionRepository.save(tx);
            }
        }
        
        // Notify observers
        for (ExpenseObserver observer : observers) {
            observer.onExpenseAdded(expense);
        }
        
        return expense;
    }
    
    @Override
    public Expense getExpense(String expenseId) {
        return expenseRepository.findById(expenseId)
                .orElseThrow(() -> new ExpenseNotFoundException(expenseId));
    }
    
    @Override
    public List<Expense> getGroupExpenses(String groupId) {
        // Validate group exists
        groupService.getGroup(groupId);
        return expenseRepository.findByGroupId(groupId);
    }
    
    @Override
    public List<Expense> getUserExpenses(String userId) {
        return expenseRepository.findByParticipantId(userId);
    }
    
    @Override
    public List<Expense> getExpensesPaidByUser(String userId) {
        return expenseRepository.findByPayerId(userId);
    }
    
    @Override
    public void deleteExpense(String expenseId) {
        Expense expense = getExpense(expenseId);
        
        // Reverse the balances
        for (Split split : expense.getSplits()) {
            String participantId = split.getUserId();
            BigDecimal splitAmount = split.getAmount();
            
            if (!participantId.equals(expense.getPayerId())) {
                // Reverse: participant no longer owes the payer
                balanceService.updateBalance(participantId, expense.getPayerId(), 
                        splitAmount.negate());
            }
        }
        
        expense.delete();
        expenseRepository.save(expense);
        
        // Notify observers
        for (ExpenseObserver observer : observers) {
            observer.onExpenseDeleted(expense);
        }
    }
}



