# ATM System - Low-Level Design

## Overview

A comprehensive ATM (Automated Teller Machine) system that handles cash withdrawals, deposits, balance inquiries, and mini statements. The system follows SOLID principles and implements multiple design patterns for extensibility and maintainability.

---

## 1. Responsibility Breakdown

| Component | Responsibility |
|-----------|---------------|
| **ATM (Facade)** | Main entry point, orchestrates all operations and components |
| **ATMState** | Manages ATM state machine (State Pattern) |
| **CashDispenser** | Manages cash inventory and dispensing operations |
| **BankService** | Interface for bank backend integration |
| **AuthenticationService** | Handles card/PIN validation |
| **TransactionService** | Records and retrieves transactions |
| **TransactionStrategy** | Executes different transaction types (Strategy Pattern) |
| **TransactionObserver** | Notifications for completed transactions (Observer Pattern) |

---

## 2. Key Abstractions

### Enums

```
┌─────────────────────────────────────────────────────────────────┐
│  ATMStateType      - IDLE, CARD_INSERTED, AUTHENTICATED,       │
│                      TRANSACTION_SELECTED, PROCESSING,          │
│                      DISPENSING, OUT_OF_SERVICE                 │
│  TransactionType   - BALANCE_INQUIRY, WITHDRAWAL, DEPOSIT,     │
│                      MINI_STATEMENT, PIN_CHANGE                 │
│  TransactionStatus - PENDING, SUCCESS, FAILED, CANCELLED,      │
│                      INSUFFICIENT_FUNDS, LIMIT_EXCEEDED         │
│  AccountType       - SAVINGS, CHECKING, CURRENT                 │
│  Denomination      - NOTE_2000, NOTE_500, NOTE_200, NOTE_100   │
└─────────────────────────────────────────────────────────────────┘
```

### Core Models

| Model | Purpose |
|-------|---------|
| `Card` | Bank card with number, customer info, expiry, blocked status |
| `Account` | Bank account with balance, type, daily limits |
| `Transaction` | Transaction record with type, amount, status, timestamps |
| `CashInventory` | Denomination-wise cash storage in ATM |
| `Receipt` | Transaction receipt for printing |

### Strategy Interfaces

| Interface | Purpose | Implementations |
|-----------|---------|-----------------|
| `TransactionStrategy` | Execute different transactions | Withdrawal, Deposit, BalanceInquiry, MiniStatement |

### State Classes

| State | Purpose |
|-------|---------|
| `IdleState` | Initial state, waiting for card |
| `CardInsertedState` | Card inserted, waiting for PIN |
| `AuthenticatedState` | PIN verified, select transaction |
| `TransactionState` | Processing specific transaction |
| `ProcessingState` | Transaction in progress |
| `OutOfServiceState` | ATM unavailable |

### Observer Interfaces

| Interface | Purpose |
|-----------|---------|
| `TransactionObserver` | React to transaction events |
| `SMSNotificationObserver` | Send SMS for transactions |
| `AuditLogObserver` | Log transactions for audit |

---

## 3. Class Diagram

```
                          ┌──────────────────────┐
                          │        ATM           │
                          │      (Facade)        │
                          └──────────┬───────────┘
                                     │
       ┌─────────────┬───────────────┼───────────────┬─────────────┐
       │             │               │               │             │
       ▼             ▼               ▼               ▼             ▼
┌─────────────┐ ┌─────────────┐ ┌─────────────┐ ┌─────────────┐ ┌─────────────┐
│ ATMState    │ │CashDispenser│ │ BankService │ │   Auth      │ │ Transaction │
│ (abstract)  │ │             │ │ (interface) │ │  Service    │ │  Service    │
└──────┬──────┘ └──────┬──────┘ └──────┬──────┘ └─────────────┘ └─────────────┘
       │               │               │
       │               │               ▼
┌──────┴──────┐        │        ┌─────────────┐
│ Concrete    │        │        │ InMemory    │
│ States      │        │        │ BankService │
│ (Idle,Auth, │        │        └─────────────┘
│  etc.)      │        │
└─────────────┘        ▼
                ┌─────────────┐
                │Denomination │
                │  Handler    │
                │   (CoR)     │
                └─────────────┘


    ┌─────────────────────────────────────────────────────────┐
    │              Transaction Strategies                      │
    ├─────────────────────────────────────────────────────────┤
    │  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐     │
    │  │ Withdrawal  │  │  Deposit    │  │  Balance    │     │
    │  │ Strategy    │  │  Strategy   │  │  Strategy   │     │
    │  └─────────────┘  └─────────────┘  └─────────────┘     │
    └─────────────────────────────────────────────────────────┘
```

---

## 4. Design Patterns Used

### 1. **State Pattern** - ATM State Management

