package filesystem.commands;

import filesystem.services.FileSystem;

/**
 * Command to change the current working directory.
 * Usage: cd [directory_path]
 */
public class CdCommand implements Command {
    
    private final FileSystem fileSystem;
    
    public CdCommand(FileSystem fileSystem) {
        this.fileSystem = fileSystem;
    }
    
    @Override
    public String execute(String[] args) {
        String path;
        if (args.length == 0 || args[0].isEmpty()) {
            path = "/"; // Go to root if no argument
        } else {
            path = args[0];
        }
        
        fileSystem.changeDirectory(path);
        return "";
    }
    
    @Override
    public String getName() {
        return "cd";
    }
    
    @Override
    public String getUsage() {
        return "cd [directory] - change current directory";
    }
}

