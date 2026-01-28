package filesystem.exceptions;

/**
 * Exception thrown when trying to create a node at a path that already exists.
 */
public class PathAlreadyExistsException extends FileSystemException {
    
    private final String path;
    
    public PathAlreadyExistsException(String path) {
        super("Path already exists: " + path);
        this.path = path;
    }
    
    public String getPath() {
        return path;
    }
}

