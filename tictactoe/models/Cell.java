package tictactoe.models;

/**
 * Represents a single cell on the game board.
 * Single Responsibility: Managing cell state only.
 */
public class Cell {
    private final int row;
    private final int col;
    private Symbol symbol;

    public Cell(int row, int col) {
        this.row = row;
        this.col = col;
        this.symbol = Symbol.EMPTY;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public Symbol getSymbol() {
        return symbol;
    }

    public void setSymbol(Symbol symbol) {
        this.symbol = symbol;
    }

    public boolean isEmpty() {
        return symbol == Symbol.EMPTY;
    }

    public void clear() {
        this.symbol = Symbol.EMPTY;
    }

    @Override
    public String toString() {
        return symbol.toString();
    }
}

