package filesystem.services;

import filesystem.commands.Command;
import filesystem.commands.CommandFactory;
import filesystem.exceptions.InvalidCommandException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Shell that parses and executes string-based commands.
 * Provides the interface between user input and the file system.
 */
public class Shell {
    
    private final FileSystem fileSystem;
    private final CommandFactory commandFactory;
    
    // Pattern to match quoted strings or non-whitespace sequences
    private static final Pattern TOKEN_PATTERN = Pattern.compile("\"([^\"]*)\"|'([^']*)'|(\\S+)");
    
    public Shell(FileSystem fileSystem, CommandFactory commandFactory) {
        this.fileSystem = fileSystem;
        this.commandFactory = commandFactory;
    }
    
    /**
     * Executes a command string and returns the result.
     *
     * @param input The command string to execute
     * @return The result of command execution
     */
    public String execute(String input) {
        if (input == null || input.trim().isEmpty()) {
            return "";
        }
        
        String[] tokens = tokenize(input.trim());
        if (tokens.length == 0) {
            return "";
        }
        
        String commandName = tokens[0];
        String[] args = new String[tokens.length - 1];
        System.arraycopy(tokens, 1, args, 0, args.length);
        
        // Handle special commands
        if (commandName.equals("help")) {
            return commandFactory.getHelp();
        }
        
        if (commandName.equals("exit") || commandName.equals("quit")) {
            return "exit";
        }
        
        Optional<Command> command = commandFactory.getCommand(commandName);
        if (command.isEmpty()) {
            throw new InvalidCommandException(commandName, "command not found");
        }
        
        return command.get().execute(args);
    }
    
    /**
     * Tokenizes a command string, handling quoted arguments.
     */
    private String[] tokenize(String input) {
        List<String> tokens = new ArrayList<>();
        Matcher matcher = TOKEN_PATTERN.matcher(input);
        
        while (matcher.find()) {
            if (matcher.group(1) != null) {
                // Double-quoted string
                tokens.add("\"" + matcher.group(1) + "\"");
            } else if (matcher.group(2) != null) {
                // Single-quoted string
                tokens.add("'" + matcher.group(2) + "'");
            } else if (matcher.group(3) != null) {
                // Unquoted token
                tokens.add(matcher.group(3));
            }
        }
        
        return tokens.toArray(new String[0]);
    }
    
    /**
     * Returns the current prompt string.
     */
    public String getPrompt() {
        return fileSystem.getCurrentPath() + " $ ";
    }
    
    /**
     * Returns the file system instance.
     */
    public FileSystem getFileSystem() {
        return fileSystem;
    }
    
    /**
     * Returns the command factory instance.
     */
    public CommandFactory getCommandFactory() {
        return commandFactory;
    }
}

