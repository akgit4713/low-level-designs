package snakeladder.enums;

/**
 * Represents the result of a player's move.
 */
public enum MoveResult {
    NORMAL("Normal Move"),
    SNAKE_BITE("Snake Bite! 🐍"),
    LADDER_CLIMB("Ladder Climb! 🪜"),
    WON("Winner! 🏆"),
    NO_MOVE("No Move (exceeded board)"),
    EXTRA_TURN("Extra Turn (rolled 6)");

    private final String description;

    MoveResult(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}



