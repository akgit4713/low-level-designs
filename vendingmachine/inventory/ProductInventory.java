package vendingmachine.inventory;

import vendingmachine.exceptions.InvalidProductException;
import vendingmachine.exceptions.OutOfStockException;
import vendingmachine.models.ItemSlot;
import vendingmachine.models.Product;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages the product inventory in the vending machine.
 * Thread-safe using ConcurrentHashMap.
 */
public class ProductInventory {
    
    private final Map<String, ItemSlot> slots;

    public ProductInventory() {
        this.slots = new ConcurrentHashMap<>();
    }

    /**
     * Adds a new product to the inventory.
     * 
     * @param product the product to add
     * @param quantity the initial quantity
     */
    public void addProduct(Product product, int quantity) {
        String code = product.getCode();
        if (slots.containsKey(code)) {
            slots.get(code).addStock(quantity);
        } else {
            slots.put(code, new ItemSlot(product, quantity));
        }
    }

    /**
     * Gets a product by its code.
     * 
     * @param code the product code
     * @return the product
     * @throws InvalidProductException if the product doesn't exist
     */
    public Product getProduct(String code) {
        ItemSlot slot = getSlot(code);
        return slot.getProduct();
    }

    /**
     * Gets an item slot by product code.
     * 
     * @param code the product code
     * @return the item slot
     * @throws InvalidProductException if the product doesn't exist
     */
    public ItemSlot getSlot(String code) {
        String normalizedCode = code.toUpperCase();
        ItemSlot slot = slots.get(normalizedCode);
        if (slot == null) {
            throw new InvalidProductException(code);
        }
        return slot;
    }

    /**
     * Checks if a product is available (exists and has stock).
     * 
     * @param code the product code
     * @return true if available
     */
    public boolean isAvailable(String code) {
        try {
            ItemSlot slot = getSlot(code);
            return slot.isAvailable();
        } catch (InvalidProductException e) {
            return false;
        }
    }

    /**
     * Dispenses a product, reducing its quantity.
     * 
     * @param code the product code
     * @return the dispensed product
     * @throws InvalidProductException if the product doesn't exist
     * @throws OutOfStockException if the product is out of stock
     */
    public Product dispense(String code) {
        ItemSlot slot = getSlot(code);
        return slot.dispense();
    }

    /**
     * Restocks a product with additional quantity.
     * 
     * @param code the product code
     * @param quantity the quantity to add
     * @throws InvalidProductException if the product doesn't exist
     */
    public void restock(String code, int quantity) {
        ItemSlot slot = getSlot(code);
        slot.addStock(quantity);
    }

    /**
     * Gets all available products.
     * 
     * @return list of all item slots
     */
    public List<ItemSlot> getAllSlots() {
        return new ArrayList<>(slots.values());
    }

    /**
     * Checks if a product exists in the inventory.
     * 
     * @param code the product code
     * @return true if exists
     */
    public boolean hasProduct(String code) {
        return slots.containsKey(code.toUpperCase());
    }

    /**
     * Gets the quantity of a product.
     * 
     * @param code the product code
     * @return the quantity
     * @throws InvalidProductException if the product doesn't exist
     */
    public int getQuantity(String code) {
        return getSlot(code).getQuantity();
    }
}
