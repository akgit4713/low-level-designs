package filesystem.exceptions;

/**
 * Base exception for all file system related errors.
 */
public class FileSystemException extends RuntimeException {
    
    public FileSystemException(String message) {
        super(message);
    }
    
    public FileSystemException(String message, Throwable cause) {
        super(message, cause);
    }
}

