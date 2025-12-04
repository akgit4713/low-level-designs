# Splitwise - Low Level Design

## Overview

Splitwise is an expense-sharing application that helps users split bills and track balances with friends. This LLD covers user management, group expenses, multiple split strategies, balance tracking, and settlement.

---

## 1. Responsibility Breakdown

| Component | Responsibility |
|-----------|----------------|
| **UserService** | User registration, profile management, authentication |
| **GroupService** | Create groups, add/remove members, group metadata |
| **ExpenseService** | Add expenses, split calculation, expense history |
| **BalanceService** | Track who owes whom, simplify debts, get net balances |
| **TransactionService** | Record settlements, payment history |
| **SplitStrategy** | Calculate individual shares (Equal/Percentage/Exact) |
| **Repositories** | Data persistence abstraction |
| **Observers** | Notifications on expense/settlement events |

---

## 2. Key Abstractions

### 2.1 Core Models

```
┌─────────────────────────────────────────────────────────────────────┐
│                           MODELS                                     │
├─────────────────────────────────────────────────────────────────────┤
│                                                                      │
│  ┌─────────┐      ┌─────────┐      ┌─────────────┐                 │
│  │  User   │──────│  Group  │──────│   Expense   │                 │
│  └─────────┘      └─────────┘      └─────────────┘                 │
│       │                                    │                         │
│       │                                    ▼                         │
│       │                            ┌─────────────┐                  │
│       └────────────────────────────│    Split    │                  │
│                                    └─────────────┘                  │
│                                           │                          │
│       ┌─────────────┐             ┌──────┴──────┐                   │
│       │   Balance   │◄────────────│ Transaction │                   │
│       └─────────────┘             └─────────────┘                   │
│                                                                      │
└─────────────────────────────────────────────────────────────────────┘
```

| Class | Purpose |
|-------|---------|
| `User` | Represents a user with id, name, email, phone |
| `Group` | Collection of users who share expenses together |
| `Expense` | A shared expense with payer, amount, participants, splits |
| `Split` | Individual share of an expense for a user |
| `Balance` | Net amount owed between two users |
| `Transaction` | Settlement payment from one user to another |

### 2.2 Enums

| Enum | Values |
|------|--------|
| `SplitMethod` | EQUAL, PERCENTAGE, EXACT |
| `ExpenseStatus` | ACTIVE, SETTLED, DELETED |
| `TransactionType` | EXPENSE, SETTLEMENT |

### 2.3 Strategies

| Interface | Implementations | Purpose |
|-----------|-----------------|---------|
| `SplitStrategy` | `EqualSplitStrategy`, `PercentageSplitStrategy`, `ExactSplitStrategy` | Calculate how an expense is divided |

### 2.4 Repositories

| Interface | Purpose |
|-----------|---------|
| `UserRepository` | CRUD operations for users |
| `GroupRepository` | CRUD operations for groups |
| `ExpenseRepository` | CRUD operations for expenses |
| `TransactionRepository` | CRUD operations for transactions |

### 2.5 Services

| Interface | Purpose |
|-----------|---------|
| `UserService` | Business logic for user management |
| `GroupService` | Business logic for group operations |
| `ExpenseService` | Business logic for expense management |
| `BalanceService` | Calculate and track balances |
| `TransactionService` | Handle settlements |

---

## 3. Relationships & SOLID Principles

### 3.1 Class Diagram

