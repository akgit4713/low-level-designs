package movierating.models;

/**
 * Enum representing valid rating values (1-5 stars).
 * Encapsulates the allowed rating values to maintain consistency.
 */
public enum RatingValue {
    ONE_STAR(1),
    TWO_STARS(2),
    THREE_STARS(3),
    FOUR_STARS(4),
    FIVE_STARS(5);

    private final int value;

    RatingValue(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static RatingValue fromInt(int value) {
        for (RatingValue rv : values()) {
            if (rv.value == value) {
                return rv;
            }
        }
        throw new IllegalArgumentException("Invalid rating value: " + value + ". Must be between 1 and 5.");
    }

    @Override
    public String toString() {
        return "★".repeat(value) + "☆".repeat(5 - value);
    }
}


