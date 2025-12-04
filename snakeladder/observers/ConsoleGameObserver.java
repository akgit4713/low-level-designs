package snakeladder.observers;

import snakeladder.models.Game;
import snakeladder.models.MoveOutcome;
import snakeladder.models.Player;

/**
 * Console-based observer that prints game events to stdout.
 */
public class ConsoleGameObserver implements GameObserver {

    @Override
    public void onGameStart(Game game) {
        System.out.println("\n🎮 GAME STARTED!");
        System.out.println("━".repeat(50));
        System.out.println("Players:");
        for (Player player : game.getPlayers()) {
            System.out.println("  " + player.getPiece().getEmoji() + " " + player.getName());
        }
        System.out.println("━".repeat(50) + "\n");
    }

    @Override
    public void onPlayerMove(MoveOutcome outcome) {
        System.out.println(outcome);
    }

    @Override
    public void onSnakeEncounter(MoveOutcome outcome) {
        System.out.println("   🐍 Oh no! " + outcome.getPlayer().getName() + 
                          " was bitten by a snake! Sliding down to " + 
                          outcome.getFinalPosition());
    }

    @Override
    public void onLadderClimb(MoveOutcome outcome) {
        System.out.println("   🪜 Lucky! " + outcome.getPlayer().getName() + 
                          " found a ladder! Climbing up to " + 
                          outcome.getFinalPosition());
    }

    @Override
    public void onPlayerWin(Player player, Game game) {
        System.out.println("\n" + "🎉".repeat(10));
        System.out.println("🏆 " + player.getPiece().getEmoji() + " " + player.getName() + " WINS! 🏆");
        System.out.println("🎉".repeat(10) + "\n");
    }

    @Override
    public void onGameEnd(Game game) {
        System.out.println("━".repeat(50));
        System.out.println("Game Over! Total moves: " + game.getTotalMoves());
        System.out.println("━".repeat(50));
        
        // Show final positions
        System.out.println("\nFinal Positions:");
        for (Player player : game.getPlayers()) {
            String status = player.hasWon() ? "🏆 WINNER" : "Position: " + player.getPosition();
            System.out.println("  " + player.getPiece().getEmoji() + " " + player.getName() + " - " + status);
        }
    }

    @Override
    public void onTurnChange(Player currentPlayer, Player nextPlayer) {
        // Silent by default - can be enabled for verbose mode
    }
}