```
┌──────────────────────────────────────────────────────────────────────────────┐
│                              ARCHITECTURE                                     │
├──────────────────────────────────────────────────────────────────────────────┤
│                                                                               │
│    ┌─────────────────────────────────────────────────────────────┐           │
│    │                        FACADE                                │           │
│    │  ┌─────────────────────────────────────────────────────────┐│           │
│    │  │                    Splitwise                             ││           │
│    │  │  (Coordinates all services, single entry point)         ││           │
│    │  └─────────────────────────────────────────────────────────┘│           │
│    └─────────────────────────────────────────────────────────────┘           │
│                                    │                                          │
│                                    ▼                                          │
│    ┌─────────────────────────────────────────────────────────────┐           │
│    │                        SERVICES                              │           │
│    │  ┌───────────┐ ┌───────────┐ ┌───────────┐ ┌───────────┐   │           │
│    │  │UserService│ │GroupServ. │ │ExpenseServ│ │BalanceServ│   │           │
│    │  └───────────┘ └───────────┘ └───────────┘ └───────────┘   │           │
│    │                       ┌───────────┐                         │           │
│    │                       │TransServ. │                         │           │
│    │                       └───────────┘                         │           │
│    └─────────────────────────────────────────────────────────────┘           │
│                                    │                                          │
│         ┌──────────────────────────┼──────────────────────────┐              │
│         ▼                          ▼                          ▼              │
│    ┌──────────┐            ┌──────────────┐           ┌────────────┐        │
│    │STRATEGIES│            │ REPOSITORIES │           │ OBSERVERS  │        │
│    │          │            │              │           │            │        │
│    │ Equal    │            │ UserRepo     │           │ Expense    │        │
│    │ Percent  │            │ GroupRepo    │           │ Settlement │        │
│    │ Exact    │            │ ExpenseRepo  │           │ Balance    │        │
│    │          │            │ TransRepo    │           │            │        │
│    └──────────┘            └──────────────┘           └────────────┘        │
│                                                                               │
└──────────────────────────────────────────────────────────────────────────────┘
```

### 3.2 SOLID Principles Applied

| Principle | Application |
|-----------|-------------|
| **SRP** | Each service handles one domain (Users, Groups, Expenses, etc.) |
| **OCP** | New split methods added by implementing `SplitStrategy` without modifying existing code |
| **LSP** | All `SplitStrategy` implementations are interchangeable |
| **ISP** | Separate interfaces for repositories and services; clients depend only on what they need |
| **DIP** | Services depend on repository interfaces, not implementations; Strategies injected via constructor |

---

## 4. Design Patterns

| Pattern | Where Used | Why |
|---------|------------|-----|
| **Strategy** | `SplitStrategy` | Different split algorithms (equal, percentage, exact) interchangeable at runtime |
| **Factory** | `SplitStrategyFactory` | Create appropriate strategy based on `SplitMethod` enum |
| **Repository** | All data access | Abstract persistence, easy to swap in-memory for DB |
| **Facade** | `Splitwise` class | Single entry point hiding complexity of multiple services |
| **Observer** | Expense/Settlement notifications | Decouple event generation from handling (email, push, etc.) |
| **Builder** | `Expense.Builder` | Complex object construction with many optional fields |

---

## 5. Extension Points

### 5.1 Adding New Split Methods

```java
// 1. Add enum value
public enum SplitMethod {
    EQUAL, PERCENTAGE, EXACT, SHARE_BASED  // NEW
}

// 2. Implement strategy
public class ShareBasedSplitStrategy implements SplitStrategy {
    @Override
    public List<Split> split(Expense expense, Map<String, BigDecimal> shares) {
        // Implementation
    }
}

// 3. Register in factory
factory.register(SplitMethod.SHARE_BASED, new ShareBasedSplitStrategy());
```

### 5.2 Adding New Notification Channels

```java
// Implement ExpenseObserver
public class PushNotificationObserver implements ExpenseObserver {
    @Override
    public void onExpenseAdded(Expense expense) {
        // Send push notification
    }
}
```

### 5.3 Switching to Database Persistence

```java
// Implement repository interface with JPA/JDBC
public class JpaUserRepository implements UserRepository {
    @Override
    public User save(User user) { /* JPA implementation */ }
}

// Inject into services
UserService userService = new UserServiceImpl(new JpaUserRepository());
```

---

## 6. Sequence Diagrams

### 6.1 Add Expense Flow

```
User          Splitwise        ExpenseService      SplitStrategy       BalanceService
  │               │                  │                   │                   │
  │──addExpense──▶│                  │                   │                   │
  │               │──addExpense─────▶│                   │                   │
  │               │                  │──split(expense)──▶│                   │
  │               │                  │◀──List<Split>─────│                   │
  │               │                  │──updateBalances─────────────────────▶│
  │               │                  │◀─────────────────────────────────────│
  │               │◀──Expense────────│                   │                   │
  │◀──Expense─────│                  │                   │                   │
```

### 6.2 Settle Balance Flow

