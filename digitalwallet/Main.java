package digitalwallet;

import digitalwallet.enums.Currency;
import digitalwallet.models.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Demonstration of the Digital Wallet System.
 */
public class Main {
    
    public static void main(String[] args) {
        printHeader("DIGITAL WALLET SYSTEM DEMO");

        // Initialize the digital wallet system
        DigitalWallet digitalWallet = new DigitalWallet();

        // ==================== User Creation ====================
        printSection("1. Creating Users");
        
        User alice = digitalWallet.createUser(
            "Alice Smith", "alice@example.com", "+1234567890", "1234"
        );
        System.out.println("✓ Created user: " + alice);
        
        User bob = digitalWallet.createUser(
            "Bob Jones", "bob@example.com", "+0987654321", "5678"
        );
        System.out.println("✓ Created user: " + bob);

        User charlie = digitalWallet.createUser(
            "Charlie Brown", "charlie@example.com", "+1122334455", "9012"
        );
        System.out.println("✓ Created user: " + charlie);

        // ==================== Payment Methods ====================
        printSection("2. Adding Payment Methods");
        
        CreditCard aliceCard = digitalWallet.addCreditCard(
            alice.getId(), "4111111111111111", "12/25", "123", "Alice Smith"
        );
        System.out.println("✓ Added credit card: " + aliceCard);
        
        BankAccount aliceBank = digitalWallet.addBankAccount(
            alice.getId(), "Chase Bank", "021000021", "1234567890", 
            "Checking", "Alice Smith"
        );
        System.out.println("✓ Added bank account: " + aliceBank);
        
        CreditCard bobCard = digitalWallet.addCreditCard(
            bob.getId(), "5555555555554444", "06/26", "456", "Bob Jones"
        );
        System.out.println("✓ Added credit card: " + bobCard);

        // ==================== Deposits ====================
        printSection("3. Depositing Funds");
        
        Transaction deposit1 = digitalWallet.deposit(
            alice.getId(), new BigDecimal("1000.00"), Currency.USD, aliceCard.getId()
        );
        System.out.println("✓ Deposited $1000 to Alice's wallet");
        
        Transaction deposit2 = digitalWallet.deposit(
            bob.getId(), new BigDecimal("500.00"), Currency.USD, bobCard.getId()
        );
        System.out.println("✓ Deposited $500 to Bob's wallet");
        
        // Deposit in different currency
        Transaction deposit3 = digitalWallet.deposit(
            alice.getId(), new BigDecimal("500.00"), Currency.EUR, aliceCard.getId()
        );
        System.out.println("✓ Deposited €500 to Alice's wallet");

        // Show balances
        printSection("4. Current Balances");
        printBalances(digitalWallet, alice.getId(), "Alice");
        printBalances(digitalWallet, bob.getId(), "Bob");
        printBalances(digitalWallet, charlie.getId(), "Charlie");

        // ==================== P2P Transfer ====================
        printSection("5. P2P Transfer: Alice → Bob ($250)");
        
        Transfer transfer1 = digitalWallet.transfer(
            alice.getId(), bob.getId(), new BigDecimal("250.00"), 
            Currency.USD, "Birthday gift"
        );
        System.out.println("✓ Transfer completed: " + transfer1);
        System.out.println("  Fee charged: " + Currency.USD.format(transfer1.getFee()));
        
        printBalances(digitalWallet, alice.getId(), "Alice");
        printBalances(digitalWallet, bob.getId(), "Bob");

        // ==================== Cross-Currency Transfer ====================
        printSection("6. Cross-Currency Transfer: Alice (EUR) → Charlie (USD)");
        
        // Show exchange rate first
        ExchangeRate rate = digitalWallet.getExchangeRate(Currency.EUR, Currency.USD);
        System.out.println("Exchange rate: " + rate);
        
        BigDecimal euroAmount = new BigDecimal("100.00");
        BigDecimal expectedUsd = digitalWallet.convertCurrency(euroAmount, Currency.EUR, Currency.USD);
        System.out.println("Converting €100 ≈ " + Currency.USD.format(expectedUsd));
        
        Transfer transfer2 = digitalWallet.transferWithConversion(
            alice.getId(), charlie.getId(), euroAmount,
            Currency.EUR, Currency.USD, "Payment for services"
        );
        System.out.println("✓ Transfer completed: " + transfer2);
        System.out.println("  Sent: €" + euroAmount);
        System.out.println("  Received: " + Currency.USD.format(transfer2.getConvertedAmount()));
        
        printBalances(digitalWallet, alice.getId(), "Alice");
        printBalances(digitalWallet, charlie.getId(), "Charlie");

        // ==================== Currency Exchange ====================
        printSection("7. Currency Exchange: Alice USD → GBP");
        
        BigDecimal usdToExchange = new BigDecimal("200.00");
        ExchangeRate usdGbpRate = digitalWallet.getExchangeRate(Currency.USD, Currency.GBP);
        System.out.println("Exchange rate: " + usdGbpRate);
        
        Transfer exchange = digitalWallet.exchangeCurrency(
            alice.getId(), usdToExchange, Currency.USD, Currency.GBP
        );
        System.out.println("✓ Exchange completed");
        System.out.println("  Exchanged: " + Currency.USD.format(usdToExchange));
        System.out.println("  Received: " + Currency.GBP.format(exchange.getConvertedAmount()));
        
        printBalances(digitalWallet, alice.getId(), "Alice");

        // ==================== Withdrawal ====================
        printSection("8. Withdrawal: Bob → Bank Account");
        
        BankAccount bobBank = digitalWallet.addBankAccount(
            bob.getId(), "Bank of America", "026009593", "9876543210",
            "Savings", "Bob Jones"
        );
        
        Transfer withdrawal = digitalWallet.withdraw(
            bob.getId(), new BigDecimal("100.00"), Currency.USD, bobBank.getId()
        );
        System.out.println("✓ Withdrawal completed: " + withdrawal);
        
        printBalances(digitalWallet, bob.getId(), "Bob");

        // ==================== Transaction History ====================
        printSection("9. Transaction History: Alice");
        
        var transactions = digitalWallet.getTransactions(alice.getId());
        System.out.println("Total transactions: " + transactions.size());
        for (Transaction tx : transactions) {
            String sign = tx.isCredit() ? "+" : "-";
            System.out.printf("  %s | %s%s | %s%n",
                tx.getCreatedAt().toLocalTime(),
                sign, tx.getCurrency().format(tx.getAmount()),
                tx.getType().getDisplayName()
            );
        }

        // ==================== Statement Generation ====================
        printSection("10. Transaction Statement: Alice");
        
        TransactionStatement statement = digitalWallet.getStatement(
            alice.getId(),
            LocalDateTime.now().minusDays(1),
            LocalDateTime.now().plusDays(1)
        );
        System.out.println(statement.getSummary());

        // ==================== Error Handling Demo ====================
        printSection("11. Error Handling Demo");
        
        // Try transfer with insufficient balance
        System.out.println("Attempting transfer with insufficient funds...");
        try {
            digitalWallet.transfer(
                charlie.getId(), alice.getId(), new BigDecimal("10000.00"),
                Currency.USD, "Large transfer"
            );
        } catch (Exception e) {
            System.out.println("✗ Transfer failed: " + e.getMessage());
        }

        // Try invalid card number
        System.out.println("\nAttempting to add invalid card...");
        try {
            digitalWallet.addCreditCard(
                charlie.getId(), "1234567890123456", "12/25", "123", "Charlie Brown"
            );
        } catch (Exception e) {
            System.out.println("✗ Failed: " + e.getMessage());
        }

        // ==================== Final Balances ====================
        printSection("12. Final Balances");
        printBalances(digitalWallet, alice.getId(), "Alice");
        printBalances(digitalWallet, bob.getId(), "Bob");
        printBalances(digitalWallet, charlie.getId(), "Charlie");

        printFooter("DEMO COMPLETED SUCCESSFULLY");
    }

