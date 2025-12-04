package digitalwallet;

import digitalwallet.enums.Currency;
import digitalwallet.models.*;
import digitalwallet.observers.AuditLogObserver;
import digitalwallet.observers.NotificationObserver;
import digitalwallet.repositories.*;
import digitalwallet.repositories.impl.*;
import digitalwallet.services.*;
import digitalwallet.services.impl.*;
import digitalwallet.strategies.conversion.CurrencyConversionStrategy;
import digitalwallet.strategies.conversion.FixedRateConversionStrategy;
import digitalwallet.strategies.fee.FeeCalculationStrategy;
import digitalwallet.strategies.fee.PercentageFeeStrategy;
import digitalwallet.strategies.fraud.FraudDetectionStrategy;
import digitalwallet.strategies.fraud.RuleBasedFraudDetector;
import digitalwallet.strategies.validation.CompositeValidationStrategy;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Digital Wallet Facade.
 * Provides a simplified interface to the digital wallet subsystem.
 * Follows Facade Pattern - hides complexity and provides clean API.
 */
public class DigitalWallet {
    
    // Repositories
    private final UserRepository userRepository;
    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;
    private final TransferRepository transferRepository;
    private final PaymentMethodRepository paymentMethodRepository;
    private final ExchangeRateRepository exchangeRateRepository;

    // Services
    private final UserService userService;
    private final WalletServiceImpl walletService;
    private final TransactionServiceImpl transactionService;
    private final TransferServiceImpl transferService;
    private final CurrencyService currencyService;
    private final PaymentMethodService paymentMethodService;

    // Strategies
    private final CurrencyConversionStrategy conversionStrategy;
    private final FeeCalculationStrategy feeStrategy;
    private final FraudDetectionStrategy fraudDetectionStrategy;
    private final CompositeValidationStrategy validationStrategy;

    // Observers
    private final AuditLogObserver auditLogObserver;
    private final NotificationObserver notificationObserver;

    /**
     * Create a new Digital Wallet system with default configuration
     */
    public DigitalWallet() {
        // Initialize repositories
        this.userRepository = new InMemoryUserRepository();
        this.walletRepository = new InMemoryWalletRepository();
        this.transactionRepository = new InMemoryTransactionRepository();
        this.transferRepository = new InMemoryTransferRepository();
        this.paymentMethodRepository = new InMemoryPaymentMethodRepository();
        this.exchangeRateRepository = new InMemoryExchangeRateRepository();

        // Initialize strategies
        this.conversionStrategy = new FixedRateConversionStrategy(exchangeRateRepository);
        this.feeStrategy = new PercentageFeeStrategy(new BigDecimal("1.5"));
        this.validationStrategy = CompositeValidationStrategy.createDefault(transferRepository);
        this.fraudDetectionStrategy = new RuleBasedFraudDetector(transferRepository);

        // Initialize services
        this.userService = new UserServiceImpl(userRepository);
        this.walletService = new WalletServiceImpl(walletRepository);
        this.transactionService = new TransactionServiceImpl(transactionRepository, walletRepository);
        this.currencyService = new CurrencyServiceImpl(exchangeRateRepository, conversionStrategy);
        this.paymentMethodService = new PaymentMethodServiceImpl(paymentMethodRepository);
        this.transferService = new TransferServiceImpl(
            transferRepository, walletRepository, walletService, transactionService,
            currencyService, validationStrategy, feeStrategy, fraudDetectionStrategy
        );

        // Initialize observers
        this.auditLogObserver = new AuditLogObserver(true);
        this.notificationObserver = new NotificationObserver();

        // Register observers
        walletService.addObserver(auditLogObserver);
        walletService.addObserver(notificationObserver);
        transactionService.addObserver(auditLogObserver);
        transactionService.addObserver(notificationObserver);
        transferService.addObserver(auditLogObserver);
        transferService.addObserver(notificationObserver);
    }

    // ==================== User Operations ====================

    /**
     * Create a new user and wallet
     */
    public User createUser(String name, String email, String phoneNumber, String pin) {
        User user = userService.createUser(name, email, phoneNumber, pin);
        walletService.createWallet(user.getId(), Currency.USD);
        userService.verifyKyc(user.getId()); // Auto-verify for demo
        return user;
    }