```
User          Splitwise      TransactionService    BalanceService
  │               │                  │                   │
  │──settleUp────▶│                  │                   │
  │               │──settle─────────▶│                   │
  │               │                  │──updateBalance───▶│
  │               │                  │◀──────────────────│
  │               │◀──Transaction────│                   │
  │◀──Transaction─│                  │                   │
```

---

## 7. Concurrency Handling

```java
public class BalanceServiceImpl implements BalanceService {
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    
    @Override
    public void updateBalance(String userId1, String userId2, BigDecimal amount) {
        lock.writeLock().lock();
        try {
            // Atomic balance update
        } finally {
            lock.writeLock().unlock();
        }
    }
    
    @Override
    public Balance getBalance(String userId1, String userId2) {
        lock.readLock().lock();
        try {
            // Read balance
        } finally {
            lock.readLock().unlock();
        }
    }
}
```

---

## 8. Package Structure

```
splitwise/
├── enums/
│   ├── SplitMethod.java
│   ├── ExpenseStatus.java
│   └── TransactionType.java
├── exceptions/
│   ├── SplitwiseException.java
│   ├── UserNotFoundException.java
│   ├── GroupNotFoundException.java
│   ├── ExpenseNotFoundException.java
│   └── InvalidSplitException.java
├── models/
│   ├── User.java
│   ├── Group.java
│   ├── Expense.java
│   ├── Split.java
│   ├── Balance.java
│   └── Transaction.java
├── strategies/
│   ├── SplitStrategy.java
│   ├── EqualSplitStrategy.java
│   ├── PercentageSplitStrategy.java
│   └── ExactSplitStrategy.java
├── factories/
│   └── SplitStrategyFactory.java
├── repositories/
│   ├── UserRepository.java
│   ├── GroupRepository.java
│   ├── ExpenseRepository.java
│   ├── TransactionRepository.java
│   └── impl/
│       ├── InMemoryUserRepository.java
│       ├── InMemoryGroupRepository.java
│       ├── InMemoryExpenseRepository.java
│       └── InMemoryTransactionRepository.java
├── services/
│   ├── UserService.java
│   ├── GroupService.java
│   ├── ExpenseService.java
│   ├── BalanceService.java
│   ├── TransactionService.java
│   └── impl/
│       ├── UserServiceImpl.java
│       ├── GroupServiceImpl.java
│       ├── ExpenseServiceImpl.java
│       ├── BalanceServiceImpl.java
│       └── TransactionServiceImpl.java
├── observers/
│   ├── ExpenseObserver.java
│   ├── SettlementObserver.java
│   └── ConsoleNotificationObserver.java
├── Splitwise.java
└── Main.java
```

---

## 9. API Summary

### Splitwise (Facade)

```java
// User operations
User registerUser(String name, String email, String phone);
User getUser(String userId);

// Group operations
Group createGroup(String name, String creatorId);
void addUserToGroup(String groupId, String userId);
void removeUserFromGroup(String groupId, String userId);

// Expense operations
Expense addExpense(String groupId, String payerId, BigDecimal amount, 
                   String description, List<String> participantIds, 
                   SplitMethod method, Map<String, BigDecimal> splitDetails);
List<Expense> getGroupExpenses(String groupId);
List<Expense> getUserExpenses(String userId);

// Balance operations
Map<String, BigDecimal> getUserBalances(String userId);
BigDecimal getBalanceBetween(String userId1, String userId2);

// Settlement operations
Transaction settleBalance(String fromUserId, String toUserId, BigDecimal amount);
List<Transaction> getUserTransactions(String userId);
```

---

## 10. Design Rationale

1. **Extensibility**: Strategy pattern for split methods allows adding new algorithms without changing core logic
2. **Loose Coupling**: All dependencies are injected via interfaces; services don't know about concrete implementations
3. **Testability**: Repository interfaces can be mocked; strategies are standalone and easily unit-tested
4. **Thread Safety**: Read-write locks in BalanceService ensure data consistency under concurrent access
5. **Clean Separation**: Facade hides internal complexity; each service has a single responsibility
6. **Event-Driven**: Observer pattern decouples notifications from business logic

---

## 11. Future Enhancements

- [ ] Multi-currency support with conversion strategies
- [ ] Recurring expenses
- [ ] Debt simplification algorithm (minimize transactions)
- [ ] Activity feed with pagination
- [ ] Friend requests and invitations
- [ ] Expense attachments (receipts)



