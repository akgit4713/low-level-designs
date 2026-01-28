package packagelocker.services;

import packagelocker.enums.TokenStatus;
import packagelocker.exceptions.AlreadyUsedAccessTokenException;
import packagelocker.exceptions.ExpiredAccessTokenException;
import packagelocker.exceptions.InvalidAccessTokenException;
import packagelocker.models.AccessToken;
import packagelocker.models.Package;
import packagelocker.repositories.PackageRepository;
import packagelocker.strategies.AccessTokenGenerator;

import java.time.LocalDateTime;

/**
 * Service for managing access tokens.
 * Handles token generation, validation, and status management.
 */
public class AccessTokenService {
    
    private static final int DEFAULT_EXPIRY_DAYS = 7;
    
    private final AccessTokenGenerator tokenGenerator;
    private final PackageRepository packageRepository;
    private final Clock clock;
    private final int expiryDays;

    public AccessTokenService(AccessTokenGenerator tokenGenerator, 
                              PackageRepository packageRepository,
                              Clock clock) {
        this(tokenGenerator, packageRepository, clock, DEFAULT_EXPIRY_DAYS);
    }

    public AccessTokenService(AccessTokenGenerator tokenGenerator, 
                              PackageRepository packageRepository,
                              Clock clock, 
                              int expiryDays) {
        this.tokenGenerator = tokenGenerator;
        this.packageRepository = packageRepository;
        this.clock = clock;
        this.expiryDays = expiryDays;
    }

    /**
     * Generates a new access token with configured expiry.
     */
    public AccessToken generateToken() {
        LocalDateTime now = clock.now();
        String code = tokenGenerator.generate();
        
        // Ensure uniqueness
        while (packageRepository.findByAccessCode(code).isPresent()) {
            code = tokenGenerator.generate();
        }
        
        return new AccessToken(code, now, now.plusDays(expiryDays));
    }

    /**
     * Validates an access code and returns the associated package.
     * 
     * @throws InvalidAccessTokenException if code is not found
     * @throws ExpiredAccessTokenException if code has expired
     * @throws AlreadyUsedAccessTokenException if code was already used
     */
    public Package validateAndGetPackage(String accessCode) {
        Package pkg = packageRepository.findByAccessCode(accessCode)
                .orElseThrow(() -> new InvalidAccessTokenException(accessCode));
        
        AccessToken token = pkg.getAccessToken();
        
        // Check if already used
        if (token.getStatus() == TokenStatus.USED || pkg.isRetrieved()) {
            throw new AlreadyUsedAccessTokenException(accessCode);
        }
        
        // Check if expired
        if (token.isExpired(clock.now())) {
            token.markAsExpired();
            throw new ExpiredAccessTokenException(accessCode, token.getExpiresAt());
        }
        
        return pkg;
    }

    /**
     * Marks a token as used after successful retrieval.
     */
    public void markAsUsed(AccessToken token) {
        token.markAsUsed();
    }

    public int getExpiryDays() {
        return expiryDays;
    }
}
