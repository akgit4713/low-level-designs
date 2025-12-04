package splitwise;

import splitwise.enums.SplitMethod;
import splitwise.factories.SplitStrategyFactory;
import splitwise.models.*;
import splitwise.observers.ConsoleNotificationObserver;
import splitwise.observers.ExpenseObserver;
import splitwise.observers.SettlementObserver;
import splitwise.repositories.*;
import splitwise.repositories.impl.*;
import splitwise.services.*;
import splitwise.services.impl.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Facade class that provides a unified interface to the Splitwise system.
 * Coordinates all services and hides internal complexity.
 */
public class Splitwise {
    
    private final UserService userService;
    private final GroupService groupService;
    private final ExpenseService expenseService;
    private final BalanceService balanceService;
    private final TransactionService transactionService;
    
    // For accessing impl-specific methods like addObserver
    private final ExpenseServiceImpl expenseServiceImpl;
    private final TransactionServiceImpl transactionServiceImpl;
    
    /**
     * Create a new Splitwise instance with default in-memory repositories.
     */
    public Splitwise() {
        // Initialize repositories
        UserRepository userRepository = new InMemoryUserRepository();
        GroupRepository groupRepository = new InMemoryGroupRepository();
        ExpenseRepository expenseRepository = new InMemoryExpenseRepository();
        TransactionRepository transactionRepository = new InMemoryTransactionRepository();
        
        // Initialize services
        this.userService = new UserServiceImpl(userRepository);
        this.groupService = new GroupServiceImpl(groupRepository, userService);
        this.balanceService = new BalanceServiceImpl();
        
        SplitStrategyFactory splitStrategyFactory = new SplitStrategyFactory();
        this.expenseServiceImpl = new ExpenseServiceImpl(
                expenseRepository,
                transactionRepository,
                groupService,
                balanceService,
                splitStrategyFactory
        );
        this.expenseService = expenseServiceImpl;
        
        this.transactionServiceImpl = new TransactionServiceImpl(
                transactionRepository,
                balanceService,
                userService
        );
        this.transactionService = transactionServiceImpl;
    }
    
    /**
     * Create a Splitwise instance with custom repositories (for testing/different storage).
     */
    public Splitwise(
            UserRepository userRepository,
            GroupRepository groupRepository,
            ExpenseRepository expenseRepository,
            TransactionRepository transactionRepository) {
        
        this.userService = new UserServiceImpl(userRepository);
        this.groupService = new GroupServiceImpl(groupRepository, userService);
        this.balanceService = new BalanceServiceImpl();
        
        SplitStrategyFactory splitStrategyFactory = new SplitStrategyFactory();
        this.expenseServiceImpl = new ExpenseServiceImpl(
                expenseRepository,
                transactionRepository,
                groupService,
                balanceService,
                splitStrategyFactory
        );
        this.expenseService = expenseServiceImpl;
        
        this.transactionServiceImpl = new TransactionServiceImpl(
                transactionRepository,
                balanceService,
                userService
        );
        this.transactionService = transactionServiceImpl;
    }
    
    // ================== User Operations ==================
    
    /**
     * Register a new user.
     */
    public User registerUser(String name, String email, String phone) {
        return userService.registerUser(name, email, phone);
    }
    
    /**
     * Get a user by ID.
     */
    public User getUser(String userId) {
        return userService.getUser(userId);
    }
    
