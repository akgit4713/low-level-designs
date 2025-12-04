package trafficsignal.enums;

/**
 * Represents the direction of roads at an intersection.
 */
public enum Direction {
    NORTH("North"),
    SOUTH("South"),
    EAST("East"),
    WEST("West");

    private final String displayName;

    Direction(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    /**
     * Returns the opposite direction (used for pairing signals).
     */
    public Direction getOpposite() {
        return switch (this) {
            case NORTH -> SOUTH;
            case SOUTH -> NORTH;
            case EAST -> WEST;
            case WEST -> EAST;
        };
    }

    /**
     * Checks if this direction is perpendicular to another.
     */
    public boolean isPerpendicularTo(Direction other) {
        return (this == NORTH || this == SOUTH) && (other == EAST || other == WEST)
                || (this == EAST || this == WEST) && (other == NORTH || other == SOUTH);
    }
}



