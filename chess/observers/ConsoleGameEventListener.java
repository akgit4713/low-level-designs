package chess.observers;

import chess.enums.Color;
import chess.game.Game;
import chess.game.GameResult;
import chess.models.Move;
import chess.players.Player;

/**
 * Console-based game event listener for displaying game progress.
 */
public class ConsoleGameEventListener implements GameEventListener {

    @Override
    public void onGameStart(Game game) {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("       CHESS GAME STARTED");
        System.out.println("=".repeat(50));
        System.out.println("White: " + game.getWhitePlayer().getName());
        System.out.println("Black: " + game.getBlackPlayer().getName());
        System.out.println("=".repeat(50) + "\n");
    }

    @Override
    public void onMoveMade(Move move, Player player) {
        String notation = move.toAlgebraic();
        System.out.println(player.getName() + " plays: " + notation + " (" + move + ")");
    }

    @Override
    public void onTurnChange(Player player) {
        System.out.println("\n--- " + player.getName() + "'s turn (" + 
                          player.getColor().getDisplayName() + ") ---");
    }

    @Override
    public void onCheck(Color kingColor) {
        System.out.println(">>> CHECK! " + kingColor.getDisplayName() + " king is in check!");
    }

    @Override
    public void onCheckmate(Color loserColor, Player winner) {
        System.out.println("\n" + "#".repeat(50));
        System.out.println("    CHECKMATE!");
        System.out.println("    " + winner.getName() + " wins!");
        System.out.println("#".repeat(50) + "\n");
    }

    @Override
    public void onStalemate() {
        System.out.println("\n" + "#".repeat(50));
        System.out.println("    STALEMATE!");
        System.out.println("    Game is a draw.");
        System.out.println("#".repeat(50) + "\n");
    }

    @Override
    public void onGameEnd(GameResult result) {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("       GAME OVER");
        System.out.println("=".repeat(50));
        System.out.println("Result: " + result.getStatus().getDescription());
        if (result.getWinner() != null) {
            System.out.println("Winner: " + result.getWinner().getName());
        }
        System.out.println("Total moves: " + result.getTotalMoves());
        System.out.println("Duration: " + formatDuration(result.getDurationMillis()));
        System.out.println("=".repeat(50) + "\n");
    }

    @Override
    public void onInvalidMove(Move move, String reason) {
        System.out.println("Invalid move: " + move + " - " + reason);
    }

    @Override
    public void onResignation(Player player) {
        System.out.println(player.getName() + " resigns!");
    }

    private String formatDuration(long millis) {
        long seconds = millis / 1000;
        long minutes = seconds / 60;
        seconds = seconds % 60;
        
        if (minutes > 0) {
            return minutes + " min " + seconds + " sec";
        }
        return seconds + " sec";
    }
}


