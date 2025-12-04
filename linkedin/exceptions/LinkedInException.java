package linkedin.exceptions;

public class LinkedInException extends RuntimeException {
    
    public LinkedInException(String message) {
        super(message);
    }
    
    public LinkedInException(String message, Throwable cause) {
        super(message, cause);
    }
}



