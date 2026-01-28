package packagelocker.strategies;

/**
 * Strategy interface for generating access token codes.
 * Allows different token generation mechanisms.
 */
public interface AccessTokenGenerator {
    
    /**
     * Generates a unique access token code.
     * 
     * @return a unique token code string
     */
    String generate();
    
    /**
     * Returns the name of this generator.
     */
    String getGeneratorName();
}
