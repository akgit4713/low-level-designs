package tictactoe.models;

/**
 * Enum representing the possible symbols in Tic Tac Toe.
 * Extensible - new symbols can be added for variants.
 */
public enum Symbol {
    X('X'),
    O('O'),
    EMPTY(' ');

    private final char displayChar;

    Symbol(char displayChar) {
        this.displayChar = displayChar;
    }

    public char getDisplayChar() {
        return displayChar;
    }

    @Override
    public String toString() {
        return String.valueOf(displayChar);
    }
}

