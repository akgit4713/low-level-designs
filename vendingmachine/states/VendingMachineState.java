package vendingmachine.states;

import vendingmachine.enums.Coin;
import vendingmachine.enums.Note;

/**
 * Interface for the State Pattern in the vending machine.
 * Each state defines behavior for all possible operations.
 * 
 * State Pattern Benefits:
 * - Encapsulates state-specific behavior
 * - Makes state transitions explicit
 * - Eliminates complex conditional logic
 * - Easy to add new states without modifying existing code (OCP)
 */
public interface VendingMachineState {
    
    /**
     * Handles coin insertion in this state.
     * 
     * @param coin the coin being inserted
     */
    void insertCoin(Coin coin);

    /**
     * Handles note insertion in this state.
     * 
     * @param note the note being inserted
     */
    void insertNote(Note note);

    /**
     * Handles product selection in this state.
     * 
     * @param productCode the selected product code
     */
    void selectProduct(String productCode);

    /**
     * Handles transaction cancellation in this state.
     */
    void cancelTransaction();

    /**
     * Handles product dispensing in this state.
     */
    void dispenseProduct();

    /**
     * Gets the name of this state for display/logging.
     * 
     * @return the state name
     */
    String getStateName();
}
