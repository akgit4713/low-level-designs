package packagelocker.exceptions;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Thrown when an access token has expired.
 */
public class ExpiredAccessTokenException extends LockerException {
    
    private final String accessCode;
    private final LocalDateTime expiredAt;

    public ExpiredAccessTokenException(String accessCode, LocalDateTime expiredAt) {
        super(String.format("Access code expired on %s. Package may have been returned to sender.", 
                expiredAt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))));
        this.accessCode = accessCode;
        this.expiredAt = expiredAt;
    }

    public String getAccessCode() {
        return accessCode;
    }

    public LocalDateTime getExpiredAt() {
        return expiredAt;
    }
}
