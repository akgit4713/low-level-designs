package vendingmachine.inventory;

import vendingmachine.enums.Coin;
import vendingmachine.enums.Note;
import vendingmachine.exceptions.InsufficientChangeException;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages the cash (coins and notes) in the vending machine.
 * Handles change calculation using a greedy algorithm.
 * Thread-safe using ConcurrentHashMap.
 */
public class CashInventory {
    
    private final Map<Coin, Integer> coins;
    private final Map<Note, Integer> notes;

    public CashInventory() {
        this.coins = new ConcurrentHashMap<>();
        this.notes = new ConcurrentHashMap<>();
        
        // Initialize with zero counts
        for (Coin coin : Coin.values()) {
            coins.put(coin, 0);
        }
        for (Note note : Note.values()) {
            notes.put(note, 0);
        }
    }

    /**
     * Adds a coin to the inventory.
     * 
     * @param coin the coin to add
     */
    public synchronized void addCoin(Coin coin) {
        coins.merge(coin, 1, Integer::sum);
    }

    /**
     * Adds a note to the inventory.
     * 
     * @param note the note to add
     */
    public synchronized void addNote(Note note) {
        notes.merge(note, 1, Integer::sum);
    }

    /**
     * Adds multiple coins of the same type.
     * 
     * @param coin the coin type
     * @param count the number of coins
     */
    public synchronized void addCoins(Coin coin, int count) {
        if (count > 0) {
            coins.merge(coin, count, Integer::sum);
        }
    }

    /**
     * Adds multiple notes of the same type.
     * 
     * @param note the note type
     * @param count the number of notes
     */
    public synchronized void addNotes(Note note, int count) {
        if (count > 0) {
            notes.merge(note, count, Integer::sum);
        }
    }

    /**
     * Calculates and returns change for the given amount.
     * Uses a greedy algorithm (largest denominations first).
     * 
     * @param amount the change amount needed
     * @return map of denomination name to count
     * @throws InsufficientChangeException if exact change cannot be provided
     */
    public synchronized Map<String, Integer> calculateChange(int amount) {
        if (amount == 0) {
            return Collections.emptyMap();
        }
        if (amount < 0) {
            throw new IllegalArgumentException("Change amount cannot be negative");
        }

        Map<String, Integer> change = new LinkedHashMap<>();
        int remaining = amount;

        // Create a sorted list of all denominations (highest first)
        List<DenominationInfo> allDenominations = new ArrayList<>();
        
        for (Note note : Note.values()) {
            allDenominations.add(new DenominationInfo(note.toString(), note.getValue(), notes.get(note), true));
        }
        for (Coin coin : Coin.values()) {
            allDenominations.add(new DenominationInfo(coin.toString(), coin.getValue(), coins.get(coin), false));
        }
        
        // Sort by value descending
        allDenominations.sort((a, b) -> Integer.compare(b.value, a.value));

        // Temporary storage for change to be given
        Map<Note, Integer> notesUsed = new EnumMap<>(Note.class);
        Map<Coin, Integer> coinsUsed = new EnumMap<>(Coin.class);

        for (DenominationInfo denom : allDenominations) {
            if (remaining <= 0) break;
            if (denom.count == 0) continue;

            int needed = remaining / denom.value;
            int available = denom.count;
            int use = Math.min(needed, available);

            if (use > 0) {
                remaining -= use * denom.value;
                change.put(denom.name, use);
                
                // Track which denominations we're using
                if (denom.isNote) {
                    Note note = getNoteByValue(denom.value);
                    if (note != null) notesUsed.put(note, use);
                } else {
                    Coin coin = getCoinByValue(denom.value);
                    if (coin != null) coinsUsed.put(coin, use);
                }
            }
        }

        if (remaining > 0) {
            throw new InsufficientChangeException(amount);
        }

        // Deduct the change from inventory
        for (Map.Entry<Note, Integer> entry : notesUsed.entrySet()) {
            notes.merge(entry.getKey(), -entry.getValue(), Integer::sum);
        }
        for (Map.Entry<Coin, Integer> entry : coinsUsed.entrySet()) {
            coins.merge(entry.getKey(), -entry.getValue(), Integer::sum);
        }

        return change;
    }

    /**
     * Gets the total cash value in the inventory.
     * 
     * @return total value in rupees
     */
    public synchronized int getTotalValue() {
        int total = 0;
        for (Map.Entry<Coin, Integer> entry : coins.entrySet()) {
            total += entry.getKey().getValue() * entry.getValue();
        }
        for (Map.Entry<Note, Integer> entry : notes.entrySet()) {
            total += entry.getKey().getValue() * entry.getValue();
        }
        return total;
    }

    /**
     * Collects all cash from the inventory.
     * 
     * @return the total amount collected
     */
    public synchronized int collectAll() {
        int total = getTotalValue();
        coins.replaceAll((k, v) -> 0);
        notes.replaceAll((k, v) -> 0);
        return total;
    }

    /**
     * Initializes the inventory with default change for operations.
     */
    public synchronized void initializeDefaultChange() {
        // Add some coins for change
        addCoins(Coin.RUPEE_10, 20);
        addCoins(Coin.RUPEE_5, 20);
        addCoins(Coin.RUPEE_2, 20);
        addCoins(Coin.RUPEE_1, 20);
        
        // Add some small notes
        addNotes(Note.RUPEE_10, 10);
        addNotes(Note.RUPEE_20, 10);
    }

    /**
     * Gets coin count for a specific denomination.
     */
    public int getCoinCount(Coin coin) {
        return coins.getOrDefault(coin, 0);
    }

    /**
     * Gets note count for a specific denomination.
     */
    public int getNoteCount(Note note) {
        return notes.getOrDefault(note, 0);
    }

    private Note getNoteByValue(int value) {
        for (Note note : Note.values()) {
            if (note.getValue() == value) return note;
        }
        return null;
    }

    private Coin getCoinByValue(int value) {
        for (Coin coin : Coin.values()) {
            if (coin.getValue() == value) return coin;
        }
        return null;
    }

    /**
     * Helper class to store denomination information.
     */
    private static class DenominationInfo {
        final String name;
        final int value;
        final int count;
        final boolean isNote;

        DenominationInfo(String name, int value, int count, boolean isNote) {
            this.name = name;
            this.value = value;
            this.count = count;
            this.isNote = isNote;
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Cash Inventory:\n");
        sb.append("Notes:\n");
        for (Note note : Note.values()) {
            int count = notes.get(note);
            if (count > 0) {
                sb.append(String.format("  %s × %d%n", note, count));
            }
        }
        sb.append("Coins:\n");
        for (Coin coin : Coin.values()) {
            int count = coins.get(coin);
            if (count > 0) {
                sb.append(String.format("  %s × %d%n", coin, count));
            }
        }
        sb.append(String.format("Total: ₹%d%n", getTotalValue()));
        return sb.toString();
    }
}
