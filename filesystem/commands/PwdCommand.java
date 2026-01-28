package filesystem.commands;

import filesystem.services.FileSystem;

/**
 * Command to print the current working directory.
 * Usage: pwd
 */
public class PwdCommand implements Command {
    
    private final FileSystem fileSystem;
    
    public PwdCommand(FileSystem fileSystem) {
        this.fileSystem = fileSystem;
    }
    
    @Override
    public String execute(String[] args) {
        return fileSystem.getCurrentPath();
    }
    
    @Override
    public String getName() {
        return "pwd";
    }
    
    @Override
    public String getUsage() {
        return "pwd - print current working directory";
    }
}

