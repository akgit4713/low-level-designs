package packagelocker.exceptions;

import packagelocker.enums.CompartmentSize;

/**
 * Thrown when no compartment of the requested size is available.
 */
public class NoAvailableCompartmentException extends LockerException {
    
    private final CompartmentSize requestedSize;

    public NoAvailableCompartmentException(CompartmentSize size) {
        super(String.format("No available compartment of size '%s'. Please try again later.", 
                size.getDisplayName()));
        this.requestedSize = size;
    }

    public CompartmentSize getRequestedSize() {
        return requestedSize;
    }
}
