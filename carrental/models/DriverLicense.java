package carrental.models;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Represents a driver's license.
 * Value object - immutable.
 */
public class DriverLicense {
    private final String licenseNumber;
    private final String state;
    private final LocalDate issueDate;
    private final LocalDate expiryDate;

    public DriverLicense(String licenseNumber, String state, LocalDate issueDate, LocalDate expiryDate) {
        this.licenseNumber = Objects.requireNonNull(licenseNumber, "License number cannot be null");
        this.state = Objects.requireNonNull(state, "State cannot be null");
        this.issueDate = Objects.requireNonNull(issueDate, "Issue date cannot be null");
        this.expiryDate = Objects.requireNonNull(expiryDate, "Expiry date cannot be null");
    }

    public String getLicenseNumber() { return licenseNumber; }
    public String getState() { return state; }
    public LocalDate getIssueDate() { return issueDate; }
    public LocalDate getExpiryDate() { return expiryDate; }

    public boolean isValid() {
        return expiryDate.isAfter(LocalDate.now());
    }

    public boolean isExpiringSoon(int daysThreshold) {
        return expiryDate.isBefore(LocalDate.now().plusDays(daysThreshold));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DriverLicense that = (DriverLicense) o;
        return Objects.equals(licenseNumber, that.licenseNumber) &&
               Objects.equals(state, that.state);
    }

    @Override
    public int hashCode() {
        return Objects.hash(licenseNumber, state);
    }

    @Override
    public String toString() {
        return String.format("DriverLicense{number='%s', state='%s', expires=%s}", 
            licenseNumber, state, expiryDate);
    }
}



