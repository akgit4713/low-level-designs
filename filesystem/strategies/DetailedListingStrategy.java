package filesystem.strategies;

import filesystem.models.FileSystemNode;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Detailed listing strategy that shows file metadata.
 * Used for ls -l command, shows permissions, size, and timestamps.
 */
public class DetailedListingStrategy implements ListingStrategy {
    
    @Override
    public String format(List<FileSystemNode> nodes) {
        if (nodes.isEmpty()) {
            return "total 0";
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append("total ").append(nodes.size()).append("\n");
        
        String formatted = nodes.stream()
                .map(this::formatNode)
                .collect(Collectors.joining("\n"));
        
        sb.append(formatted);
        return sb.toString();
    }
    
    private String formatNode(FileSystemNode node) {
        StringBuilder sb = new StringBuilder();
        
        // File type indicator
        sb.append(node.isDirectory() ? "d" : "-");
        
        // Permissions (simulated)
        sb.append(node.isDirectory() ? "rwxr-xr-x" : "rw-r--r--");
        
        // Number of links (simulated)
        sb.append("  1 ");
        
        // Owner and group (simulated)
        sb.append("user  user  ");
        
        // Size (right-aligned, 8 characters)
        sb.append(String.format("%8d ", node.getSize()));
        
        // Modification time
        sb.append(node.getMetadata().getFormattedModifiedTime());
        sb.append(" ");
        
        // Name
        sb.append(node.getName());
        if (node.isDirectory()) {
            sb.append("/");
        }
        
        return sb.toString();
    }
    
    @Override
    public String getName() {
        return "detailed";
    }
}

