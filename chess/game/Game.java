package chess.game;

import chess.enums.Color;
import chess.enums.GameStatus;
import chess.enums.MoveType;
import chess.enums.PieceType;
import chess.exceptions.GameOverException;
import chess.exceptions.InvalidMoveException;
import chess.factories.BoardFactory;
import chess.models.Board;
import chess.models.Move;
import chess.models.Piece;
import chess.models.Position;
import chess.observers.GameEventListener;
import chess.pieces.*;
import chess.players.Player;
import chess.validators.CheckDetector;
import chess.validators.MoveValidator;

import java.util.ArrayList;
import java.util.List;

/**
 * Main game orchestrator class.
 * Manages game flow, turn alternation, and rule enforcement.
 * 
 * Single Responsibility: Orchestrate game flow.
 * Uses Dependency Injection for players and board (DIP).
 */
public class Game {
    
    private final Board board;
    private final Player whitePlayer;
    private final Player blackPlayer;
    private final MoveValidator moveValidator;
    private final CheckDetector checkDetector;
    private final List<Move> moveHistory;
    private final List<GameEventListener> eventListeners;
    
    private Color currentTurn;
    private GameStatus gameStatus;
    private long gameStartTime;

    private Game(Board board, Player whitePlayer, Player blackPlayer) {
        this.board = board;
        this.whitePlayer = whitePlayer;
        this.blackPlayer = blackPlayer;
        this.moveValidator = new MoveValidator(board);
        this.checkDetector = new CheckDetector(board);
        this.moveHistory = new ArrayList<>();
        this.eventListeners = new ArrayList<>();
        this.currentTurn = Color.WHITE;
        this.gameStatus = GameStatus.NOT_STARTED;
    }

    public void addEventListener(GameEventListener listener) {
        eventListeners.add(listener);
    }

    public void removeEventListener(GameEventListener listener) {
        eventListeners.remove(listener);
    }

    /**
     * Starts and runs the game loop.
     */
    public GameResult play() {
        gameStatus = GameStatus.IN_PROGRESS;
        gameStartTime = System.currentTimeMillis();
        
        notifyGameStart();
        
        while (!gameStatus.isGameOver()) {
            board.display();
            
            Player currentPlayer = getCurrentPlayer();
            notifyTurnChange(currentPlayer);
            
            // Check for check
            if (checkDetector.isInCheck(currentTurn)) {
                notifyCheck(currentTurn);
            }
            
            // Get move from player
            Move move = currentPlayer.makeMove(board);
            
            // Handle resignation
            if (move == null) {
                gameStatus = currentTurn == Color.WHITE ? 
                    GameStatus.WHITE_RESIGNS : GameStatus.BLACK_RESIGNS;
                notifyResignation(currentPlayer);
                break;
            }
            
            // Execute move
            try {
                if (!executeMove(move)) {
                    continue;  // Invalid move, try again
                }
            } catch (InvalidMoveException e) {
                notifyInvalidMove(move, e.getMessage());
                continue;
            }
            
            // Check game status after move
            updateGameStatus();
        }
        
        board.display();
        
        GameResult result = createGameResult();
        notifyGameEnd(result);
        
        return result;
    }

    /**
     * Executes a move without playing the full game.
     * For programmatic move execution.
     */
    public boolean executeMove(Move move) {
        if (gameStatus.isGameOver()) {
            throw new GameOverException(gameStatus);
        }

        // Validate move
        if (!moveValidator.isValidMove(move, currentTurn)) {
            notifyInvalidMove(move, "Illegal move");
            return false;
        }

        // Execute the move on the board
        executeMoveOnBoard(move);
        moveHistory.add(move);
        
        notifyMoveMade(move, getCurrentPlayer());
        
        // Update en passant target
        updateEnPassantTarget(move);
        
        // Switch turns
        currentTurn = currentTurn.opposite();
        
        return true;
    }

    private void executeMoveOnBoard(Move move) {
        Position from = move.getFrom();
        Position to = move.getTo();
        Piece piece = board.getPieceAt(from);

        // Handle castling
        if (move.getMoveType() == MoveType.CASTLING_KINGSIDE) {
            int row = from.getRow();
            board.movePiece(new Position(row, 7), new Position(row, 5)); // Move rook
        } else if (move.getMoveType() == MoveType.CASTLING_QUEENSIDE) {
            int row = from.getRow();
            board.movePiece(new Position(row, 0), new Position(row, 3)); // Move rook
        }

        // Handle en passant
        if (move.getMoveType() == MoveType.EN_PASSANT) {
            int capturedPawnRow = from.getRow();
            board.removePiece(new Position(capturedPawnRow, to.getCol()));
        }

        // Regular move
        board.movePiece(from, to);

        // Handle promotion
        if (move.getMoveType() == MoveType.PAWN_PROMOTION) {
            PieceType promoteTo = move.getPromotionPiece();
            if (promoteTo == null) promoteTo = PieceType.QUEEN;
            
            Piece promotedPiece = createPromotedPiece(piece.getColor(), to, promoteTo);
            board.setPieceAt(to, promotedPiece);
        }

        board.setLastMove(move);
    }

    private Piece createPromotedPiece(Color color, Position position, PieceType type) {
        return switch (type) {
            case QUEEN -> new Queen(color, position);
            case ROOK -> new Rook(color, position);
            case BISHOP -> new Bishop(color, position);
            case KNIGHT -> new Knight(color, position);
            default -> new Queen(color, position);
        };
    }

