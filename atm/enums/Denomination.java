package atm.enums;

/**
 * Cash denominations available in the ATM.
 * Ordered from highest to lowest for optimal dispensing.
 */
public enum Denomination {
    NOTE_2000(2000),
    NOTE_500(500),
    NOTE_200(200),
    NOTE_100(100);

    private final int value;

    Denomination(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    /**
     * Get denominations in descending order for greedy dispensing algorithm.
     */
    public static Denomination[] getDescending() {
        return new Denomination[]{NOTE_2000, NOTE_500, NOTE_200, NOTE_100};
    }
}



