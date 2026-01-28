package filesystem.exceptions;

/**
 * Exception thrown when an operation expects a file but receives a directory.
 */
public class NotAFileException extends FileSystemException {
    
    private final String path;
    
    public NotAFileException(String path) {
        super("Not a file: " + path);
        this.path = path;
    }
    
    public String getPath() {
        return path;
    }
}

