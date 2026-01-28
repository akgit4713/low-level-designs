package filesystem.commands;

import filesystem.services.FileSystem;
import filesystem.strategies.ListingStrategyFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Factory for creating and managing shell commands.
 * Supports registration of custom commands for extensibility.
 */
public class CommandFactory {
    
    private final Map<String, Command> commands;
    
    public CommandFactory(FileSystem fileSystem, ListingStrategyFactory listingStrategyFactory) {
        this.commands = new HashMap<>();
        registerDefaultCommands(fileSystem, listingStrategyFactory);
    }
    
    private void registerDefaultCommands(FileSystem fileSystem, ListingStrategyFactory listingStrategyFactory) {
        register(new MkdirCommand(fileSystem));
        register(new CdCommand(fileSystem));
        register(new TouchCommand(fileSystem));
        register(new LsCommand(fileSystem, listingStrategyFactory));
        register(new PwdCommand(fileSystem));
        register(new CatCommand(fileSystem));
        register(new EchoCommand(fileSystem));
    }
    
    /**
     * Registers a custom command.
     *
     * @param command The command to register
     */
    public void register(Command command) {
        commands.put(command.getName(), command);
    }
    
    /**
     * Gets a command by name.
     *
     * @param name The command name
     * @return Optional containing the command if found
     */
    public Optional<Command> getCommand(String name) {
        return Optional.ofNullable(commands.get(name));
    }
    
    /**
     * Checks if a command exists.
     */
    public boolean hasCommand(String name) {
        return commands.containsKey(name);
    }
    
    /**
     * Returns all registered command names.
     */
    public Set<String> getCommandNames() {
        return commands.keySet();
    }
    
    /**
     * Returns help text for all commands.
     */
    public String getHelp() {
        StringBuilder sb = new StringBuilder();
        sb.append("Available commands:\n");
        for (Command cmd : commands.values()) {
            sb.append("  ").append(cmd.getUsage()).append("\n");
        }
        return sb.toString();
    }
}

