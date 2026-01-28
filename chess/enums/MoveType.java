package chess.enums;

/**
 * Represents the type of a chess move.
 */
public enum MoveType {
    NORMAL("Normal move"),
    CAPTURE("Capture"),
    CASTLING_KINGSIDE("Kingside castling (O-O)"),
    CASTLING_QUEENSIDE("Queenside castling (O-O-O)"),
    EN_PASSANT("En passant capture"),
    PAWN_DOUBLE_MOVE("Pawn double move"),
    PAWN_PROMOTION("Pawn promotion");

    private final String description;

    MoveType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    /**
     * Checks if this move type is a capture.
     */
    public boolean isCapture() {
        return this == CAPTURE || this == EN_PASSANT;
    }

    /**
     * Checks if this move type is castling.
     */
    public boolean isCastling() {
        return this == CASTLING_KINGSIDE || this == CASTLING_QUEENSIDE;
    }

    /**
     * Checks if this move type is a special pawn move.
     */
    public boolean isSpecialPawnMove() {
        return this == EN_PASSANT || this == PAWN_DOUBLE_MOVE || this == PAWN_PROMOTION;
    }
}


