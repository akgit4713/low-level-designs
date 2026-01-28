package packagelocker.tests;

import packagelocker.enums.CompartmentSize;
import packagelocker.enums.TokenStatus;
import packagelocker.exceptions.AlreadyUsedAccessTokenException;
import packagelocker.exceptions.ExpiredAccessTokenException;
import packagelocker.exceptions.InvalidAccessTokenException;
import packagelocker.models.AccessToken;
import packagelocker.models.Package;
import packagelocker.repositories.PackageRepository;
import packagelocker.repositories.impl.InMemoryPackageRepository;
import packagelocker.services.AccessTokenService;
import packagelocker.services.Clock;
import packagelocker.strategies.UUIDAccessTokenGenerator;

import java.time.LocalDateTime;

/**
 * Unit tests for AccessTokenService.
 */
public class AccessTokenServiceTest {
    
    private static int testsRun = 0;
    private static int testsPassed = 0;
    private static int testsFailed = 0;

    public static void main(String[] args) {
        System.out.println("╔══════════════════════════════════════════════════════════════╗");
        System.out.println("║          ACCESS TOKEN SERVICE - UNIT TESTS                   ║");
        System.out.println("╚══════════════════════════════════════════════════════════════╝");
        System.out.println();

        testGenerateToken_CreatesValidToken();
        testGenerateToken_ExpiresIn7Days();
        testValidateAndGetPackage_Success();
        testValidateAndGetPackage_InvalidCode();
        testValidateAndGetPackage_ExpiredToken();
        testValidateAndGetPackage_AlreadyUsed();
        testMarkAsUsed_ChangesStatus();
        
        // Print summary
        System.out.println("\n" + "═".repeat(60));
        System.out.println("Total: " + testsRun + " | Passed: " + testsPassed + 
                " ✅ | Failed: " + testsFailed + " ❌");
        System.out.println("═".repeat(60));
    }

    private static void testGenerateToken_CreatesValidToken() {
        String testName = "testGenerateToken_CreatesValidToken";
        try {
            // Arrange
            LocalDateTime now = LocalDateTime.now();
            AccessTokenService service = createService(now);
            
            // Act
            AccessToken token = service.generateToken();
            
            // Assert
            assertNotNull(token, "Token should not be null");
            assertNotNull(token.getCode(), "Code should not be null");
            assertTrue(token.getCode().length() > 0, "Code should not be empty");
            assertEquals(TokenStatus.ACTIVE, token.getStatus(), "Should be active");
            
            pass(testName);
        } catch (AssertionError | Exception e) {
            fail(testName, e);
        }
    }

    private static void testGenerateToken_ExpiresIn7Days() {
        String testName = "testGenerateToken_ExpiresIn7Days";
        try {
            // Arrange
            LocalDateTime now = LocalDateTime.of(2024, 1, 1, 12, 0);
            AccessTokenService service = createService(now);
            
            // Act
            AccessToken token = service.generateToken();
            
            // Assert
            LocalDateTime expectedExpiry = now.plusDays(7);
            assertEquals(expectedExpiry, token.getExpiresAt(), "Should expire in 7 days");
            
            pass(testName);
        } catch (AssertionError | Exception e) {
            fail(testName, e);
        }
    }

    private static void testValidateAndGetPackage_Success() {
        String testName = "testValidateAndGetPackage_Success";
        try {
            // Arrange
            LocalDateTime now = LocalDateTime.now();
            PackageRepository repo = new InMemoryPackageRepository();
            AccessTokenService service = new AccessTokenService(
                    new UUIDAccessTokenGenerator(), repo, () -> now);
            
            AccessToken token = service.generateToken();
            Package pkg = new Package("pkg1", CompartmentSize.SMALL, "comp1", token, now);
            repo.save(pkg);
            
            // Act
            Package result = service.validateAndGetPackage(token.getCode());
            
            // Assert
            assertNotNull(result, "Should return package");
            assertEquals("pkg1", result.getId(), "Should return correct package");
            
            pass(testName);
        } catch (AssertionError | Exception e) {
            fail(testName, e);
        }
    }

    private static void testValidateAndGetPackage_InvalidCode() {
        String testName = "testValidateAndGetPackage_InvalidCode";
        try {
            // Arrange
            AccessTokenService service = createService(LocalDateTime.now());
            
            // Act & Assert
            assertThrows(InvalidAccessTokenException.class, () -> {
                service.validateAndGetPackage("NONEXISTENT");
            }, "Should throw InvalidAccessTokenException");
            
            pass(testName);
        } catch (AssertionError | Exception e) {
            fail(testName, e);
        }
    }

