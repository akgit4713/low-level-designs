package snakeladder.models;

import snakeladder.exceptions.InvalidBoardConfigException;

/**
 * Represents a Snake on the board.
 * Snake moves player DOWN from head to tail.
 */
public class Snake implements BoardElement {
    
    private final int head;  // Starting position (higher number)
    private final int tail;  // Ending position (lower number)
    private final String id;

    public Snake(int head, int tail) {
        validatePositions(head, tail);
        this.head = head;
        this.tail = tail;
        this.id = "SNAKE_" + head + "_" + tail;
    }

    private void validatePositions(int head, int tail) {
        if (head <= tail) {
            throw new InvalidBoardConfigException(
                "Snake head (" + head + ") must be greater than tail (" + tail + ")");
        }
        if (head <= 0 || tail <= 0) {
            throw new InvalidBoardConfigException(
                "Snake positions must be positive. Head: " + head + ", Tail: " + tail);
        }
    }

    @Override
    public int getStartPosition() {
        return head;
    }

    @Override
    public int getEndPosition() {
        return tail;
    }

    public int getHead() {
        return head;
    }

    public int getTail() {
        return tail;
    }

    public String getId() {
        return id;
    }

    @Override
    public String getDescription() {
        return "🐍 Snake from " + head + " to " + tail + " (slides down " + Math.abs(getPositionDelta()) + " cells)";
    }

    @Override
    public String getType() {
        return "SNAKE";
    }

    @Override
    public String toString() {
        return getDescription();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Snake snake = (Snake) obj;
        return head == snake.head && tail == snake.tail;
    }

    @Override
    public int hashCode() {
        return 31 * head + tail;
    }
}



