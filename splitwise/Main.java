package splitwise;

import splitwise.enums.SplitMethod;
import splitwise.models.*;

import java.math.BigDecimal;
import java.util.*;

/**
 * Demo application showcasing Splitwise functionality.
 */
public class Main {
    
    public static void main(String[] args) {
        System.out.println("╔════════════════════════════════════════════════════════════╗");
        System.out.println("║              SPLITWISE - Bill Splitting Demo               ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝\n");
        
        // Create Splitwise instance
        Splitwise splitwise = new Splitwise();
        
        // Enable console notifications
        splitwise.enableConsoleNotifications();
        
        // ============ 1. Register Users ============
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("STEP 1: Registering Users");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        
        User alice = splitwise.registerUser("Alice", "alice@email.com", "1234567890");
        User bob = splitwise.registerUser("Bob", "bob@email.com", "2345678901");
        User charlie = splitwise.registerUser("Charlie", "charlie@email.com", "3456789012");
        User diana = splitwise.registerUser("Diana", "diana@email.com", "4567890123");
        
        System.out.println("✓ Registered: " + alice.getName() + " (ID: " + alice.getId().substring(0, 8) + "...)");
        System.out.println("✓ Registered: " + bob.getName() + " (ID: " + bob.getId().substring(0, 8) + "...)");
        System.out.println("✓ Registered: " + charlie.getName() + " (ID: " + charlie.getId().substring(0, 8) + "...)");
        System.out.println("✓ Registered: " + diana.getName() + " (ID: " + diana.getId().substring(0, 8) + "...)");
        
        // ============ 2. Create Group ============
        System.out.println("\n━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("STEP 2: Creating Group & Adding Members");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        
        Group tripGroup = splitwise.createGroup("Weekend Trip", alice.getId());
        System.out.println("✓ Created group: " + tripGroup.getName());
        
        splitwise.addUserToGroup(tripGroup.getId(), bob.getId());
        splitwise.addUserToGroup(tripGroup.getId(), charlie.getId());
        splitwise.addUserToGroup(tripGroup.getId(), diana.getId());
        System.out.println("✓ Added Bob, Charlie, and Diana to the group");
        System.out.println("  Group members: " + tripGroup.getMemberCount());
        
        // ============ 3. Add Expenses with EQUAL Split ============
        System.out.println("\n━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("STEP 3: Adding Expense with EQUAL Split");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        
        List<String> allMembers = Arrays.asList(
                alice.getId(), bob.getId(), charlie.getId(), diana.getId()
        );
        
        Expense dinnerExpense = splitwise.addExpense(
                tripGroup.getId(),
                alice.getId(),
                new BigDecimal("120.00"),
                "Dinner at Italian Restaurant",
                allMembers
        );
        
        // ============ 4. Add Expense with PERCENTAGE Split ============
        System.out.println("\n━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("STEP 4: Adding Expense with PERCENTAGE Split");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        
        Map<String, BigDecimal> percentages = new HashMap<>();
        percentages.put(alice.getId(), new BigDecimal("40"));
        percentages.put(bob.getId(), new BigDecimal("30"));
        percentages.put(charlie.getId(), new BigDecimal("30"));
        
        List<String> hotelParticipants = Arrays.asList(
                alice.getId(), bob.getId(), charlie.getId()
        );
        
        Expense hotelExpense = splitwise.addExpense(
                tripGroup.getId(),
                bob.getId(),
                new BigDecimal("300.00"),
                "Hotel Room (2 nights)",
                hotelParticipants,
                SplitMethod.PERCENTAGE,
                percentages
        );
        
        // ============ 5. Add Expense with EXACT Split ============
        System.out.println("\n━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("STEP 5: Adding Expense with EXACT Split");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        
        Map<String, BigDecimal> exactAmounts = new HashMap<>();
        exactAmounts.put(bob.getId(), new BigDecimal("15.00"));
        exactAmounts.put(charlie.getId(), new BigDecimal("25.00"));
        exactAmounts.put(diana.getId(), new BigDecimal("10.00"));
        
        List<String> gasParticipants = Arrays.asList(
                bob.getId(), charlie.getId(), diana.getId()
        );
        
        Expense gasExpense = splitwise.addExpense(
                tripGroup.getId(),
                charlie.getId(),
                new BigDecimal("50.00"),
                "Gas for the trip",
                gasParticipants,
                SplitMethod.EXACT,
                exactAmounts
        );
        
        // ============ 6. View Balances ============
        System.out.println("\n━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("STEP 6: Viewing Balances");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        
        printUserBalances(splitwise, "Alice", alice);
        printUserBalances(splitwise, "Bob", bob);
        printUserBalances(splitwise, "Charlie", charlie);
        printUserBalances(splitwise, "Diana", diana);
        
        // ============ 7. Settle Up ============
        System.out.println("\n━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("STEP 7: Settling Balances");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        
        // Bob pays Alice what he owes
        BigDecimal bobOwesAlice = splitwise.getBalanceBetween(bob.getId(), alice.getId());
        if (bobOwesAlice.compareTo(BigDecimal.ZERO) > 0) {
            splitwise.settleBalance(bob.getId(), alice.getId(), bobOwesAlice);
            System.out.println("Bob paid Alice: $" + bobOwesAlice);
        }
        
        // ============ 8. View Updated Balances ============
        System.out.println("\n━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("STEP 8: Updated Balances After Settlement");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        
        printUserBalances(splitwise, "Alice", alice);
        printUserBalances(splitwise, "Bob", bob);
        
        // ============ 9. View Transaction History ============
        System.out.println("\n━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("STEP 9: Transaction History for Bob");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        
        List<Transaction> bobTransactions = splitwise.getUserTransactions(bob.getId());
        for (Transaction tx : bobTransactions) {
            System.out.println("  " + tx.getType() + ": $" + tx.getAmount() + 
                    " - " + tx.getDescription());
        }
        
        // ============ 10. View Group Expenses ============
        System.out.println("\n━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("STEP 10: All Group Expenses");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        
        List<Expense> groupExpenses = splitwise.getGroupExpenses(tripGroup.getId());
        for (Expense expense : groupExpenses) {
            System.out.println("  • " + expense.getDescription());
            System.out.println("    Amount: $" + expense.getAmount() + 
                    " | Method: " + expense.getSplitMethod() + 
                    " | Status: " + expense.getStatus());
        }
        
        System.out.println("\n╔════════════════════════════════════════════════════════════╗");
        System.out.println("║                    Demo Completed!                         ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝");
    }
    
    private static void printUserBalances(Splitwise splitwise, String name, User user) {
        Map<String, BigDecimal> balances = splitwise.getUserBalances(user.getId());
        BigDecimal totalOwed = splitwise.getTotalOwed(user.getId());
        BigDecimal totalOwedToUser = splitwise.getTotalOwedToUser(user.getId());
        
        System.out.println("\n📊 " + name + "'s Balances:");
        System.out.println("   Total owed by " + name + ": $" + totalOwed);
        System.out.println("   Total owed to " + name + ": $" + totalOwedToUser);
        
        if (!balances.isEmpty()) {
            System.out.println("   Details:");
            for (Map.Entry<String, BigDecimal> entry : balances.entrySet()) {
                String otherUserId = entry.getKey();
                BigDecimal amount = entry.getValue();
                if (amount.compareTo(BigDecimal.ZERO) > 0) {
                    System.out.println("     → Owes $" + amount + " to " + 
                            otherUserId.substring(0, 8) + "...");
                } else {
                    System.out.println("     ← Owed $" + amount.abs() + " by " + 
                            otherUserId.substring(0, 8) + "...");
                }
            }
        }
    }
}



