package socialnetwork.models;

import socialnetwork.enums.PrivacyLevel;
import socialnetwork.enums.UserStatus;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * Represents a user in the social network.
 * Contains profile information and account settings.
 */
public class User {
    private final String id;
    private String email;
    private String passwordHash;
    private String name;
    private String bio;
    private String profilePictureUrl;
    private String interests;
    private UserStatus status;
    private PrivacyLevel profilePrivacy;
    private PrivacyLevel defaultPostPrivacy;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private User(Builder builder) {
        this.id = builder.id != null ? builder.id : UUID.randomUUID().toString();
        this.email = builder.email;
        this.passwordHash = builder.passwordHash;
        this.name = builder.name;
        this.bio = builder.bio;
        this.profilePictureUrl = builder.profilePictureUrl;
        this.interests = builder.interests;
        this.status = builder.status != null ? builder.status : UserStatus.ACTIVE;
        this.profilePrivacy = builder.profilePrivacy != null ? builder.profilePrivacy : PrivacyLevel.PUBLIC;
        this.defaultPostPrivacy = builder.defaultPostPrivacy != null ? builder.defaultPostPrivacy : PrivacyLevel.FRIENDS_ONLY;
        this.createdAt = builder.createdAt != null ? builder.createdAt : LocalDateTime.now();
        this.updatedAt = this.createdAt;
    }

    // Getters
    public String getId() { return id; }
    public String getEmail() { return email; }
    public String getPasswordHash() { return passwordHash; }
    public String getName() { return name; }
    public String getBio() { return bio; }
    public String getProfilePictureUrl() { return profilePictureUrl; }
    public String getInterests() { return interests; }
    public UserStatus getStatus() { return status; }
    public PrivacyLevel getProfilePrivacy() { return profilePrivacy; }
    public PrivacyLevel getDefaultPostPrivacy() { return defaultPostPrivacy; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }

    // Setters for mutable fields
    public void setName(String name) {
        this.name = name;
        this.updatedAt = LocalDateTime.now();
    }

    public void setBio(String bio) {
        this.bio = bio;
        this.updatedAt = LocalDateTime.now();
    }

    public void setProfilePictureUrl(String profilePictureUrl) {
        this.profilePictureUrl = profilePictureUrl;
        this.updatedAt = LocalDateTime.now();
    }

    public void setInterests(String interests) {
        this.interests = interests;
        this.updatedAt = LocalDateTime.now();
    }

    public void setStatus(UserStatus status) {
        this.status = status;
        this.updatedAt = LocalDateTime.now();
    }

    public void setProfilePrivacy(PrivacyLevel profilePrivacy) {
        this.profilePrivacy = profilePrivacy;
        this.updatedAt = LocalDateTime.now();
    }

    public void setDefaultPostPrivacy(PrivacyLevel defaultPostPrivacy) {
        this.defaultPostPrivacy = defaultPostPrivacy;
        this.updatedAt = LocalDateTime.now();
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
        this.updatedAt = LocalDateTime.now();
    }

    public boolean isActive() {
        return status == UserStatus.ACTIVE;
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
        return "User{id='" + id + "', name='" + name + "', email='" + email + "'}";
    }

    // Builder Pattern
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String id;
        private String email;
        private String passwordHash;
        private String name;
        private String bio;
        private String profilePictureUrl;
        private String interests;
        private UserStatus status;
        private PrivacyLevel profilePrivacy;
        private PrivacyLevel defaultPostPrivacy;
        private LocalDateTime createdAt;

        public Builder id(String id) { this.id = id; return this; }
        public Builder email(String email) { this.email = email; return this; }
        public Builder passwordHash(String passwordHash) { this.passwordHash = passwordHash; return this; }
        public Builder name(String name) { this.name = name; return this; }
        public Builder bio(String bio) { this.bio = bio; return this; }
        public Builder profilePictureUrl(String url) { this.profilePictureUrl = url; return this; }
        public Builder interests(String interests) { this.interests = interests; return this; }
        public Builder status(UserStatus status) { this.status = status; return this; }
        public Builder profilePrivacy(PrivacyLevel privacy) { this.profilePrivacy = privacy; return this; }
        public Builder defaultPostPrivacy(PrivacyLevel privacy) { this.defaultPostPrivacy = privacy; return this; }
        public Builder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }

        public User build() {
            if (email == null || email.isBlank()) {
                throw new IllegalArgumentException("Email is required");
            }
            if (name == null || name.isBlank()) {
                throw new IllegalArgumentException("Name is required");
            }
            if (passwordHash == null || passwordHash.isBlank()) {
                throw new IllegalArgumentException("Password is required");
            }
            return new User(this);
        }
    }
}



