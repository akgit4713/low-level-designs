package vendingmachine;

import vendingmachine.enums.Coin;
import vendingmachine.enums.Note;
import vendingmachine.inventory.CashInventory;
import vendingmachine.inventory.ProductInventory;
import vendingmachine.models.ItemSlot;
import vendingmachine.models.Product;
import vendingmachine.states.*;

/**
 * Main Vending Machine class using Singleton pattern.
 * 
 * Design Patterns Used:
 * 1. Singleton Pattern - Single instance for the vending machine
 * 2. State Pattern - Behavior changes based on machine state
 * 
 * Thread Safety:
 * - Double-checked locking for singleton
 * - Synchronized methods for all public operations
 * - ConcurrentHashMap for internal data structures
 */
public class VendingMachine {
    
    private static volatile VendingMachine instance;
    
    // Inventory
    private final ProductInventory productInventory;
    private final CashInventory cashInventory;
    
    // State management
    private final VendingMachineState idleState;
    private final VendingMachineState hasMoneyState;
    private final VendingMachineState dispensingState;
    private VendingMachineState currentState;
    
    // Transaction state
    private int currentBalance;
    private Product selectedProduct;

    /**
     * Private constructor for singleton pattern.
     */
    private VendingMachine() {
        this.productInventory = new ProductInventory();
        this.cashInventory = new CashInventory();
        
        // Initialize states
        this.idleState = new IdleState(this);
        this.hasMoneyState = new HasMoneyState(this);
        this.dispensingState = new DispensingState(this);
        
        // Start in idle state
        this.currentState = idleState;
        this.currentBalance = 0;
        this.selectedProduct = null;
    }

    /**
     * Gets the singleton instance with double-checked locking.
     * 
     * @return the vending machine instance
     */
    public static VendingMachine getInstance() {
        if (instance == null) {
            synchronized (VendingMachine.class) {
                if (instance == null) {
                    instance = new VendingMachine();
                }
            }
        }
        return instance;
    }

    /**
     * Resets the singleton instance.
     * Useful for testing.
     */
    public static synchronized void resetInstance() {
        instance = null;
    }

    // ==================== User Operations ====================

    /**
     * Inserts a coin into the machine.
     * 
     * @param coin the coin to insert
     */
    public synchronized void insertCoin(Coin coin) {
        currentState.insertCoin(coin);
    }

    /**
     * Inserts a note into the machine.
     * 
     * @param note the note to insert
     */
    public synchronized void insertNote(Note note) {
        currentState.insertNote(note);
    }

    /**
     * Selects a product by code.
     * 
     * @param productCode the product code
     */
    public synchronized void selectProduct(String productCode) {
        currentState.selectProduct(productCode);
    }

    /**
     * Cancels the current transaction and refunds money.
     */
    public synchronized void cancelTransaction() {
        currentState.cancelTransaction();
    }

    /**
     * Triggers product dispensing.
     */
    public synchronized void dispenseProduct() {
        currentState.dispenseProduct();
    }

    // ==================== Admin Operations ====================

    /**
     * Adds a new product to the machine.
     * 
     * @param product the product to add
     * @param quantity the quantity to add
     */
    public synchronized void addProduct(Product product, int quantity) {
        productInventory.addProduct(product, quantity);
        System.out.println("✓ Added product: " + product + " (qty: " + quantity + ")");
    }

    /**
     * Restocks an existing product.
     * 
     * @param productCode the product code
     * @param quantity the quantity to add
     */
    public synchronized void restockProduct(String productCode, int quantity) {
        productInventory.restock(productCode, quantity);
        System.out.println("✓ Restocked: " + productCode + " +" + quantity + " units");
    }

    /**
     * Collects all cash from the machine.
     * 
     * @return the total amount collected
     */
    public synchronized int collectCash() {
        int total = cashInventory.collectAll();
        System.out.println("✓ Collected: ₹" + total);
        return total;
    }

    /**
     * Initializes the machine with default change.
     */
    public synchronized void initializeDefaultCash() {
        cashInventory.initializeDefaultChange();
        System.out.println("✓ Initialized default cash for change");
    }

    /**
     * Displays all available products.
     */
    public synchronized void displayProducts() {
        System.out.println("\n╔═══════════════════════════════════════════════════╗");
        System.out.println("║              AVAILABLE PRODUCTS                    ║");
        System.out.println("╠═══════════════════════════════════════════════════╣");
        
        for (ItemSlot slot : productInventory.getAllSlots()) {
            String status = slot.isAvailable() ? "✓" : "✗";
            System.out.printf("║ %s %-46s ║%n", status, slot);
        }
        
        System.out.println("╚═══════════════════════════════════════════════════╝\n");
    }

    /**
     * Displays the current machine status.
     */
    public synchronized void displayStatus() {
        System.out.println("\n╔═══════════════════════════════════════════════════╗");
        System.out.println("║              MACHINE STATUS                        ║");
        System.out.println("╠═══════════════════════════════════════════════════╣");
        System.out.printf("║ State:   %-40s ║%n", currentState.getStateName());
        System.out.printf("║ Balance: ₹%-38d ║%n", currentBalance);
        System.out.printf("║ Cash:    ₹%-38d ║%n", cashInventory.getTotalValue());
        System.out.println("╚═══════════════════════════════════════════════════╝\n");
    }

    // ==================== State Management ====================

    public VendingMachineState getIdleState() {
        return idleState;
    }

    public VendingMachineState getHasMoneyState() {
        return hasMoneyState;
    }

    public VendingMachineState getDispensingState() {
        return dispensingState;
    }

    public void setState(VendingMachineState state) {
        this.currentState = state;
    }

    public String getCurrentStateName() {
        return currentState.getStateName();
    }

    // ==================== Balance Management ====================

    public int getCurrentBalance() {
        return currentBalance;
    }

    public void addToBalance(int amount) {
        this.currentBalance += amount;
    }

    public void resetBalance() {
        this.currentBalance = 0;
    }

    // ==================== Product Management ====================

    public Product getSelectedProduct() {
        return selectedProduct;
    }

    public void setSelectedProduct(Product product) {
        this.selectedProduct = product;
    }

    // ==================== Inventory Access ====================

    public ProductInventory getProductInventory() {
        return productInventory;
    }

    public CashInventory getCashInventory() {
        return cashInventory;
    }
}
