package filesystem.strategies;

import filesystem.models.FileSystemNode;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Simple listing strategy that shows only node names.
 * Default format for ls command without flags.
 */
public class SimpleListingStrategy implements ListingStrategy {
    
    @Override
    public String format(List<FileSystemNode> nodes) {
        if (nodes.isEmpty()) {
            return "";
        }
        
        return nodes.stream()
                .map(this::formatNode)
                .collect(Collectors.joining("  "));
    }
    
    private String formatNode(FileSystemNode node) {
        if (node.isDirectory()) {
            return node.getName() + "/";
        }
        return node.getName();
    }
    
    @Override
    public String getName() {
        return "simple";
    }
}

