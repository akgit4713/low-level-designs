package chess.enums;

/**
 * Represents the current status of a chess game.
 */
public enum GameStatus {
    NOT_STARTED("Not Started", false),
    IN_PROGRESS("In Progress", false),
    WHITE_WINS("White Wins by Checkmate", true),
    BLACK_WINS("Black Wins by Checkmate", true),
    STALEMATE("Draw by Stalemate", true),
    DRAW_BY_AGREEMENT("Draw by Agreement", true),
    DRAW_BY_REPETITION("Draw by Threefold Repetition", true),
    DRAW_BY_FIFTY_MOVES("Draw by Fifty-Move Rule", true),
    DRAW_BY_INSUFFICIENT_MATERIAL("Draw by Insufficient Material", true),
    WHITE_RESIGNS("Black Wins by Resignation", true),
    BLACK_RESIGNS("White Wins by Resignation", true);

    private final String description;
    private final boolean gameOver;

    GameStatus(String description, boolean gameOver) {
        this.description = description;
        this.gameOver = gameOver;
    }

    public String getDescription() {
        return description;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    /**
     * Checks if this status represents a win for any player.
     */
    public boolean isWin() {
        return this == WHITE_WINS || this == BLACK_WINS 
            || this == WHITE_RESIGNS || this == BLACK_RESIGNS;
    }

    /**
     * Checks if this status represents a draw.
     */
    public boolean isDraw() {
        return this == STALEMATE || this == DRAW_BY_AGREEMENT 
            || this == DRAW_BY_REPETITION || this == DRAW_BY_FIFTY_MOVES
            || this == DRAW_BY_INSUFFICIENT_MATERIAL;
    }

    /**
     * Returns the winner color, or null if no winner.
     */
    public Color getWinner() {
        return switch (this) {
            case WHITE_WINS, BLACK_RESIGNS -> Color.WHITE;
            case BLACK_WINS, WHITE_RESIGNS -> Color.BLACK;
            default -> null;
        };
    }
}


