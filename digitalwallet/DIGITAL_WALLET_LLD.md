# Digital Wallet Service - Low-Level Design

## Overview

A comprehensive digital wallet system that allows users to manage accounts, payment methods, perform fund transfers, handle multiple currencies, and maintain transaction history. The system is designed following SOLID principles and common design patterns for extensibility, security, and scalability.

---

## 1. Responsibility Breakdown

| Component | Responsibility |
|-----------|---------------|
| **DigitalWallet (Facade)** | Single entry point for all wallet operations; wires components together |
| **UserService** | Manage user accounts, authentication, and personal information |
| **WalletService** | Create and manage wallets, handle balance operations |
| **PaymentMethodService** | Add, remove, and validate payment methods (cards, bank accounts) |
| **TransferService** | Execute P2P transfers and external account transfers |
| **TransactionService** | Record, query, and generate statements of transactions |
| **CurrencyService** | Handle currency conversions and exchange rates |
| **SecurityService** | Encryption, authentication, fraud detection |
| **NotificationService** | Send transaction alerts and notifications |

---

## 2. Key Abstractions

### Enums

```
┌─────────────────────────────────────────────────────────────────┐
│  Currency        - USD, EUR, GBP, INR, JPY, etc.               │
│  TransactionType - DEPOSIT, WITHDRAWAL, TRANSFER_IN,           │
│                    TRANSFER_OUT, CURRENCY_EXCHANGE              │
│  TransactionStatus - PENDING, PROCESSING, COMPLETED,           │
│                      FAILED, CANCELLED, REVERSED                │
│  PaymentMethodType - CREDIT_CARD, DEBIT_CARD, BANK_ACCOUNT     │
│  AccountStatus   - ACTIVE, SUSPENDED, CLOSED, PENDING_VERIFICATION│
│  TransferType    - P2P, EXTERNAL, SELF_TRANSFER                │
└─────────────────────────────────────────────────────────────────┘
```

### Core Models

| Model | Purpose |
|-------|---------|
| `User` | User account with personal info, KYC status, security settings |
| `Wallet` | User's wallet with balances per currency, transaction limits |
| `WalletBalance` | Balance for a specific currency within a wallet |
| `Transaction` | Record of any financial operation with full audit trail |
| `Transfer` | Fund transfer between wallets or to external accounts |
| `PaymentMethod` | Abstract base for credit cards, bank accounts |
| `CreditCard` | Credit/debit card with masked number, expiry |
| `BankAccount` | Bank account with routing and account numbers |
| `ExchangeRate` | Currency pair exchange rate with timestamp |
| `TransactionStatement` | Summary of transactions for a period |

### Strategy Interfaces

| Interface | Purpose | Implementations |
|-----------|---------|-----------------|
| `CurrencyConversionStrategy` | Convert amounts between currencies | RealTimeRateStrategy, FixedRateStrategy |
| `TransferValidationStrategy` | Validate transfers before execution | BalanceValidation, LimitValidation, FraudValidation |
| `FraudDetectionStrategy` | Detect suspicious transactions | RuleBasedFraudDetection, MLBasedFraudDetection |
| `FeeCalculationStrategy` | Calculate transaction fees | PercentageFee, FlatFee, TieredFee |

### Observer Interfaces

| Interface | Purpose |
|-----------|---------|
| `TransactionObserver` | React to transaction lifecycle events |
| `WalletObserver` | React to wallet balance changes |
| `SecurityObserver` | React to security events (login, suspicious activity) |

---

## 3. Class Diagram

