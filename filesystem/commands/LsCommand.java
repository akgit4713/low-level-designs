package filesystem.commands;

import filesystem.models.FileSystemNode;
import filesystem.services.FileSystem;
import filesystem.strategies.ListingStrategy;
import filesystem.strategies.ListingStrategyFactory;

import java.util.List;

/**
 * Command to list directory contents.
 * Usage: ls [-l] [directory_path]
 * Supports -l flag for detailed listing.
 */
public class LsCommand implements Command {
    
    private final FileSystem fileSystem;
    private final ListingStrategyFactory strategyFactory;
    
    public LsCommand(FileSystem fileSystem, ListingStrategyFactory strategyFactory) {
        this.fileSystem = fileSystem;
        this.strategyFactory = strategyFactory;
    }
    
    @Override
    public String execute(String[] args) {
        boolean detailed = false;
        String path = null;
        
        // Parse arguments
        for (String arg : args) {
            if (arg.equals("-l")) {
                detailed = true;
            } else if (!arg.startsWith("-")) {
                path = arg;
            }
        }
        
        List<FileSystemNode> nodes = fileSystem.listDirectory(path);
        
        ListingStrategy strategy = detailed 
                ? strategyFactory.getDetailedStrategy() 
                : strategyFactory.getDefaultStrategy();
        
        return strategy.format(nodes);
    }
    
    @Override
    public String getName() {
        return "ls";
    }
    
    @Override
    public String getUsage() {
        return "ls [-l] [directory] - list directory contents";
    }
}

