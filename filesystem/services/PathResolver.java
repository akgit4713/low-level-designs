package filesystem.services;

import filesystem.exceptions.NotADirectoryException;
import filesystem.exceptions.PathNotFoundException;
import filesystem.models.Directory;
import filesystem.models.FileSystemNode;

/**
 * Resolves paths (absolute and relative) to file system nodes.
 * Handles path normalization and traversal.
 */
public class PathResolver {
    
    private static final String CURRENT_DIR = ".";
    private static final String PARENT_DIR = "..";
    
    /**
     * Resolves a path string to a FileSystemNode.
     * Supports both absolute paths (starting with /) and relative paths.
     *
     * @param path The path to resolve
     * @param currentDir The current working directory (for relative paths)
     * @param root The root directory
     * @return The resolved FileSystemNode
     * @throws PathNotFoundException if the path does not exist
     */
    public FileSystemNode resolve(String path, Directory currentDir, Directory root) {
        if (path == null || path.isEmpty()) {
            return currentDir;
        }
        
        Directory startDir = isAbsolutePath(path) ? root : currentDir;
        String[] components = normalizePath(path);
        
        FileSystemNode current = startDir;
        
        for (String component : components) {
            if (component.isEmpty() || component.equals(CURRENT_DIR)) {
                continue;
            }
            
            if (!current.isDirectory()) {
                throw new NotADirectoryException(current.getPath());
            }
            
            Directory dir = (Directory) current;
            
            if (component.equals(PARENT_DIR)) {
                current = dir.getParent() != null ? dir.getParent() : dir;
            } else {
                current = dir.getChild(component);
            }
        }
        
        return current;
    }
    
    /**
     * Resolves a path to a directory, throwing an exception if it's not a directory.
     */
    public Directory resolveDirectory(String path, Directory currentDir, Directory root) {
        FileSystemNode node = resolve(path, currentDir, root);
        if (!node.isDirectory()) {
            throw new NotADirectoryException(path);
        }
        return (Directory) node;
    }
    
    /**
     * Resolves a path to its parent directory and returns the final component name.
     * Useful for create operations where the parent must exist.
     */
    public PathResolution resolveParent(String path, Directory currentDir, Directory root) {
        if (path == null || path.isEmpty()) {
            throw new IllegalArgumentException("Path cannot be null or empty");
        }
        
        String[] components = normalizePath(path);
        if (components.length == 0) {
            throw new IllegalArgumentException("Invalid path: " + path);
        }
        
        String targetName = components[components.length - 1];
        
        // Build parent path
        StringBuilder parentPath = new StringBuilder();
        if (isAbsolutePath(path)) {
            parentPath.append("/");
        }
        for (int i = 0; i < components.length - 1; i++) {
            if (i > 0) {
                parentPath.append("/");
            }
            parentPath.append(components[i]);
        }
        
        Directory parentDir;
        if (components.length == 1) {
            parentDir = isAbsolutePath(path) ? root : currentDir;
        } else {
            parentDir = resolveDirectory(parentPath.toString(), currentDir, root);
        }
        
        return new PathResolution(parentDir, targetName);
    }
    
    /**
     * Normalizes a path string to an array of components.
     */
    private String[] normalizePath(String path) {
        if (path.equals("/")) {
            return new String[0];
        }
        
        String normalized = path.replaceAll("/+", "/");
        if (normalized.startsWith("/")) {
            normalized = normalized.substring(1);
        }
        if (normalized.endsWith("/")) {
            normalized = normalized.substring(0, normalized.length() - 1);
        }
        
        if (normalized.isEmpty()) {
            return new String[0];
        }
        
        return normalized.split("/");
    }
    
    /**
     * Checks if a path is absolute (starts with /).
     */
    public boolean isAbsolutePath(String path) {
        return path != null && path.startsWith("/");
    }
    
    /**
     * Represents the result of resolving a parent path.
     */
    public static class PathResolution {
        private final Directory parentDirectory;
        private final String targetName;
        
        public PathResolution(Directory parentDirectory, String targetName) {
            this.parentDirectory = parentDirectory;
            this.targetName = targetName;
        }
        
        public Directory getParentDirectory() {
            return parentDirectory;
        }
        
        public String getTargetName() {
            return targetName;
        }
    }
}

