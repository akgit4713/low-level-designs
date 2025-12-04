package atm.dispenser;

import atm.enums.Denomination;
import atm.models.CashInventory;

import java.util.EnumMap;
import java.util.Map;

/**
 * Chain of Responsibility handler for each denomination.
 * Each handler processes its denomination and passes remaining to next handler.
 */
public class DenominationHandler {
    
    private final Denomination denomination;
    private final CashInventory inventory;
    private final DenominationHandler nextHandler;

    public DenominationHandler(Denomination denomination, CashInventory inventory, 
                                DenominationHandler nextHandler) {
        this.denomination = denomination;
        this.inventory = inventory;
        this.nextHandler = nextHandler;
    }

    /**
     * Handle dispensing for this denomination.
     * @param amount Remaining amount to dispense
     * @param result Map to accumulate dispensed notes
     * @return Remaining amount after this handler
     */
    public int handle(int amount, Map<Denomination, Integer> result) {
        if (amount <= 0) {
            return 0;
        }

        int denominationValue = denomination.getValue();
        int available = inventory.getNoteCount(denomination);
        
        if (available > 0 && denominationValue <= amount) {
            int needed = amount / denominationValue;
            int toDispense = Math.min(needed, available);
            
            if (toDispense > 0) {
                result.put(denomination, toDispense);
                amount -= toDispense * denominationValue;
            }
        }

        // Pass remaining to next handler
        if (nextHandler != null && amount > 0) {
            return nextHandler.handle(amount, result);
        }

        return amount;
    }

    /**
     * Start dispensing chain.
     * @param amount Total amount to dispense
     * @return Map of denomination to count, or null if cannot dispense exactly
     */
    public Map<Denomination, Integer> dispense(int amount) {
        Map<Denomination, Integer> result = new EnumMap<>(Denomination.class);
        int remaining = handle(amount, result);
        
        if (remaining > 0) {
            return null; // Cannot dispense exact amount
        }
        return result;
    }

    public Denomination getDenomination() {
        return denomination;
    }
}



