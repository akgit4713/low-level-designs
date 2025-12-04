package vendingmachine.enums;

/**
 * Enum representing note denominations accepted by the vending machine.
 * Each note has a value in rupees.
 */
public enum Note {
    RUPEE_10(10),
    RUPEE_20(20),
    RUPEE_50(50),
    RUPEE_100(100),
    RUPEE_200(200),
    RUPEE_500(500);

    private final int value;

    Note(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "₹" + value + " Note";
    }
}
