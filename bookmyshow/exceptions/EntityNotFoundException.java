package bookmyshow.exceptions;

/**
 * Thrown when an entity is not found.
 */
public class EntityNotFoundException extends BookMyShowException {
    
    public EntityNotFoundException(String entityType, String id) {
        super(String.format("%s not found with ID: %s", entityType, id));
    }
}



