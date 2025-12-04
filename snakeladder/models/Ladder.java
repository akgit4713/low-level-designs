package snakeladder.models;

import snakeladder.exceptions.InvalidBoardConfigException;

/**
 * Represents a Ladder on the board.
 * Ladder moves player UP from base to top.
 */
public class Ladder implements BoardElement {
    
    private final int base;  // Starting position (lower number)
    private final int top;   // Ending position (higher number)
    private final String id;

    public Ladder(int base, int top) {
        validatePositions(base, top);
        this.base = base;
        this.top = top;
        this.id = "LADDER_" + base + "_" + top;
    }

    private void validatePositions(int base, int top) {
        if (base >= top) {
            throw new InvalidBoardConfigException(
                "Ladder base (" + base + ") must be less than top (" + top + ")");
        }
        if (base <= 0 || top <= 0) {
            throw new InvalidBoardConfigException(
                "Ladder positions must be positive. Base: " + base + ", Top: " + top);
        }
    }

    @Override
    public int getStartPosition() {
        return base;
    }

    @Override
    public int getEndPosition() {
        return top;
    }

    public int getBase() {
        return base;
    }

    public int getTop() {
        return top;
    }

    public String getId() {
        return id;
    }

    @Override
    public String getDescription() {
        return "🪜 Ladder from " + base + " to " + top + " (climbs up " + getPositionDelta() + " cells)";
    }

    @Override
    public String getType() {
        return "LADDER";
    }

    @Override
    public String toString() {
        return getDescription();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Ladder ladder = (Ladder) obj;
        return base == ladder.base && top == ladder.top;
    }

    @Override
    public int hashCode() {
        return 31 * base + top;
    }
}



