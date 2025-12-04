package snakeladder.models;

import snakeladder.exceptions.InvalidBoardConfigException;

import java.util.*;

/**
 * Represents the game board with cells, snakes, and ladders.
 * Single Responsibility: Managing board state and element lookups.
 */
public class Board {
    
    private final int size;
    private final Map<Integer, BoardElement> elements;  // Position -> Snake or Ladder
    private final List<Snake> snakes;
    private final List<Ladder> ladders;

    public Board(int size) {
        this.size = size;
        this.elements = new HashMap<>();
        this.snakes = new ArrayList<>();
        this.ladders = new ArrayList<>();
    }

    /**
     * Adds a snake to the board.
     */
    public void addSnake(Snake snake) {
        validateElement(snake);
        elements.put(snake.getHead(), snake);
        snakes.add(snake);
    }

    /**
     * Adds a ladder to the board.
     */
    public void addLadder(Ladder ladder) {
        validateElement(ladder);
        elements.put(ladder.getBase(), ladder);
        ladders.add(ladder);
    }

    /**
     * Validates that a board element can be added.
     */
    private void validateElement(BoardElement element) {
        int startPos = element.getStartPosition();
        int endPos = element.getEndPosition();

        // Check bounds
        if (startPos <= 0 || startPos > size) {
            throw new InvalidBoardConfigException(
                "Element start position " + startPos + " is out of bounds [1, " + size + "]");
        }
        if (endPos <= 0 || endPos > size) {
            throw new InvalidBoardConfigException(
                "Element end position " + endPos + " is out of bounds [1, " + size + "]");
        }

        // Check for conflicts
        if (elements.containsKey(startPos)) {
            throw new InvalidBoardConfigException(
                "Position " + startPos + " already has a " + elements.get(startPos).getType());
        }

        // Snake head and ladder top should not be at the last position
        if (element instanceof Snake && startPos == size) {
            throw new InvalidBoardConfigException(
                "Snake head cannot be at the winning position " + size);
        }
    }

    /**
     * Gets the board element at a position, if any.
     */
    public Optional<BoardElement> getElementAt(int position) {
        return Optional.ofNullable(elements.get(position));
    }

    /**
     * Gets the final position after applying any snake/ladder at the given position.
     */
    public int getFinalPosition(int position) {
        BoardElement element = elements.get(position);
        if (element != null) {
            return element.getEndPosition();
        }
        return position;
    }

    /**
     * Checks if a position is valid on this board.
     */
    public boolean isValidPosition(int position) {
        return position >= 1 && position <= size;
    }

    /**
     * Checks if the position is the winning position.
     */
    public boolean isWinningPosition(int position) {
        return position == size;
    }

    public int getSize() {
        return size;
    }

    public List<Snake> getSnakes() {
        return Collections.unmodifiableList(snakes);
    }

    public List<Ladder> getLadders() {
        return Collections.unmodifiableList(ladders);
    }

    public Map<Integer, BoardElement> getElements() {
        return Collections.unmodifiableMap(elements);
    }

    /**
     * Displays the board configuration.
     */
    public void display() {
        System.out.println("\n╔══════════════════════════════════════╗");
        System.out.println("║          BOARD CONFIGURATION         ║");
        System.out.println("╠══════════════════════════════════════╣");
        System.out.println("║ Board Size: " + size + " cells");
        System.out.println("╠══════════════════════════════════════╣");
        System.out.println("║ SNAKES (" + snakes.size() + "):");
        for (Snake snake : snakes) {
            System.out.println("║   " + snake);
        }
        System.out.println("╠══════════════════════════════════════╣");
        System.out.println("║ LADDERS (" + ladders.size() + "):");
        for (Ladder ladder : ladders) {
            System.out.println("║   " + ladder);
        }
        System.out.println("╚══════════════════════════════════════╝\n");
    }

    @Override
    public String toString() {
        return "Board{size=" + size + ", snakes=" + snakes.size() + ", ladders=" + ladders.size() + "}";
    }
}



