package vendingmachine.states;

import vendingmachine.VendingMachine;
import vendingmachine.enums.Coin;
import vendingmachine.enums.Note;
import vendingmachine.exceptions.InvalidStateException;

/**
 * Idle State - Machine is waiting for money to be inserted.
 * 
 * Valid Operations:
 * - insertCoin: Accept and transition to HasMoneyState
 * - insertNote: Accept and transition to HasMoneyState
 * 
 * Invalid Operations:
 * - selectProduct: No money inserted
 * - cancelTransaction: Nothing to cancel
 * - dispenseProduct: No product selected
 */
public class IdleState implements VendingMachineState {
    
    private final VendingMachine machine;

    public IdleState(VendingMachine machine) {
        this.machine = machine;
    }

    @Override
    public void insertCoin(Coin coin) {
        machine.addToBalance(coin.getValue());
        machine.getCashInventory().addCoin(coin);
        System.out.println("✓ Inserted: " + coin);
        System.out.println("  Current balance: ₹" + machine.getCurrentBalance());
        
        // Transition to HasMoneyState
        machine.setState(machine.getHasMoneyState());
    }

    @Override
    public void insertNote(Note note) {
        machine.addToBalance(note.getValue());
        machine.getCashInventory().addNote(note);
        System.out.println("✓ Inserted: " + note);
        System.out.println("  Current balance: ₹" + machine.getCurrentBalance());
        
        // Transition to HasMoneyState
        machine.setState(machine.getHasMoneyState());
    }

    @Override
    public void selectProduct(String productCode) {
        throw new InvalidStateException(getStateName(), "selectProduct - Please insert money first");
    }

    @Override
    public void cancelTransaction() {
        throw new InvalidStateException(getStateName(), "cancelTransaction - No transaction in progress");
    }

    @Override
    public void dispenseProduct() {
        throw new InvalidStateException(getStateName(), "dispenseProduct - No product selected");
    }

    @Override
    public String getStateName() {
        return "IDLE";
    }
}
