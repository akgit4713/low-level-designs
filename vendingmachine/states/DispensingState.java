package vendingmachine.states;

import vendingmachine.VendingMachine;
import vendingmachine.enums.Coin;
import vendingmachine.enums.Note;
import vendingmachine.exceptions.InvalidStateException;
import vendingmachine.models.Product;

import java.util.Map;

/**
 * Dispensing State - Product is being dispensed.
 * This is a transient state that immediately dispenses and returns to Idle.
 * 
 * Valid Operations:
 * - dispenseProduct: Dispense the selected product and return change
 * 
 * Invalid Operations (during dispensing):
 * - insertCoin: Cannot accept during dispense
 * - insertNote: Cannot accept during dispense
 * - selectProduct: Already dispensing
 * - cancelTransaction: Too late to cancel
 */
public class DispensingState implements VendingMachineState {
    
    private final VendingMachine machine;

    public DispensingState(VendingMachine machine) {
        this.machine = machine;
    }

    @Override
    public void insertCoin(Coin coin) {
        throw new InvalidStateException(getStateName(), "insertCoin - Please wait, dispensing in progress");
    }

    @Override
    public void insertNote(Note note) {
        throw new InvalidStateException(getStateName(), "insertNote - Please wait, dispensing in progress");
    }

    @Override
    public void selectProduct(String productCode) {
        throw new InvalidStateException(getStateName(), "selectProduct - Already dispensing");
    }

    @Override
    public void cancelTransaction() {
        throw new InvalidStateException(getStateName(), "cancelTransaction - Too late to cancel, dispensing in progress");
    }

    @Override
    public void dispenseProduct() {
        Product product = machine.getSelectedProduct();
        int balance = machine.getCurrentBalance();
        int price = product.getPrice();
        int changeAmount = balance - price;

        // Display dispensing information
        System.out.println();
        System.out.println("╔═══════════════════════════════════════╗");
        System.out.println("║         DISPENSING PRODUCT            ║");
        System.out.println("╠═══════════════════════════════════════╣");
        System.out.printf("║  Product: %-27s ║%n", product.getName());
        System.out.printf("║  Price:   ₹%-26d ║%n", price);
        System.out.printf("║  Paid:    ₹%-26d ║%n", balance);
        System.out.println("╠═══════════════════════════════════════╣");

        // Calculate and dispense change
        if (changeAmount > 0) {
            System.out.printf("║  Change:  ₹%-26d ║%n", changeAmount);
            try {
                Map<String, Integer> change = machine.getCashInventory().calculateChange(changeAmount);
                for (Map.Entry<String, Integer> entry : change.entrySet()) {
                    System.out.printf("║    %-21s × %-10d ║%n", entry.getKey(), entry.getValue());
                }
            } catch (Exception e) {
                System.out.println("║  ⚠ Change unavailable. Issuing credit. ║");
            }
        } else {
            System.out.println("║  Change:  ₹0 (exact amount paid)      ║");
        }

        // Dispense the product
        machine.getProductInventory().dispense(product.getCode());
        
        System.out.println("╠═══════════════════════════════════════╣");
        System.out.println("║  ✓ Please collect your product!       ║");
        System.out.println("╚═══════════════════════════════════════╝");
        System.out.println();

        // Reset machine state
        machine.resetBalance();
        machine.setSelectedProduct(null);
        machine.setState(machine.getIdleState());
    }

    @Override
    public String getStateName() {
        return "DISPENSING";
    }
}
