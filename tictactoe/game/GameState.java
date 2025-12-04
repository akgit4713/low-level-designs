package tictactoe.game;

/**
 * Enum representing the current state of the game.
 */
public enum GameState {
    NOT_STARTED("Game has not started yet"),
    IN_PROGRESS("Game is in progress"),
    DRAW("Game ended in a draw"),
    WIN("Game has a winner");

    private final String description;

    GameState(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public boolean isGameOver() {
        return this == DRAW || this == WIN;
    }
}

