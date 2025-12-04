package vendingmachine.enums;

/**
 * Enum representing coin denominations accepted by the vending machine.
 * Each coin has a value in rupees.
 */
public enum Coin {
    RUPEE_1(1),
    RUPEE_2(2),
    RUPEE_5(5),
    RUPEE_10(10);

    private final int value;

    Coin(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "₹" + value + " Coin";
    }
}