Controls ATM behavior based on current state. Transitions are managed within states.

```java
// State transitions
IdleState → CardInsertedState → AuthenticatedState → TransactionState → Processing
                ↓
         (on cancel/eject)
                ↓
            IdleState
```

### 2. **Strategy Pattern** - Transaction Processing

Different transaction types are handled by separate strategy implementations.

```java
public interface TransactionStrategy {
    Transaction execute(Account account, Card card, BigDecimal amount, ATM atm);
    TransactionType getTransactionType();
    boolean validate(Account account, BigDecimal amount);
}

// Adding a new transaction type:
atm.registerTransactionStrategy(TransactionType.FUND_TRANSFER, new FundTransferStrategy());
```

### 3. **Chain of Responsibility** - Cash Dispensing

Denominations are processed in chain from highest to lowest.

```
NOTE_2000 → NOTE_500 → NOTE_200 → NOTE_100
```

### 4. **Observer Pattern** - Transaction Notifications

Decoupled notifications for completed transactions.

```java
atm.addObserver(new SMSNotificationObserver());
atm.addObserver(new AuditLogObserver());
```

### 5. **Facade Pattern** - ATM Class

Provides simplified interface to the complex ATM subsystem.

```java
// Simple operations hiding internal complexity
atm.insertCard(card);
atm.enterPin("1234");
atm.quickWithdraw(new BigDecimal("5000"));
```

### 6. **Builder Pattern** - Transaction Object

Complex transaction objects with optional fields.

```java
Transaction transaction = Transaction.builder()
    .accountNumber("ACC001")
    .cardNumber("4111111111111111")
    .type(TransactionType.WITHDRAWAL)
    .amount(new BigDecimal("5000"))
    .atmId("ATM-001")
    .build();
```

---

## 5. SOLID Principles Applied

### Single Responsibility Principle (SRP)
- `CashDispenser` only handles cash operations
- `AuthenticationService` only handles authentication
- `TransactionService` only handles transaction records
- Each state class handles behavior for that specific state

### Open/Closed Principle (OCP)
- New transaction types via `TransactionStrategy` without modifying existing code
- New notification channels via `TransactionObserver`
- New denominations can be added to `Denomination` enum

### Liskov Substitution Principle (LSP)
- All `ATMState` implementations can be used interchangeably
- All `TransactionStrategy` implementations fulfill the same contract

### Interface Segregation Principle (ISP)
- `BankService` is separate from `AuthenticationService`
- `TransactionObserver` has focused methods: `onTransactionComplete`, `onTransactionFailed`

### Dependency Inversion Principle (DIP)
- `ATM` depends on `BankService` interface, not concrete implementation
- Easy to swap `InMemoryBankService` for actual bank integration

---

## 6. ATM State Machine

```
                              ┌───────────────┐
                              │    IDLE       │◄──────────────────────┐
                              │ (Insert Card) │                       │
                              └───────┬───────┘                       │
                                      │ insertCard()                  │
                                      ▼                               │
                              ┌───────────────┐                       │
                              │ CARD_INSERTED │                       │
                              │ (Enter PIN)   │───────────────────────┤
                              └───────┬───────┘    cancel()           │
                                      │ enterPin()                    │
                                      ▼                               │
                              ┌───────────────┐                       │
                              │ AUTHENTICATED │                       │
                              │(Select Trans) │───────────────────────┤
                              └───────┬───────┘    ejectCard()        │
                                      │ selectTransaction()           │
                                      ▼                               │
                              ┌───────────────┐                       │
                              │  TRANSACTION  │                       │
                              │  (Details)    │───────────────────────┤
                              └───────┬───────┘    cancel()           │
                                      │ confirm()                     │
                                      ▼                               │
                              ┌───────────────┐                       │
                              │  PROCESSING   │                       │
                              │               │                       │
                              └───────┬───────┘                       │
                                      │ complete                      │
                                      ▼                               │
                              ┌───────────────┐                       │
                              │ AUTHENTICATED │                       │
                              │(Another Trans)│───────────────────────┘
                              └───────────────┘
```

---

## 7. Concurrency Handling

### Thread-Safe Components

```java
// ATM uses ReentrantLock for operation serialization
public void insertCard(Card card) {
    operationLock.lock();
    try {
        currentState.insertCard(card);
    } finally {
        operationLock.unlock();
    }
}

// Account uses ReentrantLock for balance operations
public boolean debit(BigDecimal amount) {
    lock.lock();
    try {
        if (balance.compareTo(amount) < 0) return false;
        balance = balance.subtract(amount);
        return true;
    } finally {
        lock.unlock();
    }
}

// CashInventory uses lock for dispensing
public Map<Denomination, Integer> dispense(BigDecimal amount) {
    lock.lock();
    try {
        // Calculate and dispense
    } finally {
        lock.unlock();
    }
}
```

