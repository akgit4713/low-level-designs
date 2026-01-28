package filesystem.exceptions;

/**
 * Exception thrown when a specified path does not exist.
 */
public class PathNotFoundException extends FileSystemException {
    
    private final String path;
    
    public PathNotFoundException(String path) {
        super("Path not found: " + path);
        this.path = path;
    }
    
    public String getPath() {
        return path;
    }
}

