package packagelocker.models;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Value object representing the result of a successful package retrieval.
 * Immutable - returned to the customer after successful validation.
 */
public final class RetrievalResult {
    
    private final String packageId;
    private final int compartmentNumber;
    private final LocalDateTime retrievedAt;

    public RetrievalResult(String packageId, int compartmentNumber, LocalDateTime retrievedAt) {
        this.packageId = Objects.requireNonNull(packageId);
        this.compartmentNumber = compartmentNumber;
        this.retrievedAt = Objects.requireNonNull(retrievedAt);
    }

    public String getPackageId() {
        return packageId;
    }

    public int getCompartmentNumber() {
        return compartmentNumber;
    }

    public LocalDateTime getRetrievedAt() {
        return retrievedAt;
    }

    @Override
    public String toString() {
        return String.format(
            "RetrievalResult{packageId=%s, compartment=%d, retrievedAt=%s}",
            packageId, compartmentNumber, retrievedAt
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RetrievalResult that = (RetrievalResult) o;
        return compartmentNumber == that.compartmentNumber &&
               Objects.equals(packageId, that.packageId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(packageId, compartmentNumber);
    }
}
