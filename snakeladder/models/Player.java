package snakeladder.models;

import snakeladder.enums.GamePiece;
import java.util.UUID;

/**
 * Represents a player in the game.
 * Immutable player identity with mutable position state.
 */
public class Player {
    
    private final String id;
    private final String name;
    private final GamePiece piece;
    private int position;
    private boolean hasWon;

    public Player(String name, GamePiece piece) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.piece = piece;
        this.position = 0;  // Start before the board
        this.hasWon = false;
    }

    public Player(String id, String name, GamePiece piece) {
        this.id = id;
        this.name = name;
        this.piece = piece;
        this.position = 0;
        this.hasWon = false;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public GamePiece getPiece() {
        return piece;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public boolean hasWon() {
        return hasWon;
    }

    public void setWon(boolean won) {
        this.hasWon = won;
    }

    /**
     * Resets the player's position for a new game.
     */
    public void reset() {
        this.position = 0;
        this.hasWon = false;
    }

    @Override
    public String toString() {
        return piece.getEmoji() + " " + name + " (Position: " + position + ")";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Player player = (Player) obj;
        return id.equals(player.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}