```
                          ┌──────────────────────┐
                          │   DigitalWallet      │
                          │     (Facade)         │
                          └──────────┬───────────┘
                                     │
       ┌─────────────┬───────────────┼───────────────┬─────────────┐
       │             │               │               │             │
       ▼             ▼               ▼               ▼             ▼
┌─────────────┐ ┌─────────────┐ ┌─────────────┐ ┌─────────────┐ ┌─────────────┐
│ UserService │ │WalletService│ │ Transfer    │ │Transaction  │ │ Currency    │
│             │ │             │ │ Service     │ │ Service     │ │ Service     │
└──────┬──────┘ └──────┬──────┘ └──────┬──────┘ └──────┬──────┘ └──────┬──────┘
       │               │               │               │               │
       ▼               ▼               ▼               ▼               ▼
┌─────────────┐ ┌─────────────┐ ┌─────────────┐ ┌─────────────┐ ┌─────────────┐
│User         │ │Wallet       │ │Transfer     │ │Transaction  │ │ExchangeRate │
│Repository   │ │Repository   │ │Repository   │ │Repository   │ │Repository   │
└─────────────┘ └─────────────┘ └─────────────┘ └─────────────┘ └─────────────┘


                    ┌─────────────┐     ┌─────────────┐
                    │PaymentMethod│────▶│ Security    │
                    │  Service    │     │  Service    │
                    └──────┬──────┘     └──────┬──────┘
                           │                   │
              ┌────────────┴────────┐    ┌─────┴──────────┐
              │                     │    │                │
              ▼                     ▼    ▼                ▼
        ┌───────────┐        ┌───────────┐        ┌───────────┐
        │CreditCard │        │BankAccount│        │Fraud      │
        │Repository │        │Repository │        │Detection  │
        └───────────┘        └───────────┘        │Strategy   │
                                                  └───────────┘

Strategy Layer:
┌──────────────────────────────────────────────────────────────────┐
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐              │
│  │Currency     │  │Transfer     │  │Fee          │              │
│  │Conversion   │  │Validation   │  │Calculation  │              │
│  │Strategy     │  │Strategy     │  │Strategy     │              │
│  └─────────────┘  └─────────────┘  └─────────────┘              │
└──────────────────────────────────────────────────────────────────┘
```

---

## 4. Design Patterns Used

### 1. **Facade Pattern** - `DigitalWallet` class
- Provides a simplified interface to the complex subsystem
- Coordinates between all services
- Entry point for all wallet operations

```java
DigitalWallet wallet = new DigitalWallet();
wallet.createUser("John Doe", "john@example.com");
wallet.transferFunds(senderId, receiverId, amount, currency);
```

### 2. **Strategy Pattern** - Currency Conversion, Fee Calculation, Fraud Detection
- Allows runtime selection of algorithms
- Easy to add new conversion providers, fee structures
- Follows Open/Closed Principle

```java
// Switch between real-time and fixed rate conversion
currencyService.setConversionStrategy(new RealTimeRateStrategy(rateProvider));

// Different fee structures
transferService.setFeeStrategy(new TieredFeeStrategy());
```

### 3. **Observer Pattern** - Transaction & Security Events
- Notification service receives transaction updates
- Audit logging receives all security events
- Decouples event producers from consumers

```java
public interface TransactionObserver {
    void onTransactionCreated(Transaction transaction);
    void onTransactionCompleted(Transaction transaction);
    void onTransactionFailed(Transaction transaction, String reason);
}
```

### 4. **Builder Pattern** - `Transaction`, `Transfer`, `User`
- Complex object construction with many optional parameters
- Fluent API for readability
- Validation during build

```java
Transaction tx = Transaction.builder()
    .id(UUID.randomUUID().toString())
    .walletId(wallet.getId())
    .type(TransactionType.TRANSFER_OUT)
    .amount(new BigDecimal("100.00"))
    .currency(Currency.USD)
    .description("Payment to friend")
    .build();
```

### 5. **Repository Pattern** - Data Access
- Abstracts data storage from business logic
- Easy to swap implementations (in-memory → database)
- Follows Dependency Inversion Principle

### 6. **Factory Pattern** - Payment Method Creation
- Encapsulates creation logic for different payment methods
- Validates payment method data before creation
- Centralizes object instantiation

```java
PaymentMethod card = PaymentMethodFactory.createCreditCard(
    "4111111111111111", "12/25", "123", "John Doe"
);
```

### 7. **Chain of Responsibility** - Transfer Validation
- Multiple validators in sequence
- Each validator can pass or reject
- Easy to add new validation rules

```java
transferValidator
    .addValidator(new BalanceValidator())
    .addValidator(new DailyLimitValidator())
    .addValidator(new FraudValidator());
```