    private void updateEnPassantTarget(Move move) {
        // Set en passant target if pawn made a double move
        if (move.getMoveType() == MoveType.PAWN_DOUBLE_MOVE) {
            Position from = move.getFrom();
            Position to = move.getTo();
            int enPassantRow = (from.getRow() + to.getRow()) / 2;
            board.setEnPassantTarget(new Position(enPassantRow, from.getCol()));
        } else {
            board.setEnPassantTarget(null);
        }
    }

    private void updateGameStatus() {
        Color opponentColor = currentTurn;  // Turn already switched
        
        // Check for checkmate
        if (checkDetector.isCheckmate(opponentColor)) {
            gameStatus = opponentColor == Color.WHITE ? 
                GameStatus.BLACK_WINS : GameStatus.WHITE_WINS;
            Player winner = opponentColor == Color.WHITE ? blackPlayer : whitePlayer;
            notifyCheckmate(opponentColor, winner);
            return;
        }
        
        // Check for stalemate
        if (checkDetector.isStalemate(opponentColor)) {
            gameStatus = GameStatus.STALEMATE;
            notifyStalemate();
            return;
        }
        
        // Check for insufficient material
        if (checkDetector.isInsufficientMaterial()) {
            gameStatus = GameStatus.DRAW_BY_INSUFFICIENT_MATERIAL;
            return;
        }
        
        // Check for fifty-move rule (simplified)
        if (moveHistory.size() >= 100) {
            // Count moves since last pawn move or capture
            int movesSinceProgress = 0;
            for (int i = moveHistory.size() - 1; i >= 0 && movesSinceProgress < 100; i--) {
                Move m = moveHistory.get(i);
                if (m.getPiece() != null && m.getPiece().getType() == PieceType.PAWN || 
                    m.isCapture()) {
                    break;
                }
                movesSinceProgress++;
            }
            if (movesSinceProgress >= 100) {
                gameStatus = GameStatus.DRAW_BY_FIFTY_MOVES;
            }
        }
    }

    public Player getCurrentPlayer() {
        return currentTurn == Color.WHITE ? whitePlayer : blackPlayer;
    }

    public Player getWhitePlayer() {
        return whitePlayer;
    }

    public Player getBlackPlayer() {
        return blackPlayer;
    }

    public Board getBoard() {
        return board;
    }

    public Color getCurrentTurn() {
        return currentTurn;
    }

    public GameStatus getGameStatus() {
        return gameStatus;
    }

    public List<Move> getMoveHistory() {
        return new ArrayList<>(moveHistory);
    }

    public boolean isCheck() {
        return checkDetector.isInCheck(currentTurn);
    }

    public boolean isCheckmate() {
        return checkDetector.isCheckmate(currentTurn);
    }

    public boolean isStalemate() {
        return checkDetector.isStalemate(currentTurn);
    }

    private GameResult createGameResult() {
        long duration = System.currentTimeMillis() - gameStartTime;
        Player winner = null;
        
        if (gameStatus.getWinner() == Color.WHITE) {
            winner = whitePlayer;
        } else if (gameStatus.getWinner() == Color.BLACK) {
            winner = blackPlayer;
        }
        
        return new GameResult(gameStatus, winner, moveHistory.size(), duration);
    }

    // Event notification methods
    private void notifyGameStart() {
        for (GameEventListener listener : eventListeners) {
            listener.onGameStart(this);
        }
    }

    private void notifyMoveMade(Move move, Player player) {
        for (GameEventListener listener : eventListeners) {
            listener.onMoveMade(move, player);
        }
    }

    private void notifyTurnChange(Player player) {
        for (GameEventListener listener : eventListeners) {
            listener.onTurnChange(player);
        }
    }

    private void notifyCheck(Color kingColor) {
        for (GameEventListener listener : eventListeners) {
            listener.onCheck(kingColor);
        }
    }

    private void notifyCheckmate(Color loserColor, Player winner) {
        for (GameEventListener listener : eventListeners) {
            listener.onCheckmate(loserColor, winner);
        }
    }

    private void notifyStalemate() {
        for (GameEventListener listener : eventListeners) {
            listener.onStalemate();
        }
    }

    private void notifyGameEnd(GameResult result) {
        for (GameEventListener listener : eventListeners) {
            listener.onGameEnd(result);
        }
    }

    private void notifyInvalidMove(Move move, String reason) {
        for (GameEventListener listener : eventListeners) {
            listener.onInvalidMove(move, reason);
        }
    }

    private void notifyResignation(Player player) {
        for (GameEventListener listener : eventListeners) {
            listener.onResignation(player);
        }
    }

    // ==================== BUILDER PATTERN ====================
    
    /**
     * Builder for flexible game construction.
     */
    public static class Builder {
        private Board board;
        private Player whitePlayer;
        private Player blackPlayer;

        public Builder() {
            this.board = BoardFactory.createStandardBoard();
        }

        public Builder withBoard(Board board) {
            this.board = board;
            return this;
        }

        public Builder withWhitePlayer(Player player) {
            if (player.getColor() != Color.WHITE) {
                throw new IllegalArgumentException("White player must have WHITE color");
            }
            this.whitePlayer = player;
            return this;
        }

        public Builder withBlackPlayer(Player player) {
            if (player.getColor() != Color.BLACK) {
                throw new IllegalArgumentException("Black player must have BLACK color");
            }
            this.blackPlayer = player;
            return this;
        }

        public Builder withPlayers(Player white, Player black) {
            return withWhitePlayer(white).withBlackPlayer(black);
        }

        public Game build() {
            validate();
            return new Game(board, whitePlayer, blackPlayer);
        }

        private void validate() {
            if (whitePlayer == null) {
                throw new IllegalStateException("White player is required");
            }
            if (blackPlayer == null) {
                throw new IllegalStateException("Black player is required");
            }
            if (board == null) {
                throw new IllegalStateException("Board is required");
            }
        }
    }
}


