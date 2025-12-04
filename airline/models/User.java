package airline.models;

import airline.enums.UserRole;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Represents a system user with role-based access.
 */
public class User {
    private final String id;
    private final String username;
    private final String email;
    private String passwordHash; // In real system, properly hashed
    private final UserRole role;
    private final LocalDateTime createdAt;
    private volatile LocalDateTime lastLoginAt;
    private volatile boolean active;

    private User(Builder builder) {
        this.id = builder.id != null ? builder.id :
                "USR-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        this.username = builder.username;
        this.email = builder.email;
        this.passwordHash = builder.passwordHash;
        this.role = builder.role;
        this.createdAt = LocalDateTime.now();
        this.active = true;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public UserRole getRole() {
        return role;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getLastLoginAt() {
        return lastLoginAt;
    }

    public boolean isActive() {
        return active;
    }

    public void recordLogin() {
        this.lastLoginAt = LocalDateTime.now();
    }

    public void deactivate() {
        this.active = false;
    }

    public void activate() {
        this.active = true;
    }

    public boolean canManageFlights() {
        return role.canManageFlights();
    }

    public boolean canManageCrew() {
        return role.canManageCrew();
    }

    public boolean canViewAllBookings() {
        return role.canViewAllBookings();
    }

    public boolean canProcessRefunds() {
        return role.canProcessRefunds();
    }

    @Override
    public String toString() {
        return String.format("User[%s | %s | %s | %s]",
                id, username, role, active ? "Active" : "Inactive");
    }

    public static class Builder {
        private String id;
        private String username;
        private String email;
        private String passwordHash;
        private UserRole role;

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder username(String username) {
            this.username = username;
            return this;
        }

        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public Builder passwordHash(String passwordHash) {
            this.passwordHash = passwordHash;
            return this;
        }

        public Builder role(UserRole role) {
            this.role = role;
            return this;
        }

        public User build() {
            if (username == null || email == null || role == null) {
                throw new IllegalStateException("User requires username, email, and role");
            }
            return new User(this);
        }
    }
}



