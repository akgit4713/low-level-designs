package tictactoe.players;

import tictactoe.models.Board;
import tictactoe.models.Move;
import tictactoe.models.Symbol;

import java.util.Scanner;

/**
 * Human player that takes input from console.
 * Single Responsibility: Handling human input for moves.
 */
public class HumanPlayer extends Player {
    
    private final Scanner scanner;

    public HumanPlayer(String name, Symbol symbol) {
        super(name, symbol);
        this.scanner = new Scanner(System.in);
    }

    public HumanPlayer(String name, Symbol symbol, Scanner scanner) {
        super(name, symbol);
        this.scanner = scanner;
    }

    @Override
    public Move makeMove(Board board) {
        int row, col;
        
        while (true) {
            System.out.print(name + "'s turn (" + symbol + "). Enter row and column (0-" + 
                           (board.getSize() - 1) + "): ");
            
            try {
                row = scanner.nextInt();
                col = scanner.nextInt();
                
                if (!board.isValidPosition(row, col)) {
                    System.out.println("Invalid position! Please enter values between 0 and " + 
                                     (board.getSize() - 1));
                    continue;
                }
                
                if (!isValidMove(board, row, col)) {
                    System.out.println("Cell is already occupied! Choose another cell.");
                    continue;
                }
                
                break;
            } catch (Exception e) {
                System.out.println("Invalid input! Please enter two numbers separated by space.");
                scanner.nextLine(); // Clear the buffer
            }
        }
        
        return new Move(row, col, this);
    }
}

