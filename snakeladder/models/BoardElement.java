package snakeladder.models;

/**
 * Interface for board elements like Snakes and Ladders.
 * Follows ISP - Interface Segregation Principle.
 * Supports LSP - Both Snake and Ladder can substitute this interface.
 */
public interface BoardElement {
    
    /**
     * Gets the starting position of this element.
     * For Snake: the head position.
     * For Ladder: the base position.
     */
    int getStartPosition();

    /**
     * Gets the ending position of this element.
     * For Snake: the tail position.
     * For Ladder: the top position.
     */
    int getEndPosition();

    /**
     * Gets the position change when landing on this element.
     * Negative for snakes, positive for ladders.
     */
    default int getPositionDelta() {
        return getEndPosition() - getStartPosition();
    }

    /**
     * Gets a description of this element.
     */
    String getDescription();

    /**
     * Gets the type of this element.
     */
    String getType();
}