---

## 5. SOLID Principles Applied

### Single Responsibility Principle (SRP)
- Each service handles one domain concern
- `WalletService` only manages wallets
- `TransferService` only handles transfers
- Models are pure data holders

### Open/Closed Principle (OCP)
- New currencies via `Currency` enum without modifying logic
- New payment methods via `PaymentMethodFactory`
- New validation rules via `TransferValidationStrategy`
- New fee structures via `FeeCalculationStrategy`

### Liskov Substitution Principle (LSP)
- All `PaymentMethod` subclasses are interchangeable
- All `Repository` implementations fulfill the same contract
- All strategy implementations can be swapped

### Interface Segregation Principle (ISP)
- `TransactionObserver` is separate from `SecurityObserver`
- `WalletRepository` is separate from `TransactionRepository`
- Clients only depend on interfaces they need

### Dependency Inversion Principle (DIP)
- Services depend on interfaces, not concrete implementations
- `TransferService` depends on `WalletRepository` interface
- `CurrencyService` depends on `CurrencyConversionStrategy` interface

---

## 6. Concurrency Handling

### Thread-Safe Components

```java
// Wallet uses ReentrantLock for atomic balance operations
public class Wallet {
    private final ReentrantLock lock = new ReentrantLock();
    
    public boolean debit(BigDecimal amount, Currency currency) {
        lock.lock();
        try {
            WalletBalance balance = getBalance(currency);
            if (balance.getAmount().compareTo(amount) >= 0) {
                balance.debit(amount);
                return true;
            }
            return false;
        } finally {
            lock.unlock();
        }
    }
}
```

### Transfer Atomicity

```java
// TransferService uses lock ordering to prevent deadlocks
public Transfer executeTransfer(String fromWalletId, String toWalletId, BigDecimal amount) {
    // Always acquire locks in consistent order (by wallet ID)
    String firstLock = fromWalletId.compareTo(toWalletId) < 0 ? fromWalletId : toWalletId;
    String secondLock = fromWalletId.compareTo(toWalletId) < 0 ? toWalletId : fromWalletId;
    
    synchronized (firstLock.intern()) {
        synchronized (secondLock.intern()) {
            // Atomic transfer logic
        }
    }
}
```

- All repositories use `ConcurrentHashMap`
- `Wallet` uses `ReentrantLock` for balance operations
- `Transaction` uses volatile fields for status visibility
- Lock ordering prevents deadlocks in transfers

---

## 7. Security Considerations

### Data Protection
- Card numbers stored masked (only last 4 digits visible)
- Passwords hashed with BCrypt
- Sensitive data encrypted at rest

### Authentication & Authorization
- PIN verification for transactions
- Session tokens for API access
- Rate limiting on sensitive operations

### Fraud Prevention
- Transaction velocity checks
- Amount threshold alerts
- Suspicious pattern detection
- Geographic anomaly detection

---

## 8. Extension Points

### Adding a New Currency

```java
// 1. Add to Currency enum
public enum Currency {
    USD, EUR, GBP, INR, JPY,
    BTC, ETH  // New cryptocurrencies
}

// 2. Add exchange rates
exchangeRateRepository.save(new ExchangeRate(Currency.BTC, Currency.USD, new BigDecimal("45000")));
```

### Adding a New Payment Method

```java
// 1. Create new PaymentMethod subclass
public class DigitalWalletPayment extends PaymentMethod {
    private String walletProvider;  // PayPal, Venmo, etc.
    private String accountEmail;
    // ...
}

// 2. Update factory
public static PaymentMethod createDigitalWallet(String provider, String email) {
    // validation and creation
}
```

### Adding a New Fee Structure

```java
// 1. Implement FeeCalculationStrategy
public class SubscriptionFeeStrategy implements FeeCalculationStrategy {
    @Override
    public BigDecimal calculateFee(Transfer transfer) {
        // Premium users get free transfers
        if (transfer.getUser().isPremium()) {
            return BigDecimal.ZERO;
        }
        return super.calculateFee(transfer);
    }
}
```

### Adding New Fraud Detection Rules

