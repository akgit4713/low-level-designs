package packagelocker.models;

import packagelocker.enums.TokenStatus;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Represents an access token for package retrieval.
 * This is a value object - immutable once created.
 */
public class AccessToken {
    
    private final String code;
    private final LocalDateTime createdAt;
    private final LocalDateTime expiresAt;
    private TokenStatus status;

    public AccessToken(String code, LocalDateTime createdAt, LocalDateTime expiresAt) {
        this.code = Objects.requireNonNull(code, "Access code cannot be null");
        this.createdAt = Objects.requireNonNull(createdAt, "Created time cannot be null");
        this.expiresAt = Objects.requireNonNull(expiresAt, "Expiry time cannot be null");
        this.status = TokenStatus.ACTIVE;
    }

    public String getCode() {
        return code;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public TokenStatus getStatus() {
        return status;
    }

    public boolean isExpired(LocalDateTime now) {
        return now.isAfter(expiresAt);
    }

    public boolean isActive() {
        return status == TokenStatus.ACTIVE;
    }

    public boolean isUsed() {
        return status == TokenStatus.USED;
    }

    public void markAsUsed() {
        if (status != TokenStatus.ACTIVE) {
            throw new IllegalStateException("Token is not active, current status: " + status);
        }
        this.status = TokenStatus.USED;
    }

    public void markAsExpired() {
        if (status == TokenStatus.USED) {
            return; // Already used tokens don't need to be marked expired
        }
        this.status = TokenStatus.EXPIRED;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AccessToken that = (AccessToken) o;
        return Objects.equals(code, that.code);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code);
    }

    @Override
    public String toString() {
        return String.format("AccessToken{code=%s****, expiresAt=%s, status=%s}", 
                code.substring(0, Math.min(4, code.length())), expiresAt, status);
    }
}
