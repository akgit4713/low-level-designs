package packagelocker.models;

import packagelocker.enums.CompartmentSize;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Represents a package deposited in the locker.
 */
public class Package {
    
    private final String id;
    private final CompartmentSize size;
    private final String compartmentId;
    private final AccessToken accessToken;
    private final LocalDateTime depositTime;
    private LocalDateTime retrievalTime;
    private boolean retrieved;

    public Package(String id, CompartmentSize size, String compartmentId, 
                   AccessToken accessToken, LocalDateTime depositTime) {
        this.id = Objects.requireNonNull(id, "Package ID cannot be null");
        this.size = Objects.requireNonNull(size, "Package size cannot be null");
        this.compartmentId = Objects.requireNonNull(compartmentId, "Compartment ID cannot be null");
        this.accessToken = Objects.requireNonNull(accessToken, "Access token cannot be null");
        this.depositTime = Objects.requireNonNull(depositTime, "Deposit time cannot be null");
        this.retrieved = false;
    }

    public String getId() {
        return id;
    }

    public CompartmentSize getSize() {
        return size;
    }

    public String getCompartmentId() {
        return compartmentId;
    }

    public AccessToken getAccessToken() {
        return accessToken;
    }

    public LocalDateTime getDepositTime() {
        return depositTime;
    }

    public LocalDateTime getRetrievalTime() {
        return retrievalTime;
    }

    public boolean isRetrieved() {
        return retrieved;
    }

    public void markAsRetrieved(LocalDateTime retrievalTime) {
        if (retrieved) {
            throw new IllegalStateException("Package has already been retrieved");
        }
        this.retrieved = true;
        this.retrievalTime = retrievalTime;
        this.accessToken.markAsUsed();
    }

    public boolean isExpired(LocalDateTime now) {
        return accessToken.isExpired(now);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Package aPackage = (Package) o;
        return Objects.equals(id, aPackage.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("Package{id=%s, size=%s, compartmentId=%s, retrieved=%s}", 
                id, size.getDisplayName(), compartmentId, retrieved);
    }
}
