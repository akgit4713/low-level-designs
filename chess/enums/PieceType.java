package chess.enums;

/**
 * Represents the type of a chess piece.
 */
public enum PieceType {
    KING("King", 'K', 0),       // Infinite value (game over if lost)
    QUEEN("Queen", 'Q', 9),
    ROOK("Rook", 'R', 5),
    BISHOP("Bishop", 'B', 3),
    KNIGHT("Knight", 'N', 3),
    PAWN("Pawn", 'P', 1);

    private final String displayName;
    private final char symbol;
    private final int value;

    PieceType(String displayName, char symbol, int value) {
        this.displayName = displayName;
        this.symbol = symbol;
        this.value = value;
    }

    public String getDisplayName() {
        return displayName;
    }

    /**
     * Returns the algebraic notation symbol for this piece.
     * Note: Pawns typically don't use a symbol in algebraic notation.
     */
    public char getSymbol() {
        return symbol;
    }

    /**
     * Returns the relative value of this piece for evaluation purposes.
     */
    public int getValue() {
        return value;
    }

    /**
     * Returns the Unicode chess symbol for display.
     */
    public char getUnicodeSymbol(Color color) {
        if (color == Color.WHITE) {
            return switch (this) {
                case KING -> '♔';
                case QUEEN -> '♕';
                case ROOK -> '♖';
                case BISHOP -> '♗';
                case KNIGHT -> '♘';
                case PAWN -> '♙';
            };
        } else {
            return switch (this) {
                case KING -> '♚';
                case QUEEN -> '♛';
                case ROOK -> '♜';
                case BISHOP -> '♝';
                case KNIGHT -> '♞';
                case PAWN -> '♟';
            };
        }
    }
}


