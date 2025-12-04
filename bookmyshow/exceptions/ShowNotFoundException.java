package bookmyshow.exceptions;

/**
 * Thrown when a show is not found.
 */
public class ShowNotFoundException extends BookMyShowException {
    
    public ShowNotFoundException(String showId) {
        super(String.format("Show not found with ID: %s", showId));
    }
}



