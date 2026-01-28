package filesystem;

import filesystem.commands.CommandFactory;
import filesystem.exceptions.FileSystemException;
import filesystem.services.FileSystem;
import filesystem.services.Shell;
import filesystem.strategies.ListingStrategyFactory;

import java.util.Scanner;

/**
 * Main entry point for the In-Memory File System.
 * Provides an interactive shell for file system operations.
 */
public class Main {
    
    public static void main(String[] args) {
        System.out.println("=== In-Memory File System ===");
        System.out.println("Type 'help' for available commands, 'exit' to quit.\n");
        
        // Initialize components
        FileSystem fileSystem = new FileSystem();
        ListingStrategyFactory listingStrategyFactory = new ListingStrategyFactory();
        CommandFactory commandFactory = new CommandFactory(fileSystem, listingStrategyFactory);
        Shell shell = new Shell(fileSystem, commandFactory);
        
        // Demo mode if running without interactive input
        if (args.length > 0 && args[0].equals("--demo")) {
            runDemo(shell);
            return;
        }
        
        // Interactive mode
        Scanner scanner = new Scanner(System.in);
        
        while (true) {
            System.out.print(shell.getPrompt());
            
            if (!scanner.hasNextLine()) {
                break;
            }
            
            String input = scanner.nextLine();
            
            try {
                String result = shell.execute(input);
                
                if (result.equals("exit")) {
                    System.out.println("Goodbye!");
                    break;
                }
                
                if (!result.isEmpty()) {
                    System.out.println(result);
                }
            } catch (FileSystemException e) {
                System.out.println("Error: " + e.getMessage());
            } catch (Exception e) {
                System.out.println("Unexpected error: " + e.getMessage());
            }
        }
        
        scanner.close();
    }
    
    /**
     * Runs a demonstration of file system operations.
     */
    private static void runDemo(Shell shell) {
        String[] commands = {
            "pwd",
            "mkdir home",
            "mkdir home/user",
            "mkdir home/user/documents",
            "ls",
            "cd home/user",
            "pwd",
            "touch notes.txt",
            "echo \"Hello, World!\" > notes.txt",
            "cat notes.txt",
            "echo \" More content\" >> notes.txt",
            "cat notes.txt",
            "touch readme.txt",
            "echo \"This is a readme file\" > readme.txt",
            "ls",
            "ls -l",
            "cd documents",
            "touch report.txt",
            "echo \"Quarterly Report\" > report.txt",
            "cd ..",
            "ls -l",
            "cd /",
            "ls -l home/user",
            "pwd"
        };
        
        System.out.println("Running demo...\n");
        
        for (String cmd : commands) {
            System.out.println(shell.getPrompt() + cmd);
            
            try {
                String result = shell.execute(cmd);
                if (!result.isEmpty() && !result.equals("exit")) {
                    System.out.println(result);
                }
            } catch (FileSystemException e) {
                System.out.println("Error: " + e.getMessage());
            }
            
            System.out.println();
        }
        
        System.out.println("Demo completed.");
    }
}

