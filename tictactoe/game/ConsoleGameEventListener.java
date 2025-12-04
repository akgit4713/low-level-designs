package tictactoe.game;

import tictactoe.models.Move;
import tictactoe.players.Player;

/**
 * Console-based implementation of GameEventListener.
 * Demonstrates Observer Pattern for loose coupling.
 */
public class ConsoleGameEventListener implements GameEventListener {

    @Override
    public void onGameStart(Game game) {
        System.out.println("\n========================================");
        System.out.println("          TIC TAC TOE GAME");
        System.out.println("========================================");
        System.out.println("Players:");
        for (Player player : game.getPlayers()) {
            System.out.println("  - " + player);
        }
        System.out.println("----------------------------------------\n");
    }

    @Override
    public void onMoveMade(Move move) {
        // Move details are already printed by players
    }

    @Override
    public void onTurnChange(Player currentPlayer) {
        // Turn info is handled by player.makeMove()
    }

    @Override
    public void onGameEnd(GameResult result) {
        System.out.println("\n========================================");
        System.out.println("            GAME OVER!");
        System.out.println("========================================");
        
        if (result.hasWinner()) {
            System.out.println("🎉 Winner: " + result.getWinner().getName() + " (" + 
                             result.getWinner().getSymbol() + ")");
        } else {
            System.out.println("🤝 It's a DRAW!");
        }
        
        System.out.println("Total moves: " + result.getTotalMoves());
        System.out.println("Game duration: " + result.getGameDurationMs() + " ms");
        System.out.println("========================================\n");
    }

    @Override
    public void onInvalidMove(Player player, int row, int col) {
        System.out.println("⚠️  Invalid move by " + player.getName() + 
                          " at position (" + row + ", " + col + ")");
    }
}

