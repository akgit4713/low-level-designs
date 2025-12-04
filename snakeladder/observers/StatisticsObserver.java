package snakeladder.observers;

import snakeladder.models.Game;
import snakeladder.models.MoveOutcome;
import snakeladder.models.Player;

import java.util.HashMap;
import java.util.Map;

/**
 * Observer that collects game statistics.
 * Useful for analytics and testing.
 */
public class StatisticsObserver implements GameObserver {
    
    private int totalMoves;
    private int snakeEncounters;
    private int ladderClimbs;
    private final Map<String, Integer> movesByPlayer;
    private final Map<String, Integer> snakesByPlayer;
    private final Map<String, Integer> laddersByPlayer;
    private long gameStartTime;
    private long gameEndTime;

    public StatisticsObserver() {
        this.movesByPlayer = new HashMap<>();
        this.snakesByPlayer = new HashMap<>();
        this.laddersByPlayer = new HashMap<>();
        reset();
    }

    public void reset() {
        totalMoves = 0;
        snakeEncounters = 0;
        ladderClimbs = 0;
        movesByPlayer.clear();
        snakesByPlayer.clear();
        laddersByPlayer.clear();
        gameStartTime = 0;
        gameEndTime = 0;
    }

    @Override
    public void onGameStart(Game game) {
        reset();
        gameStartTime = System.currentTimeMillis();
        for (Player player : game.getPlayers()) {
            movesByPlayer.put(player.getId(), 0);
            snakesByPlayer.put(player.getId(), 0);
            laddersByPlayer.put(player.getId(), 0);
        }
    }

    @Override
    public void onPlayerMove(MoveOutcome outcome) {
        totalMoves++;
        String playerId = outcome.getPlayer().getId();
        movesByPlayer.merge(playerId, 1, Integer::sum);
    }

    @Override
    public void onSnakeEncounter(MoveOutcome outcome) {
        snakeEncounters++;
        String playerId = outcome.getPlayer().getId();
        snakesByPlayer.merge(playerId, 1, Integer::sum);
    }

    @Override
    public void onLadderClimb(MoveOutcome outcome) {
        ladderClimbs++;
        String playerId = outcome.getPlayer().getId();
        laddersByPlayer.merge(playerId, 1, Integer::sum);
    }

    @Override
    public void onGameEnd(Game game) {
        gameEndTime = System.currentTimeMillis();
    }

    // Getters for statistics
    public int getTotalMoves() {
        return totalMoves;
    }

    public int getSnakeEncounters() {
        return snakeEncounters;
    }

    public int getLadderClimbs() {
        return ladderClimbs;
    }

    public int getMovesByPlayer(String playerId) {
        return movesByPlayer.getOrDefault(playerId, 0);
    }

    public int getSnakesByPlayer(String playerId) {
        return snakesByPlayer.getOrDefault(playerId, 0);
    }

    public int getLaddersByPlayer(String playerId) {
        return laddersByPlayer.getOrDefault(playerId, 0);
    }

    public long getGameDurationMs() {
        if (gameEndTime == 0) {
            return System.currentTimeMillis() - gameStartTime;
        }
        return gameEndTime - gameStartTime;
    }

    public void printStatistics() {
        System.out.println("\n📊 GAME STATISTICS");
        System.out.println("━".repeat(40));
        System.out.println("Total Moves: " + totalMoves);
        System.out.println("Snake Encounters: " + snakeEncounters);
        System.out.println("Ladder Climbs: " + ladderClimbs);
        System.out.println("Game Duration: " + getGameDurationMs() + "ms");
        System.out.println("━".repeat(40));
    }
}



