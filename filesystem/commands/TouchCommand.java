package filesystem.commands;

import filesystem.exceptions.InvalidCommandException;
import filesystem.exceptions.PathAlreadyExistsException;
import filesystem.services.FileSystem;

/**
 * Command to create a new empty file or update timestamp.
 * Usage: touch <file_path>
 */
public class TouchCommand implements Command {
    
    private final FileSystem fileSystem;
    
    public TouchCommand(FileSystem fileSystem) {
        this.fileSystem = fileSystem;
    }
    
    @Override
    public String execute(String[] args) {
        if (args.length < 1) {
            throw new InvalidCommandException("touch", "missing file operand");
        }
        
        for (String path : args) {
            try {
                fileSystem.createFile(path);
            } catch (PathAlreadyExistsException e) {
                // Touch just updates timestamp if file exists
                // For simplicity, we ignore if it already exists
            }
        }
        
        return "";
    }
    
    @Override
    public String getName() {
        return "touch";
    }
    
    @Override
    public String getUsage() {
        return "touch <file>... - create empty files";
    }
}

