package airline.models;

import airline.enums.CrewRole;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

/**
 * Represents a crew member with role and certifications.
 */
public class Crew {
    private final String id;
    private final String employeeId;
    private final String firstName;
    private final String lastName;
    private final CrewRole role;
    private final Set<String> certifications;
    private final LocalDate licenseExpiryDate;
    private volatile boolean available;

    private Crew(Builder builder) {
        this.id = builder.id;
        this.employeeId = builder.employeeId;
        this.firstName = builder.firstName;
        this.lastName = builder.lastName;
        this.role = builder.role;
        this.certifications = new HashSet<>(builder.certifications);
        this.licenseExpiryDate = builder.licenseExpiryDate;
        this.available = true;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getId() {
        return id;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    public CrewRole getRole() {
        return role;
    }

    public Set<String> getCertifications() {
        return new HashSet<>(certifications);
    }

    public boolean hasCertification(String certification) {
        return certifications.contains(certification);
    }

    public void addCertification(String certification) {
        certifications.add(certification);
    }

    public LocalDate getLicenseExpiryDate() {
        return licenseExpiryDate;
    }

    public boolean isLicenseValid() {
        return licenseExpiryDate == null || licenseExpiryDate.isAfter(LocalDate.now());
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public boolean canOperateAircraft(String aircraftModel) {
        return certifications.contains(aircraftModel);
    }

    @Override
    public String toString() {
        return String.format("Crew[%s | %s %s | %s | %s]",
                employeeId, firstName, lastName, role.getDisplayName(),
                available ? "Available" : "On Duty");
    }

    public static class Builder {
        private String id;
        private String employeeId;
        private String firstName;
        private String lastName;
        private CrewRole role;
        private Set<String> certifications = new HashSet<>();
        private LocalDate licenseExpiryDate;

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder employeeId(String employeeId) {
            this.employeeId = employeeId;
            return this;
        }

        public Builder firstName(String firstName) {
            this.firstName = firstName;
            return this;
        }

        public Builder lastName(String lastName) {
            this.lastName = lastName;
            return this;
        }

        public Builder role(CrewRole role) {
            this.role = role;
            return this;
        }

        public Builder certifications(Set<String> certifications) {
            this.certifications = new HashSet<>(certifications);
            return this;
        }

        public Builder addCertification(String certification) {
            this.certifications.add(certification);
            return this;
        }

        public Builder licenseExpiryDate(LocalDate licenseExpiryDate) {
            this.licenseExpiryDate = licenseExpiryDate;
            return this;
        }

        public Crew build() {
            if (id == null || employeeId == null || firstName == null ||
                    lastName == null || role == null) {
                throw new IllegalStateException("Crew requires id, employeeId, firstName, lastName, and role");
            }
            return new Crew(this);
        }
    }
}



