package atm.models;

import atm.enums.Denomination;
import atm.exceptions.CashDispenserException;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Manages cash inventory in the ATM.
 * Thread-safe for concurrent dispensing operations.
 */
public class CashInventory {
    
    private final Map<Denomination, Integer> inventory;
    private final ReentrantLock lock = new ReentrantLock();

    public CashInventory() {
        this.inventory = new ConcurrentHashMap<>();
        for (Denomination denomination : Denomination.values()) {
            inventory.put(denomination, 0);
        }
    }

    /**
     * Load cash into the ATM.
     */
    public void loadCash(Denomination denomination, int count) {
        if (count < 0) {
            throw new IllegalArgumentException("Count cannot be negative");
        }
        lock.lock();
        try {
            inventory.merge(denomination, count, Integer::sum);
        } finally {
            lock.unlock();
        }
    }

    /**
     * Get count of notes for a denomination.
     */
    public int getNoteCount(Denomination denomination) {
        return inventory.getOrDefault(denomination, 0);
    }

    /**
     * Get total cash available in ATM.
     */
    public BigDecimal getTotalCash() {
        lock.lock();
        try {
            long total = 0;
            for (Map.Entry<Denomination, Integer> entry : inventory.entrySet()) {
                total += (long) entry.getKey().getValue() * entry.getValue();
            }
            return BigDecimal.valueOf(total);
        } finally {
            lock.unlock();
        }
    }

    /**
     * Check if ATM can dispense the requested amount.
     */
    public boolean canDispense(BigDecimal amount) {
        lock.lock();
        try {
            return calculateDispense(amount.intValue()) != null;
        } finally {
            lock.unlock();
        }
    }

    /**
     * Calculate notes to dispense for given amount.
     * Uses greedy algorithm with highest denominations first.
     * @return Map of denomination to count, or null if cannot dispense
     */
    public Map<Denomination, Integer> calculateDispense(int amount) {
        if (amount <= 0 || amount % 100 != 0) {
            return null; // Amount must be positive and multiple of 100
        }

        Map<Denomination, Integer> result = new EnumMap<>(Denomination.class);
        int remaining = amount;

        for (Denomination denomination : Denomination.getDescending()) {
            int available = inventory.getOrDefault(denomination, 0);
            if (available > 0 && denomination.getValue() <= remaining) {
                int needed = remaining / denomination.getValue();
                int toDispense = Math.min(needed, available);
                if (toDispense > 0) {
                    result.put(denomination, toDispense);
                    remaining -= toDispense * denomination.getValue();
                }
            }
        }

        if (remaining > 0) {
            return null; // Cannot dispense exact amount
        }
        return result;
    }

    /**
     * Dispense cash from inventory.
     * @return Map of denomination to count dispensed
     * @throws CashDispenserException if cannot dispense
     */
    public Map<Denomination, Integer> dispense(BigDecimal amount) {
        lock.lock();
        try {
            Map<Denomination, Integer> toDispense = calculateDispense(amount.intValue());
            if (toDispense == null) {
                throw new CashDispenserException(
                    "Cannot dispense amount: " + amount,
                    amount,
                    getTotalCash()
                );
            }

            // Deduct from inventory
            for (Map.Entry<Denomination, Integer> entry : toDispense.entrySet()) {
                inventory.merge(entry.getKey(), -entry.getValue(), Integer::sum);
            }

            return toDispense;
        } finally {
            lock.unlock();
        }
    }

    /**
     * Accept deposit and add to inventory.
     */
    public void acceptDeposit(Map<Denomination, Integer> notes) {
        lock.lock();
        try {
            for (Map.Entry<Denomination, Integer> entry : notes.entrySet()) {
                if (entry.getValue() > 0) {
                    inventory.merge(entry.getKey(), entry.getValue(), Integer::sum);
                }
            }
        } finally {
            lock.unlock();
        }
    }

    /**
     * Get current inventory status.
     */
    public Map<Denomination, Integer> getInventoryStatus() {
        lock.lock();
        try {
            return new EnumMap<>(inventory);
        } finally {
            lock.unlock();
        }
    }

    /**
     * Check if ATM needs refill (any denomination below threshold).
     */
    public boolean needsRefill(int threshold) {
        for (Integer count : inventory.values()) {
            if (count < threshold) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("CashInventory{\n");
        for (Denomination denomination : Denomination.getDescending()) {
            sb.append("  ₹").append(denomination.getValue())
              .append(": ").append(inventory.get(denomination))
              .append(" notes\n");
        }
        sb.append("  Total: ₹").append(getTotalCash()).append("\n}");
        return sb.toString();
    }
}