    /**
     * Get all users.
     */
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }
    
    // ================== Group Operations ==================
    
    /**
     * Create a new group.
     */
    public Group createGroup(String name, String creatorId) {
        return groupService.createGroup(name, creatorId);
    }
    
    /**
     * Get a group by ID.
     */
    public Group getGroup(String groupId) {
        return groupService.getGroup(groupId);
    }
    
    /**
     * Add a user to a group.
     */
    public void addUserToGroup(String groupId, String userId) {
        groupService.addUserToGroup(groupId, userId);
    }
    
    /**
     * Remove a user from a group.
     */
    public void removeUserFromGroup(String groupId, String userId) {
        groupService.removeUserFromGroup(groupId, userId);
    }
    
    /**
     * Get all groups a user belongs to.
     */
    public List<Group> getUserGroups(String userId) {
        return groupService.getUserGroups(userId);
    }
    
    // ================== Expense Operations ==================
    
    /**
     * Add an expense with equal split.
     */
    public Expense addExpense(
            String groupId,
            String payerId,
            BigDecimal amount,
            String description,
            List<String> participantIds) {
        return expenseService.addExpense(
                groupId, payerId, amount, description, 
                participantIds, SplitMethod.EQUAL, null
        );
    }
    
    /**
     * Add an expense with custom split method.
     */
    public Expense addExpense(
            String groupId,
            String payerId,
            BigDecimal amount,
            String description,
            List<String> participantIds,
            SplitMethod splitMethod,
            Map<String, BigDecimal> splitDetails) {
        return expenseService.addExpense(
                groupId, payerId, amount, description,
                participantIds, splitMethod, splitDetails
        );
    }
    
    /**
     * Get all expenses in a group.
     */
    public List<Expense> getGroupExpenses(String groupId) {
        return expenseService.getGroupExpenses(groupId);
    }
    
    /**
     * Get all expenses involving a user.
     */
    public List<Expense> getUserExpenses(String userId) {
        return expenseService.getUserExpenses(userId);
    }
    
    /**
     * Delete an expense.
     */
    public void deleteExpense(String expenseId) {
        expenseService.deleteExpense(expenseId);
    }
    
    // ================== Balance Operations ==================
    
    /**
     * Get all balances for a user.
     * Returns map of userId -> amount (positive means user owes them).
     */
    public Map<String, BigDecimal> getUserBalances(String userId) {
        return balanceService.getUserBalances(userId);
    }
    
    /**
     * Get the balance between two users.
     */
    public BigDecimal getBalanceBetween(String userId1, String userId2) {
        return balanceService.getBalanceBetween(userId1, userId2);
    }
    
    /**
     * Get total amount user owes to others.
     */
    public BigDecimal getTotalOwed(String userId) {
        return balanceService.getTotalOwed(userId);
    }
    
    /**
     * Get total amount others owe to user.
     */
    public BigDecimal getTotalOwedToUser(String userId) {
        return balanceService.getTotalOwedToUser(userId);
    }
    
    // ================== Settlement Operations ==================
    
    /**
     * Settle balance between two users.
     */
    public Transaction settleBalance(String fromUserId, String toUserId, BigDecimal amount) {
        return transactionService.settleBalance(fromUserId, toUserId, amount);
    }
    
    /**
     * Get all transactions for a user.
     */
    public List<Transaction> getUserTransactions(String userId) {
        return transactionService.getUserTransactions(userId);
    }
    
    /**
     * Get transaction history between two users.
     */
    public List<Transaction> getTransactionsBetween(String userId1, String userId2) {
        return transactionService.getTransactionsBetween(userId1, userId2);
    }
    
    // ================== Observer Registration ==================
    
    /**
     * Add an expense observer.
     */
    public void addExpenseObserver(ExpenseObserver observer) {
        expenseServiceImpl.addObserver(observer);
    }
    
    /**
     * Add a settlement observer.
     */
    public void addSettlementObserver(SettlementObserver observer) {
        transactionServiceImpl.addObserver(observer);
    }
    
    /**
     * Enable console notifications for all events.
     */
    public void enableConsoleNotifications() {
        ConsoleNotificationObserver observer = new ConsoleNotificationObserver();
        addExpenseObserver(observer);
        addSettlementObserver(observer);
    }
    
    // ================== Service Access (for advanced use) ==================
    
    public UserService getUserService() {
        return userService;
    }
    
    public GroupService getGroupService() {
        return groupService;
    }
    
    public ExpenseService getExpenseService() {
        return expenseService;
    }
    
    public BalanceService getBalanceService() {
        return balanceService;
    }
    
    public TransactionService getTransactionService() {
        return transactionService;
    }
}



