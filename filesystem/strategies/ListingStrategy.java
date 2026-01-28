package filesystem.strategies;

import filesystem.models.FileSystemNode;

import java.util.List;

/**
 * Strategy interface for formatting directory listings (Strategy Pattern).
 * Allows different output formats for the ls command.
 */
public interface ListingStrategy {
    
    /**
     * Formats a list of file system nodes for display.
     *
     * @param nodes The nodes to format
     * @return Formatted string representation
     */
    String format(List<FileSystemNode> nodes);
    
    /**
     * Returns the name/identifier of this listing strategy.
     */
    String getName();
}

