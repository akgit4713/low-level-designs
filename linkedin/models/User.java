package linkedin.models;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public class User {
    private final String id;
    private String email;
    private String passwordHash;
    private String name;
    private Profile profile;
    private final LocalDateTime createdAt;
    private LocalDateTime lastLoginAt;
    private boolean isActive;
    
    public User(String email, String passwordHash, String name) {
        this.id = UUID.randomUUID().toString();
        this.email = email;
        this.passwordHash = passwordHash;
        this.name = name;
        this.createdAt = LocalDateTime.now();
        this.isActive = true;
        this.profile = new Profile(this.id);
    }
    
    // Getters
    public String getId() { return id; }
    public String getEmail() { return email; }
    public String getPasswordHash() { return passwordHash; }
    public String getName() { return name; }
    public Profile getProfile() { return profile; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getLastLoginAt() { return lastLoginAt; }
    public boolean isActive() { return isActive; }
    
    // Setters
    public void setEmail(String email) { this.email = email; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    public void setName(String name) { this.name = name; }
    public void setProfile(Profile profile) { this.profile = profile; }
    public void setLastLoginAt(LocalDateTime lastLoginAt) { this.lastLoginAt = lastLoginAt; }
    public void setActive(boolean active) { isActive = active; }
    
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
        return "User{id='" + id + "', name='" + name + "', email='" + email + "'}";
    }
}



