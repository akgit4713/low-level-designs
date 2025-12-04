package vendingmachine;

import vendingmachine.enums.Coin;
import vendingmachine.enums.Note;
import vendingmachine.exceptions.*;
import vendingmachine.models.Product;

/**
 * Main driver class demonstrating the Vending Machine functionality.
 * Shows various scenarios including:
 * - Normal purchase flow
 * - Transaction cancellation
 * - Error handling (insufficient funds, out of stock)
 * - Admin operations (restocking, cash collection)
 */
public class Main {

    public static void main(String[] args) {
        printHeader();

        // Reset for fresh start
        VendingMachine.resetInstance();
        VendingMachine machine = VendingMachine.getInstance();

        // Setup phase
        setupProducts(machine);
        machine.initializeDefaultCash();
        machine.displayProducts();
        machine.displayStatus();

        // Demo scenarios
        demoSuccessfulPurchase(machine);
        demoCancelTransaction(machine);
        demoInsufficientFunds(machine);
        demoOutOfStock(machine);
        demoExactPayment(machine);
        demoAdminOperations(machine);

        printFooter();
    }

    private static void printHeader() {
        System.out.println();
        System.out.println("╔═══════════════════════════════════════════════════════════════╗");
        System.out.println("║           VENDING MACHINE SYSTEM DEMO                         ║");
        System.out.println("║                                                               ║");
        System.out.println("║   Design Patterns: State, Singleton                           ║");
        System.out.println("║   Thread-safe: Yes                                            ║");
        System.out.println("╚═══════════════════════════════════════════════════════════════╝");
        System.out.println();
    }

    private static void printFooter() {
        System.out.println();
        System.out.println("╔═══════════════════════════════════════════════════════════════╗");
        System.out.println("║           DEMO COMPLETED SUCCESSFULLY                         ║");
        System.out.println("╚═══════════════════════════════════════════════════════════════╝");
        System.out.println();
    }

    private static void setupProducts(VendingMachine machine) {
        System.out.println("=== SETTING UP PRODUCTS ===\n");

        machine.addProduct(new Product("A1", "Coca Cola", 40), 5);
        machine.addProduct(new Product("A2", "Pepsi", 35), 4);
        machine.addProduct(new Product("B1", "Lays Classic", 20), 6);
        machine.addProduct(new Product("B2", "Doritos", 25), 3);
        machine.addProduct(new Product("C1", "KitKat", 15), 8);
        machine.addProduct(new Product("C2", "Snickers", 30), 1);  // Low stock for demo
        
        System.out.println();
    }

    private static void demoSuccessfulPurchase(VendingMachine machine) {
        printScenario("SCENARIO 1: Successful Purchase with Change");

        System.out.println("Customer inserts ₹50 note and selects Coca Cola (₹40)");
        System.out.println("Expected: Product dispensed, ₹10 change returned\n");

        machine.insertNote(Note.RUPEE_50);
        machine.selectProduct("A1");

        machine.displayStatus();
    }

    private static void demoCancelTransaction(VendingMachine machine) {
        printScenario("SCENARIO 2: Cancel Transaction and Refund");

        System.out.println("Customer inserts money but decides to cancel");
        System.out.println("Expected: Full refund returned\n");

        machine.insertNote(Note.RUPEE_100);
        machine.insertCoin(Coin.RUPEE_10);
        System.out.println("\nCustomer changes mind...\n");
        machine.cancelTransaction();

        machine.displayStatus();
    }

    private static void demoInsufficientFunds(VendingMachine machine) {
        printScenario("SCENARIO 3: Insufficient Funds");

        System.out.println("Customer tries to buy Coca Cola (₹40) with only ₹20");
        System.out.println("Expected: InsufficientFundsException\n");

        try {
            machine.insertNote(Note.RUPEE_20);
            machine.selectProduct("A1");
        } catch (InsufficientFundsException e) {
            System.out.println("✗ Error: " + e.getMessage());
            System.out.println("  Required: ₹" + e.getRequired());
            System.out.println("  Available: ₹" + e.getAvailable());
            System.out.println("  Need: ₹" + e.getShortfall() + " more\n");
            
            // Customer adds more money
            System.out.println("Customer adds ₹20 more...\n");
            machine.insertNote(Note.RUPEE_20);
            machine.selectProduct("A1");
        }

        machine.displayStatus();
    }

    private static void demoOutOfStock(VendingMachine machine) {
        printScenario("SCENARIO 4: Out of Stock Product");

        System.out.println("Buying the last Snickers and trying to buy another");
        System.out.println("Expected: OutOfStockException on second attempt\n");

        // Buy the last Snickers
        machine.insertNote(Note.RUPEE_50);
        machine.selectProduct("C2");

        // Try to buy another
        System.out.println("\nTrying to buy another Snickers...\n");
        try {
            machine.insertNote(Note.RUPEE_50);
            machine.selectProduct("C2");
        } catch (OutOfStockException e) {
            System.out.println("✗ Error: " + e.getMessage());
            System.out.println("  Product code: " + e.getProductCode());
            
            // Cancel and refund
            machine.cancelTransaction();
        }

        machine.displayProducts();
    }

    private static void demoExactPayment(VendingMachine machine) {
        printScenario("SCENARIO 5: Exact Payment (No Change)");

        System.out.println("Customer pays exactly ₹15 for KitKat (₹15)");
        System.out.println("Expected: Product dispensed, no change\n");

        machine.insertCoin(Coin.RUPEE_10);
        machine.insertCoin(Coin.RUPEE_5);
        machine.selectProduct("C1");

        machine.displayStatus();
    }

    private static void demoAdminOperations(VendingMachine machine) {
        printScenario("SCENARIO 6: Admin Operations");

        System.out.println("Admin restocks products and collects cash\n");

        // Restock the sold-out Snickers
        machine.restockProduct("C2", 10);
        
        // Display updated products
        machine.displayProducts();
        
        // Show final status
        machine.displayStatus();
        
        // Collect cash
        System.out.println("\n--- Collecting Cash ---");
        int collected = machine.collectCash();
        System.out.println("Total collected: ₹" + collected);
        
        machine.displayStatus();
    }

    private static void printScenario(String title) {
        System.out.println();
        System.out.println("════════════════════════════════════════════════════════════════");
        System.out.println("  " + title);
        System.out.println("════════════════════════════════════════════════════════════════");
        System.out.println();
    }
}
