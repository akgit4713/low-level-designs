package filesystem.commands;

/**
 * Command interface for shell commands (Command Pattern).
 * Each command encapsulates the logic for a specific file system operation.
 */
public interface Command {
    
    /**
     * Executes the command with the given arguments.
     *
     * @param args The command arguments
     * @return The output of the command (may be empty)
     */
    String execute(String[] args);
    
    /**
     * Returns the name of this command.
     */
    String getName();
    
    /**
     * Returns the usage/help message for this command.
     */
    String getUsage();
}

