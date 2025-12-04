package snakeladder.factories;

import snakeladder.models.Board;
import snakeladder.models.Ladder;
import snakeladder.models.Snake;

/**
 * Factory for creating game boards with various configurations.
 * Follows Factory Pattern - encapsulates board creation logic.
 */
public class BoardFactory {

    private BoardFactory() {
        // Private constructor for utility class
    }

    /**
     * Creates a standard 100-cell board with classic snakes and ladders.
     */
    public static Board createStandardBoard() {
        Board board = new Board(100);
        
        // Classic snakes (head -> tail)
        board.addSnake(new Snake(99, 54));
        board.addSnake(new Snake(70, 55));
        board.addSnake(new Snake(52, 42));
        board.addSnake(new Snake(25, 2));
        board.addSnake(new Snake(95, 72));
        board.addSnake(new Snake(91, 45));
        board.addSnake(new Snake(47, 19));
        
        // Classic ladders (base -> top)
        board.addLadder(new Ladder(2, 23));
        board.addLadder(new Ladder(8, 34));
        board.addLadder(new Ladder(20, 77));
        board.addLadder(new Ladder(32, 68));
        board.addLadder(new Ladder(41, 79));
        board.addLadder(new Ladder(74, 88));
        board.addLadder(new Ladder(82, 100));
        board.addLadder(new Ladder(85, 95));
        
        return board;
    }

    /**
     * Creates a smaller 50-cell board for quick games.
     */
    public static Board createSmallBoard() {
        Board board = new Board(50);
        
        // Snakes
        board.addSnake(new Snake(49, 30));
        board.addSnake(new Snake(37, 20));
        board.addSnake(new Snake(28, 10));
        board.addSnake(new Snake(45, 25));
        
        // Ladders
        board.addLadder(new Ladder(4, 15));
        board.addLadder(new Ladder(12, 32));
        board.addLadder(new Ladder(22, 40));
        board.addLadder(new Ladder(35, 48));
        
        return board;
    }

    /**
     * Creates a large 200-cell board for extended games.
     */
    public static Board createLargeBoard() {
        Board board = new Board(200);
        
        // More snakes for a larger board
        board.addSnake(new Snake(199, 150));
        board.addSnake(new Snake(175, 120));
        board.addSnake(new Snake(150, 80));
        board.addSnake(new Snake(130, 90));
        board.addSnake(new Snake(95, 45));
        board.addSnake(new Snake(70, 35));
        board.addSnake(new Snake(55, 20));
        board.addSnake(new Snake(185, 140));
        board.addSnake(new Snake(110, 65));
        
        // More ladders
        board.addLadder(new Ladder(5, 45));
        board.addLadder(new Ladder(15, 60));
        board.addLadder(new Ladder(30, 85));
        board.addLadder(new Ladder(50, 100));
        board.addLadder(new Ladder(75, 125));
        board.addLadder(new Ladder(100, 160));
        board.addLadder(new Ladder(135, 180));
        board.addLadder(new Ladder(155, 195));
        board.addLadder(new Ladder(170, 200));
        
        return board;
    }

    /**
     * Creates an empty board with no snakes or ladders.
     * Useful for custom configurations.
     */
    public static Board createEmptyBoard(int size) {
        return new Board(size);
    }

    /**
     * Creates a board builder for custom configuration.
     */
    public static BoardBuilder customBoard(int size) {
        return new BoardBuilder(size);
    }

    /**
     * Builder for creating custom boards.
     */
    public static class BoardBuilder {
        private final Board board;

        public BoardBuilder(int size) {
            this.board = new Board(size);
        }

        public BoardBuilder addSnake(int head, int tail) {
            board.addSnake(new Snake(head, tail));
            return this;
        }

        public BoardBuilder addLadder(int base, int top) {
            board.addLadder(new Ladder(base, top));
            return this;
        }

        public Board build() {
            return board;
        }
    }
}



