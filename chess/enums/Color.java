package chess.enums;

/**
 * Represents the color of a chess piece or player.
 */
public enum Color {
    WHITE("White"),
    BLACK("Black");

    private final String displayName;

    Color(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    /**
     * Returns the opposite color.
     */
    public Color opposite() {
        return this == WHITE ? BLACK : WHITE;
    }

    /**
     * Returns the starting row for pieces of this color.
     * White pieces start at row 0, Black at row 7.
     */
    public int getBackRank() {
        return this == WHITE ? 0 : 7;
    }

    /**
     * Returns the starting row for pawns of this color.
     * White pawns start at row 1, Black at row 6.
     */
    public int getPawnRank() {
        return this == WHITE ? 1 : 6;
    }

    /**
     * Returns the direction pawns move for this color.
     * White pawns move up (+1), Black pawns move down (-1).
     */
    public int getPawnDirection() {
        return this == WHITE ? 1 : -1;
    }

    /**
     * Returns the promotion rank for pawns of this color.
     */
    public int getPromotionRank() {
        return this == WHITE ? 7 : 0;
    }
}


