package trafficsignal.commands;

/**
 * Command Pattern: Interface for signal control commands.
 * Allows encapsulation of operations and undo capability.
 */
public interface SignalCommand {
    
    /**
     * Executes the command.
     */
    void execute();

    /**
     * Undoes the command (restores previous state).
     */
    void undo();

    /**
     * Gets a description of this command.
     */
    String getDescription();

    /**
     * Checks if this command can be undone.
     */
    default boolean isUndoable() {
        return true;
    }
}



