package socialnetwork.models;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * Represents an active user session.
 */
public class Session {
    private final String token;
    private final String userId;
    private final LocalDateTime createdAt;
    private LocalDateTime lastAccessedAt;
    private final LocalDateTime expiresAt;
    private boolean isActive;

    public Session(String userId, int expirationHours) {
        this.token = UUID.randomUUID().toString();
        this.userId = userId;
        this.createdAt = LocalDateTime.now();
        this.lastAccessedAt = this.createdAt;
        this.expiresAt = this.createdAt.plusHours(expirationHours);
        this.isActive = true;
    }

    // Getters
    public String getToken() { return token; }
    public String getUserId() { return userId; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getLastAccessedAt() { return lastAccessedAt; }
    public LocalDateTime getExpiresAt() { return expiresAt; }
    public boolean isActive() { return isActive; }

    // Session management
    public void updateLastAccess() {
        this.lastAccessedAt = LocalDateTime.now();
    }

    public void invalidate() {
        this.isActive = false;
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiresAt);
    }

    public boolean isValid() {
        return isActive && !isExpired();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Session session = (Session) o;
        return Objects.equals(token, session.token);
    }

    @Override
    public int hashCode() {
        return Objects.hash(token);
    }

    @Override
    public String toString() {
        return "Session{token='" + token.substring(0, 8) + "...', userId='" + userId + 
               "', valid=" + isValid() + "}";
    }
}



