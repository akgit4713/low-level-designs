package filesystem.commands;

import filesystem.exceptions.InvalidCommandException;
import filesystem.services.FileSystem;

import java.util.ArrayList;
import java.util.List;

/**
 * Command to display file contents.
 * Usage: cat <file_path> [file_path...]
 */
public class CatCommand implements Command {
    
    private final FileSystem fileSystem;
    
    public CatCommand(FileSystem fileSystem) {
        this.fileSystem = fileSystem;
    }
    
    @Override
    public String execute(String[] args) {
        if (args.length < 1) {
            throw new InvalidCommandException("cat", "missing file operand");
        }
        
        List<String> contents = new ArrayList<>();
        for (String path : args) {
            contents.add(fileSystem.readFile(path));
        }
        
        return String.join("", contents);
    }
    
    @Override
    public String getName() {
        return "cat";
    }
    
    @Override
    public String getUsage() {
        return "cat <file>... - display file contents";
    }
}

