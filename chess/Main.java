package chess;

import chess.enums.Color;
import chess.enums.MoveType;
import chess.enums.PieceType;
import chess.factories.BoardFactory;
import chess.factories.PieceFactory;
import chess.game.Game;
import chess.game.GameResult;
import chess.models.Board;
import chess.models.Move;
import chess.models.Position;
import chess.observers.ConsoleGameEventListener;
import chess.players.HumanPlayer;
import chess.players.Player;

import java.util.Scanner;

/**
 * Main entry point for the Chess game.
 * Demonstrates various game modes and features.
 */
public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("\n" + "=".repeat(60));
        System.out.println("                  CHESS GAME");
        System.out.println("=".repeat(60));
        System.out.println("\nSelect a game mode:");
        System.out.println("1. Human vs Human");
        System.out.println("2. Quick Demo - Scholar's Mate (4-move checkmate)");
        System.out.println("3. Stalemate Demo");
        System.out.println("4. Move Validation Demo");
        System.out.println("5. Display Board Demo");
        System.out.println("0. Exit");
        System.out.print("\nEnter your choice: ");
        
        try {
            int choice = Integer.parseInt(scanner.nextLine().trim());
            
            switch (choice) {
                case 1 -> playHumanVsHuman(scanner);
                case 2 -> demonstrateScholarsMate();
                case 3 -> demonstrateStalemate();
                case 4 -> demonstrateMoveValidation();
                case 5 -> demonstrateBoardDisplay();
                case 0 -> System.out.println("Goodbye!");
                default -> System.out.println("Invalid choice.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a number.");
        }
    }

    /**
     * Human vs Human game mode.
     */
    private static void playHumanVsHuman(Scanner scanner) {
        System.out.print("\nEnter White player's name: ");
        String whiteName = scanner.nextLine().trim();
        if (whiteName.isEmpty()) whiteName = "White";
        
        System.out.print("Enter Black player's name: ");
        String blackName = scanner.nextLine().trim();
        if (blackName.isEmpty()) blackName = "Black";
        
        Player whitePlayer = new HumanPlayer(whiteName, Color.WHITE, scanner);
        Player blackPlayer = new HumanPlayer(blackName, Color.BLACK, scanner);
        
        Game game = new Game.Builder()
                .withWhitePlayer(whitePlayer)
                .withBlackPlayer(blackPlayer)
                .build();
        
        game.addEventListener(new ConsoleGameEventListener());
        
        System.out.println("\nGame Controls:");
        System.out.println("- Enter moves as: <from> <to> (e.g., 'e2 e4')");
        System.out.println("- Castling: 'O-O' (kingside) or 'O-O-O' (queenside)");
        System.out.println("- Type 'resign' to resign\n");
        
        GameResult result = game.play();
        System.out.println("\nFinal Result: " + result);
    }

    /**
     * Demonstrates Scholar's Mate - a 4-move checkmate.
     */
    private static void demonstrateScholarsMate() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("    SCHOLAR'S MATE DEMONSTRATION");
        System.out.println("    (Checkmate in 4 moves)");
        System.out.println("=".repeat(60));
        
        // Create a demo player that doesn't require input
        Player whitePlayer = new DemoPlayer("White", Color.WHITE);
        Player blackPlayer = new DemoPlayer("Black", Color.BLACK);
        
        Game game = new Game.Builder()
                .withWhitePlayer(whitePlayer)
                .withBlackPlayer(blackPlayer)
                .build();
        
        game.addEventListener(new ConsoleGameEventListener());
        
        Board board = game.getBoard();
        
        // Move sequence for Scholar's Mate
        Move[] moves = {
            // 1. e2-e4 (White opens with e4)
            createMove(board, "e2", "e4"),
            // 1... e7-e5 (Black responds with e5)
            createMove(board, "e7", "e5"),
            // 2. Bc1-c4 (White develops bishop to c4)
            createMove(board, "f1", "c4"),
            // 2... Nb8-c6 (Black develops knight)
            createMove(board, "b8", "c6"),
            // 3. Qd1-h5 (White brings queen to h5)
            createMove(board, "d1", "h5"),
            // 3... Ng8-f6?? (Black's blunder - should defend f7)
            createMove(board, "g8", "f6"),
            // 4. Qh5xf7# (Checkmate!)
            createMove(board, "h5", "f7")
        };
        
        System.out.println("\nExecuting Scholar's Mate sequence:\n");
        board.display();
        
        int moveNum = 1;
        for (int i = 0; i < moves.length; i++) {
            Move move = moves[i];
            if (move == null) {
                System.out.println("Error creating move at index " + i);
                continue;
            }
            
            String prefix = (i % 2 == 0) ? moveNum + ". " : "   ";
            System.out.println(prefix + move.toAlgebraic() + " (" + move.getFrom() + " -> " + move.getTo() + ")");
            
            game.executeMove(move);
            board.display();
            
            if (i % 2 == 1) moveNum++;
            
            // Check for checkmate after white's last move
            if (game.isCheckmate()) {
                System.out.println("\n>>> CHECKMATE! White wins!");
                break;
            }
            
            // Add delay for visualization
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        
        System.out.println("\n" + "=".repeat(60));
        System.out.println("Scholar's Mate complete!");
        System.out.println("=".repeat(60));
    }

    /**
     * Demonstrates a stalemate position.
     */
    private static void demonstrateStalemate() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("    STALEMATE DEMONSTRATION");
        System.out.println("=".repeat(60));
        
        // Create stalemate position
        Board board = BoardFactory.createStalemateTestBoard();
        
        System.out.println("\nStalemate Position:");
        System.out.println("White King is on a8 (cornered)");
        System.out.println("Black King is on b6");
        System.out.println("Black Queen is on b7");
        System.out.println("\nWhite to move but has no legal moves - STALEMATE!\n");
        
        board.display();
        
        // Verify it's stalemate
        chess.validators.CheckDetector detector = new chess.validators.CheckDetector(board);
        
        System.out.println("Is White in check? " + detector.isInCheck(Color.WHITE));
        System.out.println("Is White in stalemate? " + detector.isStalemate(Color.WHITE));
        
        if (detector.isStalemate(Color.WHITE)) {
            System.out.println("\n>>> STALEMATE! The game is a draw.");
        }
    }

    /**
     * Demonstrates move validation for different pieces.
     */
    private static void demonstrateMoveValidation() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("    MOVE VALIDATION DEMONSTRATION");
        System.out.println("=".repeat(60));
        
        Board board = BoardFactory.createStandardBoard();
        chess.validators.MoveValidator validator = new chess.validators.MoveValidator(board);
        
        System.out.println("\nInitial board position:");
        board.display();
        
        // Test various moves
        System.out.println("Testing move validation:\n");
        
        // Valid pawn move
        testMove(board, validator, "e2", "e4", Color.WHITE, "Pawn e2-e4 (double move)");
        
        // Invalid pawn move (too far)
        testMove(board, validator, "e2", "e5", Color.WHITE, "Pawn e2-e5 (invalid - too far)");
        
        // Valid knight move
        testMove(board, validator, "g1", "f3", Color.WHITE, "Knight g1-f3");
        
        // Invalid knight move
        testMove(board, validator, "g1", "e3", Color.WHITE, "Knight g1-e3 (invalid - not L-shape)");
        
        // Invalid move - wrong player's piece
        testMove(board, validator, "e7", "e5", Color.WHITE, "Black pawn e7-e5 by White (invalid - wrong color)");
        
        // Execute a move and test follow-up
        System.out.println("\nExecuting e2-e4...");
        board.movePiece(Position.fromAlgebraic("e2"), Position.fromAlgebraic("e4"));
        board.display();
        
        // Now test bishop can move
        testMove(board, validator, "f1", "c4", Color.WHITE, "Bishop f1-c4 (now valid - path clear)");
    }

    /**
     * Demonstrates board display with both ASCII and Unicode.
     */
    private static void demonstrateBoardDisplay() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("    BOARD DISPLAY DEMONSTRATION");
        System.out.println("=".repeat(60));
        
        Board board = BoardFactory.createStandardBoard();
        
        System.out.println("\nASCII Display (Standard):");
        board.display();
        
        System.out.println("\nUnicode Display (Chess Symbols):");
        board.displayUnicode();
        
        System.out.println("\nPiece Legend:");
        System.out.println("K/k = King    ♔/♚");
        System.out.println("Q/q = Queen   ♕/♛");
        System.out.println("R/r = Rook    ♖/♜");
        System.out.println("B/b = Bishop  ♗/♝");
        System.out.println("N/n = Knight  ♘/♞");
        System.out.println("P/p = Pawn    ♙/♟");
        System.out.println("\n(Uppercase = White, Lowercase = Black)");
    }

    private static Move createMove(Board board, String from, String to) {
        Position fromPos = Position.fromAlgebraic(from);
        Position toPos = Position.fromAlgebraic(to);
        chess.models.Piece piece = board.getPieceAt(fromPos);
        
        if (piece == null) {
            return null;
        }
        
        chess.models.Piece capturedPiece = board.getPieceAt(toPos);
        MoveType moveType = capturedPiece != null ? MoveType.CAPTURE : MoveType.NORMAL;
        
        // Check for pawn double move
        if (piece.getType() == PieceType.PAWN && 
            Math.abs(toPos.getRow() - fromPos.getRow()) == 2) {
            moveType = MoveType.PAWN_DOUBLE_MOVE;
        }
        
        return new Move.Builder(fromPos, toPos)
                .withPiece(piece)
                .withMoveType(moveType)
                .withCapturedPiece(capturedPiece)
                .build();
    }

    private static void testMove(Board board, chess.validators.MoveValidator validator,
                                  String from, String to, Color color, String description) {
        Position fromPos = Position.fromAlgebraic(from);
        Position toPos = Position.fromAlgebraic(to);
        chess.models.Piece piece = board.getPieceAt(fromPos);
        
        Move move = new Move.Builder(fromPos, toPos)
                .withPiece(piece)
                .build();
        
        boolean isValid = validator.isValidMove(move, color);
        String result = isValid ? "✓ VALID" : "✗ INVALID";
        System.out.println(result + ": " + description);
    }

    /**
     * Demo player for automated demonstrations.
     */
    private static class DemoPlayer extends Player {
        public DemoPlayer(String name, Color color) {
            super(name, color);
        }

        @Override
        public Move makeMove(Board board) {
            // This player doesn't make moves - used for demo mode
            return null;
        }
    }
}