    /**
     * Get user by ID
     */
    public Optional<User> getUser(String userId) {
        return userService.getUser(userId);
    }

    /**
     * Get user by email
     */
    public Optional<User> getUserByEmail(String email) {
        return userService.getUserByEmail(email);
    }

    /**
     * Verify user PIN
     */
    public boolean verifyPin(String userId, String pin) {
        return userService.verifyPin(userId, pin);
    }

    // ==================== Wallet Operations ====================

    /**
     * Get wallet for a user
     */
    public Optional<Wallet> getWallet(String userId) {
        return walletService.getWalletByUserId(userId);
    }

    /**
     * Get wallet balance for a currency
     */
    public BigDecimal getBalance(String userId, Currency currency) {
        Wallet wallet = walletService.getWalletByUserId(userId)
            .orElseThrow(() -> new IllegalArgumentException("Wallet not found for user"));
        return wallet.getAvailableBalance(currency);
    }

    /**
     * Get all wallet balances
     */
    public Map<Currency, WalletBalance> getAllBalances(String userId) {
        Wallet wallet = walletService.getWalletByUserId(userId)
            .orElseThrow(() -> new IllegalArgumentException("Wallet not found for user"));
        return walletService.getAllBalances(wallet.getId());
    }

    // ==================== Payment Method Operations ====================

    /**
     * Add a credit card
     */
    public CreditCard addCreditCard(String userId, String cardNumber, String expiry,
                                     String cvv, String cardholderName) {
        CreditCard card = paymentMethodService.addCreditCard(
            userId, cardNumber, expiry, cvv, cardholderName);
        paymentMethodService.verifyPaymentMethod(card.getId()); // Auto-verify for demo
        return card;
    }

    /**
     * Add a bank account
     */
    public BankAccount addBankAccount(String userId, String bankName, String routingNumber,
                                       String accountNumber, String accountType,
                                       String accountHolderName) {
        BankAccount account = paymentMethodService.addBankAccount(
            userId, bankName, routingNumber, accountNumber, accountType, accountHolderName);
        paymentMethodService.verifyPaymentMethod(account.getId()); // Auto-verify for demo
        return account;
    }

    /**
     * Get all payment methods for a user
     */
    public List<PaymentMethod> getPaymentMethods(String userId) {
        return paymentMethodService.getActivePaymentMethods(userId);
    }

    /**
     * Remove a payment method
     */
    public void removePaymentMethod(String paymentMethodId) {
        paymentMethodService.removePaymentMethod(paymentMethodId);
    }

    // ==================== Fund Operations ====================

    /**
     * Deposit funds to wallet (from payment method)
     */
    public Transaction deposit(String userId, BigDecimal amount, Currency currency,
                               String paymentMethodId) {
        Wallet wallet = walletService.getWalletByUserId(userId)
            .orElseThrow(() -> new IllegalArgumentException("Wallet not found"));

        // Credit the wallet
        walletService.credit(wallet.getId(), amount, currency);

        // Create transaction record
        Transaction tx = transactionService.createTransaction(
            wallet.getId(),
            digitalwallet.enums.TransactionType.DEPOSIT,
            amount,
            currency,
            "Deposit from payment method",
            paymentMethodId
        );
        transactionService.completeTransaction(tx.getId());
        
        return tx;
    }

    /**
     * Withdraw funds to external account
     */
    public Transfer withdraw(String userId, BigDecimal amount, Currency currency,
                              String paymentMethodId) {
        Wallet wallet = walletService.getWalletByUserId(userId)
            .orElseThrow(() -> new IllegalArgumentException("Wallet not found"));

        return transferService.transferToExternal(
            wallet.getId(), paymentMethodId, amount, currency, "Withdrawal");
    }

    // ==================== Transfer Operations ====================

    /**
     * Transfer funds to another user
     */
    public Transfer transfer(String fromUserId, String toUserId, BigDecimal amount,
                              Currency currency, String description) {
        Wallet fromWallet = walletService.getWalletByUserId(fromUserId)
            .orElseThrow(() -> new IllegalArgumentException("Sender wallet not found"));
        Wallet toWallet = walletService.getWalletByUserId(toUserId)
            .orElseThrow(() -> new IllegalArgumentException("Recipient wallet not found"));

        return transferService.transfer(
            fromWallet.getId(), toWallet.getId(), amount, currency, description);
    }

