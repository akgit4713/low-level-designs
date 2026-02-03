package atm.dispenser;

import atm.enums.Denomination;
import atm.exceptions.CashDispenserException;
import atm.models.CashInventory;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Cash Dispenser component that manages ATM's cash inventory and dispensing.
 * Uses Chain of Responsibility pattern for denomination-based dispensing.
 */
public class CashDispenser {
    
    private final CashInventory cashInventory;
    private DenominationHandler handlerChain;

    public CashDispenser() {
        this.cashInventory = new CashInventory();
        initializeHandlerChain();
    }

    /**
     * Initialize the chain of responsibility for dispensing.
     * Denominations are processed from highest to lowest.
     */
    private void initializeHandlerChain() {
        // Build chain: 2000 -> 500 -> 200 -> 100
        DenominationHandler handler100 = new DenominationHandler(Denomination.NOTE_100, cashInventory, null);
        DenominationHandler handler200 = new DenominationHandler(Denomination.NOTE_200, cashInventory, handler100);
        DenominationHandler handler500 = new DenominationHandler(Denomination.NOTE_500, cashInventory, handler200);
        this.handlerChain = new DenominationHandler(Denomination.NOTE_2000, cashInventory, handler500);
    }

    /**
     * Load cash into the dispenser.
     */
    public void loadCash(Denomination denomination, int count) {
        cashInventory.loadCash(denomination, count);
        System.out.println("  Loaded " + count + " notes of ₹" + denomination.getValue());
    }

    /**
     * Check if dispenser can dispense the requested amount.
     */
    public boolean canDispense(BigDecimal amount) {
        return cashInventory.canDispense(amount);
    }

    /**
     * Dispense the requested amount.
     * @return Map of denomination to count dispensed
     * @throws CashDispenserException if cannot dispense
     */
    public Map<Denomination, Integer> dispense(BigDecimal amount) {

        if (!canDispense(amount)) {
            throw new CashDispenserException(
                "Cannot dispense ₹" + amount + ". Please try a different amount.",
                amount,
                getTotalCash()
            );
        }
        Map<Denomination, Integer>  denominationIntegerMap = handlerChain.dispense(amount.intValue());
        cashInventory.updateInventory(denominationIntegerMap);
        return  denominationIntegerMap;
    }

    /**
     * Get total cash available.
     */
    public BigDecimal getTotalCash() {
        return cashInventory.getTotalCash();
    }

    /**
     * Get inventory status.
     */
    public Map<Denomination, Integer> getInventoryStatus() {
        return cashInventory.getInventoryStatus();
    }

    /**
     * Check if ATM needs refill.
     */
    public boolean needsRefill() {
        return cashInventory.needsRefill(10); // Threshold of 10 notes per denomination
    }

    /**
     * Accept cash deposit.
     */
    public void acceptDeposit(Map<Denomination, Integer> notes) {
        cashInventory.acceptDeposit(notes);
    }

    @Override
    public String toString() {
        return cashInventory.toString();
    }
}



