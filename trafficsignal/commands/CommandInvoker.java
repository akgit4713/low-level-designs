package trafficsignal.commands;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Command Pattern: Invoker that executes commands and manages undo history.
 */
public class CommandInvoker {
    
    private final Deque<SignalCommand> commandHistory;
    private final int maxHistorySize;

    public CommandInvoker() {
        this(100);
    }

    public CommandInvoker(int maxHistorySize) {
        this.commandHistory = new ArrayDeque<>();
        this.maxHistorySize = maxHistorySize;
    }

    /**
     * Executes a command and adds it to history.
     */
    public void executeCommand(SignalCommand command) {
        command.execute();
        
        if (command.isUndoable()) {
            if (commandHistory.size() >= maxHistorySize) {
                commandHistory.removeLast();
            }
            commandHistory.push(command);
        }
    }

    /**
     * Undoes the last command.
     */
    public boolean undoLastCommand() {
        if (commandHistory.isEmpty()) {
            return false;
        }

        SignalCommand lastCommand = commandHistory.pop();
        lastCommand.undo();
        return true;
    }

    /**
     * Undoes all commands in history.
     */
    public void undoAll() {
        while (!commandHistory.isEmpty()) {
            undoLastCommand();
        }
    }

    /**
     * Gets the number of commands in history.
     */
    public int getHistorySize() {
        return commandHistory.size();
    }

    /**
     * Clears command history.
     */
    public void clearHistory() {
        commandHistory.clear();
    }

    /**
     * Peeks at the last command without removing it.
     */
    public SignalCommand peekLastCommand() {
        return commandHistory.peek();
    }
}



