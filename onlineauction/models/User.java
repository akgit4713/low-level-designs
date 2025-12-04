package onlineauction.models;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * Represents a registered user in the auction system.
 * Immutable after creation (except for profile updates via builder).
 */
public class User {
    
    private final String id;
    private final String username;
    private final String email;
    private final String passwordHash;
    private final Instant createdAt;
    private final Instant lastLoginAt;
    private final boolean active;
    
    private User(Builder builder) {
        this.id = builder.id;
        this.username = builder.username;
        this.email = builder.email;
        this.passwordHash = builder.passwordHash;
        this.createdAt = builder.createdAt;
        this.lastLoginAt = builder.lastLoginAt;
        this.active = builder.active;
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
    
    public String getPasswordHash() {
        return passwordHash;
    }
    
    public Instant getCreatedAt() {
        return createdAt;
    }
    
    public Instant getLastLoginAt() {
        return lastLoginAt;
    }
    
    public boolean isActive() {
        return active;
    }
    
    /**
     * Create a new builder with updated last login time
     */
    public Builder toBuilder() {
        return new Builder()
                .id(this.id)
                .username(this.username)
                .email(this.email)
                .passwordHash(this.passwordHash)
                .createdAt(this.createdAt)
                .lastLoginAt(this.lastLoginAt)
                .active(this.active);
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    
    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", active=" + active +
                '}';
    }
    
    public static class Builder {
        private String id;
        private String username;
        private String email;
        private String passwordHash;
        private Instant createdAt;
        private Instant lastLoginAt;
        private boolean active = true;
        
        public Builder() {
            this.id = UUID.randomUUID().toString();
            this.createdAt = Instant.now();
        }
        
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
        
        public Builder createdAt(Instant createdAt) {
            this.createdAt = createdAt;
            return this;
        }
        
        public Builder lastLoginAt(Instant lastLoginAt) {
            this.lastLoginAt = lastLoginAt;
            return this;
        }
        
        public Builder active(boolean active) {
            this.active = active;
            return this;
        }
        
        public User build() {
            Objects.requireNonNull(username, "Username is required");
            Objects.requireNonNull(email, "Email is required");
            Objects.requireNonNull(passwordHash, "Password hash is required");
            return new User(this);
        }
    }
}