    private static void printHeader(String title) {
        System.out.println("\n╔═══════════════════════════════════════════════════════════╗");
        System.out.printf("║%s%s%s║%n", 
            " ".repeat((59 - title.length()) / 2),
            title,
            " ".repeat((60 - title.length()) / 2));
        System.out.println("╚═══════════════════════════════════════════════════════════╝\n");
    }

    private static void printFooter(String title) {
        System.out.println("\n╔═══════════════════════════════════════════════════════════╗");
        System.out.printf("║%s%s%s║%n", 
            " ".repeat((59 - title.length()) / 2),
            title,
            " ".repeat((60 - title.length()) / 2));
        System.out.println("╚═══════════════════════════════════════════════════════════╝");
    }

    private static void printSection(String title) {
        System.out.println("\n───────────────────────────────────────────────────────────");
        System.out.println("  " + title);
        System.out.println("───────────────────────────────────────────────────────────\n");
    }

    private static void printBalances(DigitalWallet wallet, String userId, String userName) {
        var balances = wallet.getAllBalances(userId);
        StringBuilder sb = new StringBuilder();
        sb.append(userName).append("'s balances: ");
        
        boolean first = true;
        for (var entry : balances.entrySet()) {
            if (entry.getValue().getAmount().compareTo(java.math.BigDecimal.ZERO) > 0) {
                if (!first) sb.append(", ");
                sb.append(entry.getKey().format(entry.getValue().getAmount()));
                first = false;
            }
        }
        
        if (first) {
            sb.append("(empty)");
        }
        
        System.out.println(sb);
    }
}



