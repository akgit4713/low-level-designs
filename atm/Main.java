package atm;

import atm.enums.Denomination;
import atm.enums.TransactionType;
import atm.exceptions.ATMException;
import atm.exceptions.AuthenticationException;
import atm.exceptions.TransactionException;
import atm.models.Card;
import atm.observers.AuditLogObserver;
import atm.observers.SMSNotificationObserver;
import atm.services.impl.InMemoryBankService;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Demonstration of the ATM System.
 */
public class Main {

    public static void main(String[] args) {
        System.out.println("╔═══════════════════════════════════════════════════════════════╗");
        System.out.println("║                    ATM SYSTEM DEMO                            ║");
        System.out.println("╚═══════════════════════════════════════════════════════════════╝\n");

        // Initialize ATM
        ATM atm = new ATM("ATM-001", "Main Street Branch, Mumbai");
        
        // Load cash into ATM
        System.out.println("--- Loading Cash into ATM ---");
        atm.loadCash(Denomination.NOTE_2000, 50);
        atm.loadCash(Denomination.NOTE_500, 100);
        atm.loadCash(Denomination.NOTE_200, 100);
        atm.loadCash(Denomination.NOTE_100, 200);
        atm.displayCashInventory();

        // Add observers
        atm.addObserver(new SMSNotificationObserver());
        atm.addObserver(new AuditLogObserver());

        // Display ATM info
        System.out.println("\n" + atm);
        System.out.println("\n" + atm.getDisplayMessage());

        // === Demo 1: Successful Cash Withdrawal ===
        System.out.println("\n" + "═".repeat(65));
        System.out.println("DEMO 1: Successful Cash Withdrawal");
        System.out.println("═".repeat(65));
        
        try {
            // Get card from bank service (simulating card insertion)
            InMemoryBankService bankService = (InMemoryBankService) atm.getBankService();
            Card johnCard = bankService.getCard("4111111111111111");
            
            // Insert card
            System.out.println("\n→ Inserting card...");
            atm.insertCard(johnCard);
            
            // Enter PIN
            System.out.println("→ Entering PIN: 1234");
            atm.enterPin("1234");
            
            // Check balance first
            System.out.println("\n→ Checking balance...");
            atm.checkBalance();
            
            // Withdraw cash
            System.out.println("\n→ Withdrawing ₹5,000...");
            atm.quickWithdraw(new BigDecimal("5000"));
            
            // Perform another transaction
            System.out.println("\n→ Checking balance again...");
            atm.checkBalance();
            
            // Eject card
            System.out.println("\n→ Ejecting card...");
            atm.cancel();
            
        } catch (ATMException e) {
            System.out.println("✗ Error: " + e.getMessage());
        }

        // === Demo 2: Cash Deposit ===
        System.out.println("\n" + "═".repeat(65));
        System.out.println("DEMO 2: Cash Deposit");
        System.out.println("═".repeat(65));
        
        try {
            InMemoryBankService bankService = (InMemoryBankService) atm.getBankService();
            Card janeCard = bankService.getCard("4222222222222222");
            
            System.out.println("\n→ Inserting card...");
            atm.insertCard(janeCard);
            
            System.out.println("→ Entering PIN: 5678");
            atm.enterPin("5678");
            
            System.out.println("\n→ Checking initial balance...");
            atm.checkBalance();
            
            System.out.println("\n→ Depositing ₹10,000...");
            atm.selectTransaction(TransactionType.DEPOSIT);
            atm.enterAmount(new BigDecimal("10000"));
            atm.confirmTransaction();
            
            System.out.println("\n→ Checking balance after deposit...");
            atm.checkBalance();
            
            atm.cancel();
            
        } catch (ATMException e) {
            System.out.println("✗ Error: " + e.getMessage());
        }

        // === Demo 3: Insufficient Funds ===
        System.out.println("\n" + "═".repeat(65));
        System.out.println("DEMO 3: Insufficient Funds Scenario");
        System.out.println("═".repeat(65));
        
        try {
            InMemoryBankService bankService = (InMemoryBankService) atm.getBankService();
            Card bobCard = bankService.getCard("4333333333333333"); // Low balance account
            
            System.out.println("\n→ Inserting card (Low balance account)...");
            atm.insertCard(bobCard);
            
            System.out.println("→ Entering PIN: 0000");
            atm.enterPin("0000");
            
            System.out.println("\n→ Checking balance...");
            atm.checkBalance();
            
            System.out.println("\n→ Attempting to withdraw ₹1,000 (balance is only ₹500)...");
            atm.quickWithdraw(new BigDecimal("1000"));
            
        } catch (TransactionException e) {
            System.out.println("✗ Transaction failed: " + e.getMessage());
            System.out.println("→ Returning to main menu...");
            atm.cancel();
        } catch (ATMException e) {
            System.out.println("✗ Error: " + e.getMessage());
            atm.cancel();
        }

        // === Demo 4: Wrong PIN ===
        System.out.println("\n" + "═".repeat(65));
        System.out.println("DEMO 4: Wrong PIN Attempts");
        System.out.println("═".repeat(65));
        
        try {
            // Create a new card for this demo
            Card testCard = new Card(
                "4444444444444444",
                "Test User",
                LocalDate.now().plusYears(2),
                "TEST"
            );
            
            System.out.println("\n→ Inserting card...");
            atm.insertCard(testCard);
            
            System.out.println("→ Entering wrong PIN: 9999");
            try {
                atm.enterPin("9999");
            } catch (AuthenticationException e) {
                System.out.println("✗ " + e.getMessage());
            }
            
            System.out.println("→ Entering wrong PIN again: 8888");
            try {
                atm.enterPin("8888");
            } catch (AuthenticationException e) {
                System.out.println("✗ " + e.getMessage());
            }
            
            System.out.println("→ Entering wrong PIN third time: 7777");
            try {
                atm.enterPin("7777");
            } catch (AuthenticationException e) {
                System.out.println("✗ " + e.getMessage());
                if (e.isCardBlocked()) {
                    System.out.println("⚠ Card has been blocked!");
                }
            }
            
        } catch (ATMException e) {
            System.out.println("✗ Error: " + e.getMessage());
        }

        // === Demo 5: Mini Statement ===
        System.out.println("\n" + "═".repeat(65));
        System.out.println("DEMO 5: Mini Statement");
        System.out.println("═".repeat(65));
        
        try {
            InMemoryBankService bankService = (InMemoryBankService) atm.getBankService();
            Card johnCard = bankService.getCard("4111111111111111");
            
            System.out.println("\n→ Inserting card...");
            atm.insertCard(johnCard);
            
            System.out.println("→ Entering PIN: 1234");
            atm.enterPin("1234");
            
            System.out.println("\n→ Requesting mini statement...");
            atm.selectTransaction(TransactionType.MINI_STATEMENT);
            atm.confirmTransaction();
            
            atm.cancel();
            
        } catch (ATMException e) {
            System.out.println("✗ Error: " + e.getMessage());
        }

        // Final ATM status
        System.out.println("\n" + "═".repeat(65));
        System.out.println("FINAL ATM STATUS");
        System.out.println("═".repeat(65));
        atm.displayCashInventory();
        System.out.println("\n" + atm);

        System.out.println("\n╔═══════════════════════════════════════════════════════════════╗");
        System.out.println("║                  DEMO COMPLETED SUCCESSFULLY                  ║");
        System.out.println("╚═══════════════════════════════════════════════════════════════╝");
    }
}



