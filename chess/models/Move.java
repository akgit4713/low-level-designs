package chess.models;

import chess.enums.MoveType;
import chess.enums.PieceType;

import java.util.Objects;

/**
 * Represents a chess move from one position to another.
 * Immutable object that encapsulates all information about a move.
 */
public class Move {
    
    private final Position from;
    private final Position to;
    private final Piece piece;
    private final Piece capturedPiece;
    private final MoveType moveType;
    private final PieceType promotionPiece;  // For pawn promotion
    private final long timestamp;

    private Move(Builder builder) {
        this.from = builder.from;
        this.to = builder.to;
        this.piece = builder.piece;
        this.capturedPiece = builder.capturedPiece;
        this.moveType = builder.moveType;
        this.promotionPiece = builder.promotionPiece;
        this.timestamp = System.currentTimeMillis();
    }

    /**
     * Creates a simple move from one position to another.
     */
    public static Move of(Position from, Position to) {
        return new Builder(from, to).build();
    }

    /**
     * Creates a move with piece information.
     */
    public static Move of(Position from, Position to, Piece piece) {
        return new Builder(from, to).withPiece(piece).build();
    }

    /**
     * Creates a move with piece and move type.
     */
    public static Move of(Position from, Position to, Piece piece, MoveType moveType) {
        return new Builder(from, to).withPiece(piece).withMoveType(moveType).build();
    }

    public Position getFrom() {
        return from;
    }

    public Position getTo() {
        return to;
    }

    public Piece getPiece() {
        return piece;
    }

    public Piece getCapturedPiece() {
        return capturedPiece;
    }

    public MoveType getMoveType() {
        return moveType;
    }

    public PieceType getPromotionPiece() {
        return promotionPiece;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public boolean isCapture() {
        return capturedPiece != null || moveType == MoveType.EN_PASSANT;
    }

    public boolean isCastling() {
        return moveType != null && moveType.isCastling();
    }

    public boolean isPromotion() {
        return moveType == MoveType.PAWN_PROMOTION;
    }

    /**
     * Returns the algebraic notation for this move.
     */
    public String toAlgebraic() {
        StringBuilder sb = new StringBuilder();
        
        if (moveType == MoveType.CASTLING_KINGSIDE) {
            return "O-O";
        }
        if (moveType == MoveType.CASTLING_QUEENSIDE) {
            return "O-O-O";
        }
        
        // Piece symbol (except for pawns)
        if (piece != null && piece.getType() != PieceType.PAWN) {
            sb.append(piece.getType().getSymbol());
        }
        
        // Source file for disambiguation (simplified - just add it for captures)
        if (piece != null && piece.getType() == PieceType.PAWN && isCapture()) {
            sb.append((char) ('a' + from.getCol()));
        }
        
        // Capture symbol
        if (isCapture()) {
            sb.append('x');
        }
        
        // Destination
        sb.append(to.toAlgebraic());
        
        // Promotion
        if (isPromotion() && promotionPiece != null) {
            sb.append('=').append(promotionPiece.getSymbol());
        }
        
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Move move = (Move) o;
        return Objects.equals(from, move.from) && Objects.equals(to, move.to);
    }

    @Override
    public int hashCode() {
        return Objects.hash(from, to);
    }

    @Override
    public String toString() {
        return from.toAlgebraic() + " -> " + to.toAlgebraic() + 
               (moveType != null ? " (" + moveType.getDescription() + ")" : "");
    }

    // Builder Pattern for flexible move construction
    public static class Builder {
        private final Position from;
        private final Position to;
        private Piece piece;
        private Piece capturedPiece;
        private MoveType moveType = MoveType.NORMAL;
        private PieceType promotionPiece;

        public Builder(Position from, Position to) {
            this.from = from;
            this.to = to;
        }

        public Builder withPiece(Piece piece) {
            this.piece = piece;
            return this;
        }

        public Builder withCapturedPiece(Piece capturedPiece) {
            this.capturedPiece = capturedPiece;
            if (capturedPiece != null && moveType == MoveType.NORMAL) {
                this.moveType = MoveType.CAPTURE;
            }
            return this;
        }

        public Builder withMoveType(MoveType moveType) {
            this.moveType = moveType;
            return this;
        }

        public Builder withPromotionPiece(PieceType promotionPiece) {
            this.promotionPiece = promotionPiece;
            return this;
        }

        public Move build() {
            return new Move(this);
        }
    }
}