```java
// 1. Implement FraudDetectionStrategy
public class VelocityFraudDetector implements FraudDetectionStrategy {
    @Override
    public FraudCheckResult check(Transfer transfer) {
        int recentCount = getRecentTransferCount(transfer.getUserId(), Duration.ofHours(1));
        if (recentCount > 10) {
            return FraudCheckResult.suspicious("High transaction velocity");
        }
        return FraudCheckResult.clean();
    }
}
```

---

## 9. Transaction Flow

### P2P Transfer Flow

```
[User Initiates Transfer]
       │
       ▼
   ┌───────────────┐
   │ Validate PIN  │
   └───────┬───────┘
           │
           ▼
   ┌───────────────┐     ┌────────────────┐
   │ Check Balance │────▶│ Fraud Detection│
   └───────┬───────┘     └────────┬───────┘
           │                      │
           ▼                      ▼
   ┌───────────────┐     ┌────────────────┐
   │ Check Limits  │     │ Block/Allow    │
   └───────┬───────┘     └────────┬───────┘
           │                      │
           └──────────┬───────────┘
                      ▼
              ┌───────────────┐
              │ Lock Wallets  │
              └───────┬───────┘
                      │
                      ▼
              ┌───────────────┐
              │ Debit Sender  │
              └───────┬───────┘
                      │
                      ▼
              ┌───────────────┐
              │Credit Receiver│
              └───────┬───────┘
                      │
                      ▼
              ┌───────────────┐
              │Record Trans.  │
              └───────┬───────┘
                      │
                      ▼
              ┌───────────────┐
              │Notify Users   │
              └───────────────┘
```

---

## 10. File Structure

```
digitalwallet/
├── enums/
│   ├── AccountStatus.java
│   ├── Currency.java
│   ├── PaymentMethodType.java
│   ├── TransactionStatus.java
│   ├── TransactionType.java
│   └── TransferType.java
├── exceptions/
│   ├── AuthenticationException.java
│   ├── InsufficientBalanceException.java
│   ├── InvalidPaymentMethodException.java
│   ├── TransferException.java
│   ├── WalletException.java
│   └── CurrencyConversionException.java
├── models/
│   ├── User.java
│   ├── Wallet.java
│   ├── WalletBalance.java
│   ├── Transaction.java
│   ├── Transfer.java
│   ├── PaymentMethod.java
│   ├── CreditCard.java
│   ├── BankAccount.java
│   ├── ExchangeRate.java
│   └── TransactionStatement.java
├── observers/
│   ├── TransactionObserver.java
│   ├── WalletObserver.java
│   ├── AuditLogObserver.java
│   └── NotificationObserver.java
├── repositories/
│   ├── Repository.java
│   ├── UserRepository.java
│   ├── WalletRepository.java
│   ├── TransactionRepository.java
│   ├── PaymentMethodRepository.java
│   ├── ExchangeRateRepository.java
│   └── impl/
│       ├── InMemoryUserRepository.java
│       ├── InMemoryWalletRepository.java
│       ├── InMemoryTransactionRepository.java
│       ├── InMemoryPaymentMethodRepository.java
│       └── InMemoryExchangeRateRepository.java
├── services/
│   ├── UserService.java
│   ├── WalletService.java
│   ├── TransferService.java
│   ├── TransactionService.java
│   ├── PaymentMethodService.java
│   ├── CurrencyService.java
│   ├── SecurityService.java
│   └── impl/
│       ├── UserServiceImpl.java
│       ├── WalletServiceImpl.java
│       ├── TransferServiceImpl.java
│       ├── TransactionServiceImpl.java
│       ├── PaymentMethodServiceImpl.java
│       ├── CurrencyServiceImpl.java
│       └── SecurityServiceImpl.java
├── strategies/
│   ├── conversion/
│   │   ├── CurrencyConversionStrategy.java
│   │   ├── FixedRateConversionStrategy.java
│   │   └── RealTimeConversionStrategy.java
│   ├── fee/
│   │   ├── FeeCalculationStrategy.java
│   │   ├── FlatFeeStrategy.java
│   │   ├── PercentageFeeStrategy.java
│   │   └── TieredFeeStrategy.java
│   ├── fraud/
│   │   ├── FraudDetectionStrategy.java
│   │   ├── RuleBasedFraudDetector.java
│   │   └── VelocityFraudDetector.java
│   └── validation/
│       ├── TransferValidationStrategy.java
│       ├── BalanceValidationStrategy.java
│       └── LimitValidationStrategy.java
├── factories/
│   └── PaymentMethodFactory.java
├── DigitalWallet.java     # Facade
└── Main.java              # Demo
```

