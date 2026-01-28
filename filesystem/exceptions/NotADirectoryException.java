package filesystem.exceptions;

/**
 * Exception thrown when an operation expects a directory but receives a file.
 */
public class NotADirectoryException extends FileSystemException {
    
    private final String path;
    
    public NotADirectoryException(String path) {
        super("Not a directory: " + path);
        this.path = path;
    }
    
    public String getPath() {
        return path;
    }
}