    private static void testValidateAndGetPackage_ExpiredToken() {
        String testName = "testValidateAndGetPackage_ExpiredToken";
        try {
            // Arrange
            LocalDateTime depositTime = LocalDateTime.now().minusDays(10);
            MutableClock clock = new MutableClock(depositTime);
            PackageRepository repo = new InMemoryPackageRepository();
            AccessTokenService service = new AccessTokenService(
                    new UUIDAccessTokenGenerator(), repo, clock);
            
            AccessToken token = service.generateToken();
            Package pkg = new Package("pkg1", CompartmentSize.SMALL, "comp1", token, depositTime);
            repo.save(pkg);
            
            // Advance clock past expiry
            clock.setTime(depositTime.plusDays(8));
            
            // Act & Assert
            assertThrows(ExpiredAccessTokenException.class, () -> {
                service.validateAndGetPackage(token.getCode());
            }, "Should throw ExpiredAccessTokenException");
            
            pass(testName);
        } catch (AssertionError | Exception e) {
            fail(testName, e);
        }
    }

    private static void testValidateAndGetPackage_AlreadyUsed() {
        String testName = "testValidateAndGetPackage_AlreadyUsed";
        try {
            // Arrange
            LocalDateTime now = LocalDateTime.now();
            PackageRepository repo = new InMemoryPackageRepository();
            AccessTokenService service = new AccessTokenService(
                    new UUIDAccessTokenGenerator(), repo, () -> now);
            
            AccessToken token = service.generateToken();
            Package pkg = new Package("pkg1", CompartmentSize.SMALL, "comp1", token, now);
            pkg.markAsRetrieved(now); // Mark as already retrieved
            repo.save(pkg);
            
            // Act & Assert
            assertThrows(AlreadyUsedAccessTokenException.class, () -> {
                service.validateAndGetPackage(token.getCode());
            }, "Should throw AlreadyUsedAccessTokenException");
            
            pass(testName);
        } catch (AssertionError | Exception e) {
            fail(testName, e);
        }
    }

    private static void testMarkAsUsed_ChangesStatus() {
        String testName = "testMarkAsUsed_ChangesStatus";
        try {
            // Arrange
            LocalDateTime now = LocalDateTime.now();
            AccessTokenService service = createService(now);
            AccessToken token = service.generateToken();
            
            // Act
            service.markAsUsed(token);
            
            // Assert
            assertEquals(TokenStatus.USED, token.getStatus(), "Should be marked as used");
            
            pass(testName);
        } catch (AssertionError | Exception e) {
            fail(testName, e);
        }
    }

    // ==================== Helpers ====================

    private static AccessTokenService createService(LocalDateTime fixedTime) {
        return new AccessTokenService(
                new UUIDAccessTokenGenerator(),
                new InMemoryPackageRepository(),
                () -> fixedTime
        );
    }

    private static class MutableClock implements Clock {
        private LocalDateTime time;

        MutableClock(LocalDateTime time) {
            this.time = time;
        }

        void setTime(LocalDateTime time) {
            this.time = time;
        }

        @Override
        public LocalDateTime now() {
            return time;
        }
    }

    private static void assertNotNull(Object obj, String message) {
        if (obj == null) throw new AssertionError(message);
    }

    private static void assertTrue(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }

    private static void assertEquals(Object expected, Object actual, String message) {
        if (!expected.equals(actual)) {
            throw new AssertionError(message + " - Expected: " + expected + ", Actual: " + actual);
        }
    }

    private static <T extends Exception> void assertThrows(Class<T> exceptionClass, 
                                                            Runnable runnable, String message) {
        try {
            runnable.run();
            throw new AssertionError(message + " - No exception thrown");
        } catch (Exception e) {
            if (!exceptionClass.isInstance(e)) {
                throw new AssertionError(message + " - Wrong exception: " + e.getClass().getSimpleName());
            }
        }
    }

    private static void pass(String testName) {
        testsRun++;
        testsPassed++;
        System.out.println("✅ PASS: " + testName);
    }

    private static void fail(String testName, Throwable e) {
        testsRun++;
        testsFailed++;
        System.out.println("❌ FAIL: " + testName + " - " + e.getMessage());
    }
}
