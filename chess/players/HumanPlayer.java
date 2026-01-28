package chess.players;

import chess.enums.Color;
import chess.enums.PieceType;
import chess.enums.MoveType;
import chess.models.Board;
import chess.models.Move;
import chess.models.Piece;
import chess.models.Position;

import java.util.Scanner;

/**
 * Human player that takes input from the console.
 */
public class HumanPlayer extends Player {
    
    private final Scanner scanner;

    public HumanPlayer(String name, Color color) {
        super(name, color);
        this.scanner = new Scanner(System.in);
    }

    public HumanPlayer(String name, Color color, Scanner scanner) {
        super(name, color);
        this.scanner = scanner;
    }

    @Override
    public Move makeMove(Board board) {
        while (true) {
            System.out.print(name + "'s turn (" + color.getDisplayName() + "). Enter move (e.g., e2 e4): ");
            
            try {
                String input = scanner.nextLine().trim();
                
                // Handle special commands
                if (input.equalsIgnoreCase("resign")) {
                    return null;  // Null indicates resignation
                }
                
                if (input.equalsIgnoreCase("draw")) {
                    System.out.println("Draw offer noted (not implemented in this version)");
                    continue;
                }

                // Handle castling notation
                if (input.equalsIgnoreCase("O-O") || input.equalsIgnoreCase("0-0")) {
                    return createCastlingMove(board, true);
                }
                if (input.equalsIgnoreCase("O-O-O") || input.equalsIgnoreCase("0-0-0")) {
                    return createCastlingMove(board, false);
                }

                // Parse move
                Move move = parseMove(input, board);
                if (move != null) {
                    return move;
                }
                
                System.out.println("Invalid move format. Use: <from> <to> (e.g., e2 e4)");
                
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    private Move parseMove(String input, Board board) {
        String[] parts = input.split("\\s+");
        
        if (parts.length < 2) {
            return null;
        }

        Position from = Position.fromAlgebraic(parts[0]);
        Position to = Position.fromAlgebraic(parts[1]);

        if (!from.isValid() || !to.isValid()) {
            System.out.println("Invalid position.");
            return null;
        }

        Piece piece = board.getPieceAt(from);
        if (piece == null) {
            System.out.println("No piece at " + from);
            return null;
        }

        if (piece.getColor() != color) {
            System.out.println("That's not your piece!");
            return null;
        }

        // Determine move type
        Piece capturedPiece = board.getPieceAt(to);
        MoveType moveType = MoveType.NORMAL;
        PieceType promotionPiece = null;

        if (capturedPiece != null) {
            moveType = MoveType.CAPTURE;
        }

        // Check for pawn promotion
        if (piece.getType() == PieceType.PAWN && to.getRow() == color.getPromotionRank()) {
            moveType = MoveType.PAWN_PROMOTION;
            promotionPiece = getPromotionChoice();
        }

        // Check for en passant
        if (piece.getType() == PieceType.PAWN && 
            board.getEnPassantTarget() != null &&
            board.getEnPassantTarget().equals(to)) {
            moveType = MoveType.EN_PASSANT;
        }

        // Check for pawn double move
        if (piece.getType() == PieceType.PAWN && 
            Math.abs(to.getRow() - from.getRow()) == 2) {
            moveType = MoveType.PAWN_DOUBLE_MOVE;
        }

        return new Move.Builder(from, to)
                .withPiece(piece)
                .withMoveType(moveType)
                .withCapturedPiece(capturedPiece)
                .withPromotionPiece(promotionPiece)
                .build();
    }

    private Move createCastlingMove(Board board, boolean kingside) {
        int row = color.getBackRank();
        Position kingPos = new Position(row, 4);
        Position targetPos = kingside ? new Position(row, 6) : new Position(row, 2);
        
        Piece king = board.getPieceAt(kingPos);
        if (king == null || king.getType() != PieceType.KING) {
            System.out.println("King not in position for castling.");
            return null;
        }

        return new Move.Builder(kingPos, targetPos)
                .withPiece(king)
                .withMoveType(kingside ? MoveType.CASTLING_KINGSIDE : MoveType.CASTLING_QUEENSIDE)
                .build();
    }

    private PieceType getPromotionChoice() {
        System.out.print("Promote to (Q/R/B/N): ");
        String choice = scanner.nextLine().trim().toUpperCase();
        
        return switch (choice) {
            case "R" -> PieceType.ROOK;
            case "B" -> PieceType.BISHOP;
            case "N" -> PieceType.KNIGHT;
            default -> PieceType.QUEEN;  // Default to Queen
        };
    }
}


