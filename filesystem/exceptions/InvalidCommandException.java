package filesystem.exceptions;

/**
 * Exception thrown when a command is invalid or malformed.
 */
public class InvalidCommandException extends FileSystemException {
    
    private final String command;
    
    public InvalidCommandException(String command, String reason) {
        super("Invalid command '" + command + "': " + reason);
        this.command = command;
    }
    
    public String getCommand() {
        return command;
    }
}