    /**
     * Transfer with currency conversion
     */
    public Transfer transferWithConversion(String fromUserId, String toUserId,
                                            BigDecimal amount, Currency fromCurrency,
                                            Currency toCurrency, String description) {
        Wallet fromWallet = walletService.getWalletByUserId(fromUserId)
            .orElseThrow(() -> new IllegalArgumentException("Sender wallet not found"));
        Wallet toWallet = walletService.getWalletByUserId(toUserId)
            .orElseThrow(() -> new IllegalArgumentException("Recipient wallet not found"));

        return transferService.transferWithConversion(
            fromWallet.getId(), toWallet.getId(), amount, fromCurrency, toCurrency, description);
    }

    /**
     * Exchange currency within own wallet
     */
    public Transfer exchangeCurrency(String userId, BigDecimal amount,
                                      Currency fromCurrency, Currency toCurrency) {
        Wallet wallet = walletService.getWalletByUserId(userId)
            .orElseThrow(() -> new IllegalArgumentException("Wallet not found"));

        return transferService.transferWithConversion(
            wallet.getId(), wallet.getId(), amount, fromCurrency, toCurrency,
            "Currency exchange");
    }

    // ==================== Transaction History ====================

    /**
     * Get transaction history
     */
    public List<Transaction> getTransactions(String userId) {
        Wallet wallet = walletService.getWalletByUserId(userId)
            .orElseThrow(() -> new IllegalArgumentException("Wallet not found"));
        return transactionService.getTransactions(wallet.getId());
    }

    /**
     * Get transaction history for date range
     */
    public List<Transaction> getTransactions(String userId, LocalDateTime start, LocalDateTime end) {
        Wallet wallet = walletService.getWalletByUserId(userId)
            .orElseThrow(() -> new IllegalArgumentException("Wallet not found"));
        return transactionService.getTransactions(wallet.getId(), start, end);
    }

    /**
     * Generate transaction statement
     */
    public TransactionStatement getStatement(String userId, LocalDateTime start, LocalDateTime end) {
        Wallet wallet = walletService.getWalletByUserId(userId)
            .orElseThrow(() -> new IllegalArgumentException("Wallet not found"));
        return transactionService.generateStatement(wallet.getId(), start, end);
    }

    /**
     * Get transfer history
     */
    public List<Transfer> getTransfers(String userId) {
        Wallet wallet = walletService.getWalletByUserId(userId)
            .orElseThrow(() -> new IllegalArgumentException("Wallet not found"));
        return transferService.getTransfers(wallet.getId());
    }

    // ==================== Currency Operations ====================

    /**
     * Convert currency (preview)
     */
    public BigDecimal convertCurrency(BigDecimal amount, Currency from, Currency to) {
        return currencyService.convert(amount, from, to);
    }

    /**
     * Get exchange rate
     */
    public ExchangeRate getExchangeRate(Currency from, Currency to) {
        return currencyService.getExchangeRate(from, to);
    }

    /**
     * Get all exchange rates
     */
    public List<ExchangeRate> getAllExchangeRates() {
        return currencyService.getAllExchangeRates();
    }

    // ==================== Account Management ====================

    /**
     * Set daily transfer limit
     */
    public void setDailyTransferLimit(String userId, BigDecimal limit) {
        Wallet wallet = walletService.getWalletByUserId(userId)
            .orElseThrow(() -> new IllegalArgumentException("Wallet not found"));
        walletService.setDailyTransferLimit(wallet.getId(), limit);
    }

    /**
     * Suspend user account
     */
    public void suspendAccount(String userId) {
        userService.suspendUser(userId);
        Wallet wallet = walletService.getWalletByUserId(userId).orElse(null);
        if (wallet != null) {
            walletService.deactivateWallet(wallet.getId());
        }
    }

    /**
     * Close user account
     */
    public void closeAccount(String userId) {
        userService.closeAccount(userId);
        Wallet wallet = walletService.getWalletByUserId(userId).orElse(null);
        if (wallet != null) {
            walletService.deactivateWallet(wallet.getId());
        }
    }
}



