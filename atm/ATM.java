package atm;

import atm.dispenser.CashDispenser;
import atm.enums.Denomination;
import atm.enums.TransactionType;
import atm.exceptions.ATMException;
import atm.models.*;
import atm.observers.TransactionObserver;
import atm.services.AuthenticationService;
import atm.services.BankService;
import atm.services.TransactionService;
import atm.services.impl.DefaultAuthenticationService;
import atm.services.impl.DefaultTransactionService;
import atm.services.impl.InMemoryBankService;
import atm.states.ATMState;
import atm.states.IdleState;
import atm.states.OutOfServiceState;
import atm.strategies.*;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Main ATM class - Facade that orchestrates all ATM operations.
 * Provides a simplified interface for ATM interactions.
 */
public class ATM {
    
    private final String atmId;
    private final String location;
    private final ReentrantLock operationLock = new ReentrantLock();
    
    // Components
    private final CashDispenser cashDispenser;
    private final BankService bankService;
    private final AuthenticationService authenticationService;
    private final TransactionService transactionService;
    
    // State management
    private ATMState currentState;
    private Card currentCard;
    private Account currentAccount;
    private TransactionType selectedTransactionType;
    private Map<Denomination, Integer> lastDispensedNotes;
    
    // Strategies
    private final Map<TransactionType, TransactionStrategy> transactionStrategies;
    
    // Observers
    private final List<TransactionObserver> observers = new ArrayList<>();

    public ATM(String atmId, String location) {
        this.atmId = atmId;
        this.location = location;
        
        // Initialize components
        this.cashDispenser = new CashDispenser();
        this.bankService = new InMemoryBankService();
        this.authenticationService = new DefaultAuthenticationService(bankService);
        this.transactionService = new DefaultTransactionService();
        
        // Initialize strategies
        this.transactionStrategies = new EnumMap<>(TransactionType.class);
        registerDefaultStrategies();
        
        // Set initial state
        this.currentState = new IdleState(this);
    }

    /**
     * Constructor with dependency injection for testing.
     */
    public ATM(String atmId, String location, BankService bankService,
               AuthenticationService authenticationService, TransactionService transactionService) {
        this.atmId = atmId;
        this.location = location;
        this.cashDispenser = new CashDispenser();
        this.bankService = bankService;
        this.authenticationService = authenticationService;
        this.transactionService = transactionService;
        
        this.transactionStrategies = new EnumMap<>(TransactionType.class);
        registerDefaultStrategies();
        
        this.currentState = new IdleState(this);
    }

    private void registerDefaultStrategies() {
        transactionStrategies.put(TransactionType.BALANCE_INQUIRY, new BalanceInquiryStrategy());
        transactionStrategies.put(TransactionType.WITHDRAWAL, new WithdrawalStrategy());
        transactionStrategies.put(TransactionType.DEPOSIT, new DepositStrategy());
        transactionStrategies.put(TransactionType.MINI_STATEMENT, new MiniStatementStrategy());
    }

    // === State Operations ===

    public void setState(ATMState state) {
        this.currentState = state;
    }

    public ATMState getState() {
        return currentState;
    }

    // === Card Operations ===

    public void insertCard(Card card) {
        operationLock.lock();
        try {
            currentState.insertCard(card);
        } finally {
            operationLock.unlock();
        }
    }

    public void enterPin(String pin) {
        operationLock.lock();
        try {
            currentState.enterPin(pin);
        } finally {
            operationLock.unlock();
        }
    }

    public void ejectCard() {
        this.currentCard = null;
        this.currentAccount = null;
        this.selectedTransactionType = null;
        this.lastDispensedNotes = null;
        System.out.println("Card ejected.");
    }

    // === Transaction Operations ===

    public void selectTransaction(TransactionType type) {
        operationLock.lock();
        try {
            currentState.selectTransaction(type);
        } finally {
            operationLock.unlock();
        }
    }

    public void enterAmount(BigDecimal amount) {
        operationLock.lock();
        try {
            currentState.enterAmount(amount);
        } finally {
            operationLock.unlock();
        }
    }

