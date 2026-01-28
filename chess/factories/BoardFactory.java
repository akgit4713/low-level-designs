package chess.factories;

import chess.enums.Color;
import chess.enums.PieceType;
import chess.models.Board;
import chess.models.Position;

/**
 * Factory for creating chess boards with various setups.
 * 
 * Factory Pattern: Encapsulates board creation logic.
 */
public class BoardFactory {

    /**
     * Creates a standard chess board with all pieces in starting positions.
     */
    public static Board createStandardBoard() {
        return Board.createStandardBoard();
    }

    /**
     * Creates an empty board.
     */
    public static Board createEmptyBoard() {
        return new Board();
    }

    /**
     * Creates a board for endgame practice with just kings and specified pieces.
     */
    public static Board createEndgameBoard(String... pieceNotations) {
        Board board = new Board();
        
        // Always add kings
        board.setPieceAt(new Position(0, 4), 
            PieceFactory.createPiece(PieceType.KING, Color.WHITE, new Position(0, 4)));
        board.setPieceAt(new Position(7, 4), 
            PieceFactory.createPiece(PieceType.KING, Color.BLACK, new Position(7, 4)));

        // Parse piece notations (e.g., "Qd4" = Queen at d4, "Nf3" = Knight at f3)
        for (String notation : pieceNotations) {
            parsePieceNotation(board, notation);
        }

        return board;
    }

    /**
     * Creates a board setup for testing checkmate.
     */
    public static Board createCheckmateTestBoard() {
        Board board = new Board();
        
        // Fool's Mate position (checkmate in 2 moves)
        // White King at e1, Black Queen at h4, Black King at e8
        board.setPieceAt(new Position(0, 4), 
            PieceFactory.createPiece(PieceType.KING, Color.WHITE, new Position(0, 4)));
        board.setPieceAt(new Position(7, 4), 
            PieceFactory.createPiece(PieceType.KING, Color.BLACK, new Position(7, 4)));
        board.setPieceAt(new Position(3, 7), 
            PieceFactory.createPiece(PieceType.QUEEN, Color.BLACK, new Position(3, 7)));
        
        return board;
    }

    /**
     * Creates a stalemate position for testing.
     */
    public static Board createStalemateTestBoard() {
        Board board = new Board();
        
        // Classic stalemate: White King at a8 (cornered), Black King at b6, Black Queen at b7
        // White to move but has no legal moves
        board.setPieceAt(new Position(7, 0), 
            PieceFactory.createPiece(PieceType.KING, Color.WHITE, new Position(7, 0)));
        board.setPieceAt(new Position(5, 1), 
            PieceFactory.createPiece(PieceType.KING, Color.BLACK, new Position(5, 1)));
        board.setPieceAt(new Position(6, 1), 
            PieceFactory.createPiece(PieceType.QUEEN, Color.BLACK, new Position(6, 1)));
        
        return board;
    }

    private static void parsePieceNotation(Board board, String notation) {
        if (notation.length() < 3) return;

        char pieceChar = notation.charAt(0);
        String position = notation.substring(1, 3);
        boolean isBlack = notation.length() > 3 && notation.charAt(3) == 'b';
        
        Color color = isBlack ? Color.BLACK : Color.WHITE;
        Position pos = Position.fromAlgebraic(position);
        
        PieceType type = switch (Character.toUpperCase(pieceChar)) {
            case 'K' -> PieceType.KING;
            case 'Q' -> PieceType.QUEEN;
            case 'R' -> PieceType.ROOK;
            case 'B' -> PieceType.BISHOP;
            case 'N' -> PieceType.KNIGHT;
            case 'P' -> PieceType.PAWN;
            default -> null;
        };

        if (type != null) {
            board.setPieceAt(pos, PieceFactory.createPiece(type, color, pos));
        }
    }
}


