package atm.services.impl;

import atm.enums.AccountType;
import atm.models.Account;
import atm.models.Card;
import atm.services.BankService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory implementation of BankService for testing/demo purposes.
 * In production, this would connect to actual bank backend.
 */
public class InMemoryBankService implements BankService {
    
    private final Map<String, Card> cards = new ConcurrentHashMap<>();
    private final Map<String, Account> accounts = new ConcurrentHashMap<>();
    private final Map<String, String> cardPins = new ConcurrentHashMap<>();
    private final Map<String, String> cardToAccount = new ConcurrentHashMap<>();

    public InMemoryBankService() {
        // Initialize with sample data
        initializeSampleData();
    }

    private void initializeSampleData() {
        // Sample Customer 1: John Doe
        Card card1 = new Card(
            "4111111111111111",
            "John Doe",
            LocalDate.now().plusYears(3),
            "HDFC"
        );
        Account account1 = new Account(
            "ACC001",
            card1.getCardNumber(),
            AccountType.SAVINGS,
            new BigDecimal("50000.00"),
            new BigDecimal("25000.00")
        );
        cards.put(card1.getCardNumber(), card1);
        accounts.put(account1.getAccountNumber(), account1);
        cardPins.put(card1.getCardNumber(), "1234");
        cardToAccount.put(card1.getCardNumber(), account1.getAccountNumber());

        // Sample Customer 2: Jane Smith
        Card card2 = new Card(
            "4222222222222222",
            "Jane Smith",
            LocalDate.now().plusYears(2),
            "ICICI"
        );
        Account account2 = new Account(
            "ACC002",
            card2.getCardNumber(),
            AccountType.CURRENT,
            new BigDecimal("150000.00"),
            new BigDecimal("50000.00")
        );
        cards.put(card2.getCardNumber(), card2);
        accounts.put(account2.getAccountNumber(), account2);
        cardPins.put(card2.getCardNumber(), "5678");
        cardToAccount.put(card2.getCardNumber(), account2.getAccountNumber());

        // Sample Customer 3: Bob Wilson (low balance for testing)
        Card card3 = new Card(
            "4333333333333333",
            "Bob Wilson",
            LocalDate.now().plusYears(1),
            "SBI"
        );
        Account account3 = new Account(
            "ACC003",
            card3.getCardNumber(),
            AccountType.SAVINGS,
            new BigDecimal("500.00"),
            new BigDecimal("10000.00")
        );
        cards.put(card3.getCardNumber(), card3);
        accounts.put(account3.getAccountNumber(), account3);
        cardPins.put(card3.getCardNumber(), "0000");
        cardToAccount.put(card3.getCardNumber(), account3.getAccountNumber());
    }

    @Override
    public boolean validateCard(String cardNumber) {
        Card card = cards.get(cardNumber);
        return card != null && !card.isBlocked() && !card.isExpired();
    }

    @Override
    public Card getCard(String cardNumber) {
        return cards.get(cardNumber);
    }

    @Override
    public boolean validatePin(String cardNumber, String pin) {
        String storedPin = cardPins.get(cardNumber);
        return storedPin != null && storedPin.equals(pin);
    }

    @Override
    public Account getAccount(String cardNumber) {
        String accountNumber = cardToAccount.get(cardNumber);
        if (accountNumber != null) {
            return accounts.get(accountNumber);
        }
        return null;
    }

    @Override
    public boolean debit(String accountNumber, BigDecimal amount) {
        Account account = accounts.get(accountNumber);
        if (account != null) {
            return account.debit(amount);
        }
        return false;
    }

    @Override
    public boolean credit(String accountNumber, BigDecimal amount) {
        Account account = accounts.get(accountNumber);
        if (account != null) {
            return account.credit(amount);
        }
        return false;
    }

    @Override
    public void blockCard(String cardNumber) {
        Card card = cards.get(cardNumber);
        if (card != null) {
            card.block();
        }
    }

    @Override
    public BigDecimal getBalance(String accountNumber) {
        Account account = accounts.get(accountNumber);
        return account != null ? account.getBalance() : BigDecimal.ZERO;
    }

    /**
     * Add a new card for testing.
     */
    public void addCard(Card card, String pin, Account account) {
        cards.put(card.getCardNumber(), card);
        accounts.put(account.getAccountNumber(), account);
        cardPins.put(card.getCardNumber(), pin);
        cardToAccount.put(card.getCardNumber(), account.getAccountNumber());
    }
}



