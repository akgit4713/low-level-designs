package packagelocker.strategies;

import java.security.SecureRandom;

/**
 * Generates numeric access tokens (e.g., "847293").
 * Easier for users to remember and type on a keypad.
 */
public class NumericAccessTokenGenerator implements AccessTokenGenerator {
    
    private static final int DEFAULT_LENGTH = 6;
    private final SecureRandom random;
    private final int length;

    public NumericAccessTokenGenerator() {
        this(DEFAULT_LENGTH);
    }

    public NumericAccessTokenGenerator(int length) {
        if (length < 4 || length > 12) {
            throw new IllegalArgumentException("Token length must be between 4 and 12");
        }
        this.random = new SecureRandom();
        this.length = length;
    }

    @Override
    public String generate() {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }

    @Override
    public String getGeneratorName() {
        return "Numeric Generator (" + length + " digits)";
    }
}
