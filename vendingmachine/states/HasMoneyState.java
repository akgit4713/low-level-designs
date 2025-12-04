package vendingmachine.states;

import vendingmachine.VendingMachine;
import vendingmachine.enums.Coin;
import vendingmachine.enums.Note;
import vendingmachine.exceptions.InsufficientFundsException;
import vendingmachine.exceptions.OutOfStockException;
import vendingmachine.models.Product;

/**
 * Has Money State - Money has been inserted, waiting for product selection.
 * 
 * Valid Operations:
 * - insertCoin: Accept more money
 * - insertNote: Accept more money
 * - selectProduct: Select a product (if sufficient funds)
 * - cancelTransaction: Refund all money and return to Idle
 * 
 * Invalid Operations:
 * - dispenseProduct: Must select product first
 */
public class HasMoneyState implements VendingMachineState {
    
    private final VendingMachine machine;

    public HasMoneyState(VendingMachine machine) {
        this.machine = machine;
    }

    @Override
    public void insertCoin(Coin coin) {
        machine.addToBalance(coin.getValue());
        machine.getCashInventory().addCoin(coin);
        System.out.println("✓ Inserted: " + coin);
        System.out.println("  Current balance: ₹" + machine.getCurrentBalance());
    }

    @Override
    public void insertNote(Note note) {
        machine.addToBalance(note.getValue());
        machine.getCashInventory().addNote(note);
        System.out.println("✓ Inserted: " + note);
        System.out.println("  Current balance: ₹" + machine.getCurrentBalance());
    }

    @Override
    public void selectProduct(String productCode) {
        // Check if product exists and is in stock
        if (!machine.getProductInventory().isAvailable(productCode)) {
            if (machine.getProductInventory().hasProduct(productCode)) {
                throw new OutOfStockException(productCode);
            }
        }

        Product product = machine.getProductInventory().getProduct(productCode);
        int balance = machine.getCurrentBalance();

        // Check if sufficient funds
        if (balance < product.getPrice()) {
            throw new InsufficientFundsException(product.getPrice(), balance);
        }

        // Set the selected product and transition to dispensing
        machine.setSelectedProduct(product);
        System.out.println("✓ Selected: " + product);
        
        // Transition to DispensingState
        machine.setState(machine.getDispensingState());
        
        // Auto-trigger dispensing
        machine.dispenseProduct();
    }

    @Override
    public void cancelTransaction() {
        int balance = machine.getCurrentBalance();
        System.out.println("✓ Transaction cancelled. Refunding ₹" + balance);
        
        // Calculate and return the refund
        if (balance > 0) {
            try {
                var change = machine.getCashInventory().calculateChange(balance);
                System.out.println("  Refund breakdown:");
                for (var entry : change.entrySet()) {
                    System.out.println("    " + entry.getKey() + " × " + entry.getValue());
                }
            } catch (Exception e) {
                System.out.println("  ⚠ Could not provide exact change. Issuing refund voucher for ₹" + balance);
            }
        }
        
        // Reset balance and return to idle
        machine.resetBalance();
        machine.setSelectedProduct(null);
        machine.setState(machine.getIdleState());
    }

    @Override
    public void dispenseProduct() {
        System.out.println("⚠ Please select a product first");
    }

    @Override
    public String getStateName() {
        return "HAS_MONEY";
    }
}
