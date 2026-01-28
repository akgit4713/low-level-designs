package filesystem.commands;

import filesystem.exceptions.InvalidCommandException;
import filesystem.services.FileSystem;

/**
 * Command to create a new directory.
 * Usage: mkdir <directory_path>
 */
public class MkdirCommand implements Command {
    
    private final FileSystem fileSystem;
    
    public MkdirCommand(FileSystem fileSystem) {
        this.fileSystem = fileSystem;
    }
    
    @Override
    public String execute(String[] args) {
        if (args.length < 1) {
            throw new InvalidCommandException("mkdir", "missing operand");
        }
        
        for (String path : args) {
            fileSystem.createDirectory(path);
        }
        
        return "";
    }
    
    @Override
    public String getName() {
        return "mkdir";
    }
    
    @Override
    public String getUsage() {
        return "mkdir <directory>... - create directories";
    }
}

