package packagelocker.models;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Value object representing the result of a successful package deposit.
 * Immutable - returned to the carrier after deposit.
 */
public final class DepositResult {
    
    private final String packageId;
    private final int compartmentNumber;
    private final String accessCode;
    private final LocalDateTime expiresAt;

    public DepositResult(String packageId, int compartmentNumber, 
                         String accessCode, LocalDateTime expiresAt) {
        this.packageId = Objects.requireNonNull(packageId);
        this.compartmentNumber = compartmentNumber;
        this.accessCode = Objects.requireNonNull(accessCode);
        this.expiresAt = Objects.requireNonNull(expiresAt);
    }

    public String getPackageId() {
        return packageId;
    }

    public int getCompartmentNumber() {
        return compartmentNumber;
    }

    public String getAccessCode() {
        return accessCode;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    @Override
    public String toString() {
        return String.format(
            "DepositResult{packageId=%s, compartment=%d, accessCode=%s, expiresAt=%s}",
            packageId, compartmentNumber, accessCode, expiresAt
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DepositResult that = (DepositResult) o;
        return compartmentNumber == that.compartmentNumber &&
               Objects.equals(packageId, that.packageId) &&
               Objects.equals(accessCode, that.accessCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(packageId, compartmentNumber, accessCode);
    }
}