---

## 8. Extension Points

### Adding a New Transaction Type

```java
// 1. Create new strategy
public class FundTransferStrategy implements TransactionStrategy {
    @Override
    public Transaction execute(Account account, Card card, 
                               BigDecimal amount, ATM atm) {
        // Implementation
    }
}

// 2. Register with ATM
atm.registerTransactionStrategy(TransactionType.FUND_TRANSFER, 
                                 new FundTransferStrategy());
```

### Adding a New Notification Channel

```java
// 1. Implement observer
public class EmailNotificationObserver implements TransactionObserver {
    @Override
    public void onTransactionComplete(Transaction transaction) {
        sendEmail(transaction);
    }
}

// 2. Register with ATM
atm.addObserver(new EmailNotificationObserver());
```

### Integrating with Real Bank

```java
// 1. Implement BankService
public class RealBankService implements BankService {
    private final BankApiClient apiClient;
    
    @Override
    public boolean debit(String accountNumber, BigDecimal amount) {
        return apiClient.processDebit(accountNumber, amount);
    }
}

// 2. Inject into ATM
ATM atm = new ATM("ATM-001", "Location", 
                   realBankService, authService, txnService);
```

---

## 9. File Structure

```
atm/
├── enums/
│   ├── ATMStateType.java
│   ├── TransactionType.java
│   ├── TransactionStatus.java
│   ├── AccountType.java
│   └── Denomination.java
├── exceptions/
│   ├── ATMException.java
│   ├── AuthenticationException.java
│   ├── TransactionException.java
│   ├── InvalidStateException.java
│   └── CashDispenserException.java
├── models/
│   ├── Card.java
│   ├── Account.java
│   ├── Transaction.java
│   ├── CashInventory.java
│   └── Receipt.java
├── states/
│   ├── ATMState.java
│   ├── IdleState.java
│   ├── CardInsertedState.java
│   ├── AuthenticatedState.java
│   ├── TransactionState.java
│   ├── ProcessingState.java
│   └── OutOfServiceState.java
├── strategies/
│   ├── TransactionStrategy.java
│   ├── BalanceInquiryStrategy.java
│   ├── WithdrawalStrategy.java
│   ├── DepositStrategy.java
│   └── MiniStatementStrategy.java
├── dispenser/
│   ├── CashDispenser.java
│   └── DenominationHandler.java
├── services/
│   ├── BankService.java
│   ├── AuthenticationService.java
│   ├── TransactionService.java
│   └── impl/
│       ├── InMemoryBankService.java
│       ├── DefaultAuthenticationService.java
│       └── DefaultTransactionService.java
├── observers/
│   ├── TransactionObserver.java
│   ├── SMSNotificationObserver.java
│   └── AuditLogObserver.java
├── ATM.java                 # Facade
└── Main.java                # Demo
```

---

## 10. Usage Example

```java
// Initialize ATM
ATM atm = new ATM("ATM-001", "Main Street Branch");

// Load cash
atm.loadCash(Denomination.NOTE_2000, 50);
atm.loadCash(Denomination.NOTE_500, 100);
atm.loadCash(Denomination.NOTE_100, 200);

// Add observers
atm.addObserver(new SMSNotificationObserver());
atm.addObserver(new AuditLogObserver());

// Customer interaction
Card card = bankService.getCard("4111111111111111");
atm.insertCard(card);
atm.enterPin("1234");

// Check balance
atm.checkBalance();

// Withdraw cash
atm.quickWithdraw(new BigDecimal("5000"));

// End session
atm.cancel();
```

---

## 11. Key Design Decisions

1. **State Machine for ATM Flow**: Clear state transitions prevent invalid operations (can't withdraw without authentication).

2. **Strategy for Transactions**: New transaction types can be added without modifying ATM core logic.

3. **Chain of Responsibility for Dispensing**: Greedy algorithm dispenses highest denominations first, easily extensible.

4. **Observer for Notifications**: Decoupled notifications - adding email doesn't affect SMS.

5. **Facade for Simplicity**: `ATM` class hides complexity of states, strategies, and services.

6. **Thread-Safe Operations**: ReentrantLock ensures data consistency for concurrent access.

7. **In-Memory Bank Service**: Easy to swap with real integration, follows DIP.

---

## 12. Future Enhancements

- [ ] Database persistence for transactions
- [ ] PIN change functionality
- [ ] Card-less (UPI/QR) withdrawals
- [ ] Multi-language support
- [ ] Cheque deposit functionality
- [ ] Bill payment integration
- [ ] ATM network integration
- [ ] Real-time fraud detection
- [ ] Biometric authentication
- [ ] Receipt email/SMS option