---

## 11. Usage Example

```java
// Initialize digital wallet system
DigitalWallet digitalWallet = new DigitalWallet();

// Create users
User alice = digitalWallet.createUser("Alice Smith", "alice@example.com", "+1234567890");
User bob = digitalWallet.createUser("Bob Jones", "bob@example.com", "+0987654321");

// Set up wallets (automatically created with user)
Wallet aliceWallet = digitalWallet.getWallet(alice.getId());
Wallet bobWallet = digitalWallet.getWallet(bob.getId());

// Add payment methods
CreditCard aliceCard = digitalWallet.addCreditCard(
    alice.getId(),
    "4111111111111111",
    "12/25",
    "123",
    "Alice Smith"
);

BankAccount aliceBank = digitalWallet.addBankAccount(
    alice.getId(),
    "021000021",
    "1234567890",
    "Checking"
);

// Deposit funds
digitalWallet.deposit(alice.getId(), new BigDecimal("1000.00"), Currency.USD, aliceCard.getId());

// Transfer between users
Transfer transfer = digitalWallet.transfer(
    alice.getId(),
    bob.getId(),
    new BigDecimal("250.00"),
    Currency.USD,
    "Birthday gift"
);

// Currency exchange
digitalWallet.exchangeCurrency(
    alice.getId(),
    new BigDecimal("100.00"),
    Currency.USD,
    Currency.EUR
);

// Get transaction history
TransactionStatement statement = digitalWallet.getStatement(
    alice.getId(),
    LocalDateTime.now().minusMonths(1),
    LocalDateTime.now()
);

// Withdraw to bank account
digitalWallet.withdraw(
    alice.getId(),
    new BigDecimal("200.00"),
    Currency.USD,
    aliceBank.getId()
);
```

---

## 12. Key Design Decisions

1. **Wallet-per-Currency Balances**: Each wallet maintains separate balances per currency, avoiding mixing and making conversions explicit.

2. **Immutable Transactions**: Once created, transactions cannot be modified - only new reversal transactions can be created.

3. **Builder Pattern for Complex Objects**: `User`, `Transaction`, and `Transfer` use builders for clean construction with validation.

4. **Two-Phase Locking for Transfers**: Prevents race conditions and ensures atomicity of fund transfers.

5. **Strategy for Flexibility**: Currency conversion, fee calculation, and fraud detection can be easily extended.

6. **Observer for Decoupling**: Notifications, audit logging, and analytics are decoupled from core transaction processing.

7. **Masked Sensitive Data**: Card numbers and bank accounts are stored with masking for security.

8. **Idempotency Keys**: Transfers use idempotency keys to prevent duplicate processing.

---

## 13. Scalability Considerations

- **Horizontal Scaling**: Stateless services can be replicated
- **Sharding**: Wallets can be sharded by user ID
- **Event Sourcing**: Transaction log enables replay and auditing
- **CQRS**: Separate read/write models for statement generation
- **Rate Limiting**: Per-user limits prevent abuse
- **Circuit Breakers**: External service calls wrapped with circuit breakers

---

## 14. Future Enhancements

- [ ] Database persistence (JPA/Hibernate)
- [ ] REST API layer with OpenAPI documentation
- [ ] Two-factor authentication (2FA)
- [ ] Recurring transfers and scheduled payments
- [ ] Spending categories and budgeting
- [ ] Integration with payment gateways (Stripe, PayPal)
- [ ] Push notifications (Firebase, APNs)
- [ ] Real-time exchange rate feeds
- [ ] Machine learning fraud detection
- [ ] Multi-tenant support for white-label wallets




