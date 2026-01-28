package packagelocker.strategies;

import java.util.UUID;

/**
 * Generates access tokens using UUID.
 * Produces globally unique, hard-to-guess tokens.
 */
public class UUIDAccessTokenGenerator implements AccessTokenGenerator {
    
    @Override
    public String generate() {
        return UUID.randomUUID().toString().replace("-", "").toUpperCase();
    }

    @Override
    public String getGeneratorName() {
        return "UUID Generator";
    }
}