    public void confirmTransaction() {
        operationLock.lock();
        try {
            currentState.confirmTransaction();
        } finally {
            operationLock.unlock();
        }
    }

    public void cancel() {
        operationLock.lock();
        try {
            currentState.cancel();
        } finally {
            operationLock.unlock();
        }
    }

    // === Quick Operations ===

    /**
     * Quick withdrawal - combines amount entry and confirmation.
     */
    public void quickWithdraw(BigDecimal amount) {
        selectTransaction(TransactionType.WITHDRAWAL);
        enterAmount(amount);
        confirmTransaction();
    }

    /**
     * Check balance - quick balance inquiry.
     */
    public void checkBalance() {
        selectTransaction(TransactionType.BALANCE_INQUIRY);
        confirmTransaction();
    }

    // === Cash Management ===

    public void loadCash(Denomination denomination, int count) {
        cashDispenser.loadCash(denomination, count);
    }

    public void displayCashInventory() {
        System.out.println("\n" + cashDispenser.toString());
    }

    // === Receipt Generation ===

    public Receipt generateReceipt(Transaction transaction) {
        return new Receipt(
            atmId,
            location,
            transaction,
            currentCard != null ? currentCard.getMaskedCardNumber() : "N/A",
            lastDispensedNotes
        );
    }

    // === Observer Management ===

    public void addObserver(TransactionObserver observer) {
        observers.add(observer);
    }

    public void removeObserver(TransactionObserver observer) {
        observers.remove(observer);
    }

    public void notifyTransactionComplete(Transaction transaction) {
        for (TransactionObserver observer : observers) {
            observer.onTransactionComplete(transaction);
        }
    }

    public void notifyTransactionFailed(Transaction transaction) {
        for (TransactionObserver observer : observers) {
            observer.onTransactionFailed(transaction);
        }
    }

    // === Service Methods ===

    public void markOutOfService(String reason) {
        this.currentState = new OutOfServiceState(this, reason);
    }

    public void markInService() {
        if (!cashDispenser.needsRefill()) {
            this.currentState = new IdleState(this);
        } else {
            throw new ATMException("Cannot mark in service: Cash dispenser needs refill");
        }
    }

    public String getDisplayMessage() {
        return currentState.getDisplayMessage();
    }

    // === Strategy Management ===

    public TransactionStrategy getTransactionStrategy(TransactionType type) {
        TransactionStrategy strategy = transactionStrategies.get(type);
        if (strategy == null) {
            throw new ATMException("Unsupported transaction type: " + type);
        }
        return strategy;
    }

    public void registerTransactionStrategy(TransactionType type, TransactionStrategy strategy) {
        transactionStrategies.put(type, strategy);
    }

    // === Getters and Setters ===

    public String getAtmId() {
        return atmId;
    }

    public String getLocation() {
        return location;
    }

    public Card getCurrentCard() {
        return currentCard;
    }

    public void setCurrentCard(Card card) {
        this.currentCard = card;
    }

    public Account getCurrentAccount() {
        return currentAccount;
    }

    public void setCurrentAccount(Account account) {
        this.currentAccount = account;
    }

    public TransactionType getSelectedTransactionType() {
        return selectedTransactionType;
    }

    public void setSelectedTransactionType(TransactionType type) {
        this.selectedTransactionType = type;
    }

    public Map<Denomination, Integer> getLastDispensedNotes() {
        return lastDispensedNotes;
    }

    public void setLastDispensedNotes(Map<Denomination, Integer> notes) {
        this.lastDispensedNotes = notes;
    }

    public CashDispenser getCashDispenser() {
        return cashDispenser;
    }

    public BankService getBankService() {
        return bankService;
    }

    public AuthenticationService getAuthenticationService() {
        return authenticationService;
    }

    public TransactionService getTransactionService() {
        return transactionService;
    }

    @Override
    public String toString() {
        return "ATM{" +
               "atmId='" + atmId + '\'' +
               ", location='" + location + '\'' +
               ", state=" + currentState.getStateType() +
               ", totalCash=₹" + cashDispenser.getTotalCash() +
               '}';
    }
}



