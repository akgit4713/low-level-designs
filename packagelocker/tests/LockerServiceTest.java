package packagelocker.tests;

import packagelocker.enums.CompartmentSize;
import packagelocker.enums.TokenStatus;
import packagelocker.exceptions.*;
import packagelocker.models.*;
import packagelocker.repositories.CompartmentRepository;
import packagelocker.repositories.PackageRepository;
import packagelocker.repositories.impl.InMemoryCompartmentRepository;
import packagelocker.repositories.impl.InMemoryPackageRepository;
import packagelocker.services.*;
import packagelocker.strategies.AccessTokenGenerator;
import packagelocker.strategies.CompartmentAllocationStrategy;
import packagelocker.strategies.ExactMatchAllocationStrategy;
import packagelocker.strategies.UUIDAccessTokenGenerator;

import java.time.LocalDateTime;

/**
 * Unit tests for the Package Locker System.
 * Uses a simple test framework approach (can be easily adapted to JUnit).
 */
public class LockerServiceTest {
    
    private static int testsRun = 0;
    private static int testsPassed = 0;
    private static int testsFailed = 0;

    public static void main(String[] args) {
        System.out.println("╔══════════════════════════════════════════════════════════════╗");
        System.out.println("║              PACKAGE LOCKER SYSTEM - UNIT TESTS              ║");
        System.out.println("╚══════════════════════════════════════════════════════════════╝");
        System.out.println();

        // Deposit Tests
        testDepositPackage_Success();
        testDepositPackage_AllocationReducesAvailability();
        testDepositPackage_NoAvailableCompartment();
        
        // Retrieval Tests
        testRetrievePackage_Success();
        testRetrievePackage_InvalidCode();
        testRetrievePackage_AlreadyUsed();
        testRetrievePackage_ExpiredToken();
        
        // Expiration Tests
        testExpirationManager_CleanupsExpiredPackages();
        
        // Strategy Tests
        testExactMatchAllocationStrategy();
        testBestFitAllocationStrategy();
        
        // Token Generation Tests
        testUUIDTokenGenerator_Uniqueness();
        
        // Print summary
        System.out.println("\n" + "═".repeat(60));
        System.out.println("TEST SUMMARY");
        System.out.println("═".repeat(60));
        System.out.println("Total Tests: " + testsRun);
        System.out.println("Passed:      " + testsPassed + " ✅");
        System.out.println("Failed:      " + testsFailed + " ❌");
        System.out.println("═".repeat(60));
        
        if (testsFailed > 0) {
            System.exit(1);
        }
    }

    // ==================== Deposit Tests ====================

    private static void testDepositPackage_Success() {
        String testName = "testDepositPackage_Success";
        try {
            // Arrange
            LockerService service = createTestLockerService(2, 2, 2, LocalDateTime.now());
            
            // Act
            DepositResult result = service.depositPackage(CompartmentSize.SMALL);
            
            // Assert
            assertNotNull(result, "Result should not be null");
            assertNotNull(result.getPackageId(), "Package ID should not be null");
            assertNotNull(result.getAccessCode(), "Access code should not be null");
            assertTrue(result.getCompartmentNumber() > 0, "Compartment number should be positive");
            assertNotNull(result.getExpiresAt(), "Expiry time should not be null");
            
            pass(testName);
        } catch (AssertionError | Exception e) {
            fail(testName, e);
        }
    }

    private static void testDepositPackage_AllocationReducesAvailability() {
        String testName = "testDepositPackage_AllocationReducesAvailability";
        try {
            // Arrange
            LockerService service = createTestLockerService(2, 0, 0, LocalDateTime.now());
            assertEquals(2, service.getAvailableCompartments(CompartmentSize.SMALL), 
                    "Should start with 2 small compartments");
            
            // Act
            service.depositPackage(CompartmentSize.SMALL);
            
            // Assert
            assertEquals(1, service.getAvailableCompartments(CompartmentSize.SMALL), 
                    "Should have 1 small compartment after deposit");
            
            pass(testName);
        } catch (AssertionError | Exception e) {
            fail(testName, e);
        }
    }

    private static void testDepositPackage_NoAvailableCompartment() {
        String testName = "testDepositPackage_NoAvailableCompartment";
        try {
            // Arrange
            LockerService service = createTestLockerService(1, 0, 0, LocalDateTime.now());
            service.depositPackage(CompartmentSize.SMALL); // Use the only compartment
            
            // Act & Assert
            assertThrows(NoAvailableCompartmentException.class, () -> {
                service.depositPackage(CompartmentSize.SMALL);
            }, "Should throw NoAvailableCompartmentException");
            
            pass(testName);
        } catch (AssertionError | Exception e) {
            fail(testName, e);
        }
    }

    // ==================== Retrieval Tests ====================

    private static void testRetrievePackage_Success() {
        String testName = "testRetrievePackage_Success";
        try {
            // Arrange
            LockerService service = createTestLockerService(2, 0, 0, LocalDateTime.now());
            DepositResult deposit = service.depositPackage(CompartmentSize.SMALL);
            int availableBefore = service.getAvailableCompartments(CompartmentSize.SMALL);
            
            // Act
            RetrievalResult result = service.retrievePackage(deposit.getAccessCode());
            
            // Assert
            assertNotNull(result, "Result should not be null");
            assertEquals(deposit.getCompartmentNumber(), result.getCompartmentNumber(), 
                    "Compartment numbers should match");
            assertEquals(deposit.getPackageId(), result.getPackageId(), 
                    "Package IDs should match");
            assertEquals(availableBefore + 1, service.getAvailableCompartments(CompartmentSize.SMALL),
                    "Compartment should be released after retrieval");
            
            pass(testName);
        } catch (AssertionError | Exception e) {
            fail(testName, e);
        }
    }

    private static void testRetrievePackage_InvalidCode() {
        String testName = "testRetrievePackage_InvalidCode";
        try {
            // Arrange
            LockerService service = createTestLockerService(2, 0, 0, LocalDateTime.now());
            
            // Act & Assert
            assertThrows(InvalidAccessTokenException.class, () -> {
                service.retrievePackage("INVALID_CODE_12345");
            }, "Should throw InvalidAccessTokenException");
            
            pass(testName);
        } catch (AssertionError | Exception e) {
            fail(testName, e);
        }
    }

    private static void testRetrievePackage_AlreadyUsed() {
        String testName = "testRetrievePackage_AlreadyUsed";
        try {
            // Arrange
            LockerService service = createTestLockerService(2, 0, 0, LocalDateTime.now());
            DepositResult deposit = service.depositPackage(CompartmentSize.SMALL);
            service.retrievePackage(deposit.getAccessCode()); // First retrieval
            
            // Act & Assert
            assertThrows(AlreadyUsedAccessTokenException.class, () -> {
                service.retrievePackage(deposit.getAccessCode()); // Second attempt
            }, "Should throw AlreadyUsedAccessTokenException");
            
            pass(testName);
        } catch (AssertionError | Exception e) {
            fail(testName, e);
        }
    }

    private static void testRetrievePackage_ExpiredToken() {
        String testName = "testRetrievePackage_ExpiredToken";
        try {
            // Arrange - use a mock clock
            LocalDateTime depositTime = LocalDateTime.now().minusDays(10);
            MutableClock clock = new MutableClock(depositTime);
            LockerService service = createTestLockerService(2, 0, 0, clock);
            
            DepositResult deposit = service.depositPackage(CompartmentSize.SMALL);
            
            // Advance time by 8 days (past 7-day expiry)
            clock.setTime(depositTime.plusDays(8));
            
            // Act & Assert
            assertThrows(ExpiredAccessTokenException.class, () -> {
                service.retrievePackage(deposit.getAccessCode());
            }, "Should throw ExpiredAccessTokenException");
            
            pass(testName);
        } catch (AssertionError | Exception e) {
            fail(testName, e);
        }
    }

    // ==================== Expiration Tests ====================

    private static void testExpirationManager_CleanupsExpiredPackages() {
        String testName = "testExpirationManager_CleanupsExpiredPackages";
        try {
            // Arrange
            LocalDateTime depositTime = LocalDateTime.now().minusDays(10);
            MutableClock clock = new MutableClock(depositTime);
            LockerService service = createTestLockerService(2, 0, 0, clock);
            
            // Deposit a package
            service.depositPackage(CompartmentSize.SMALL);
            assertEquals(1, service.getAvailableCompartments(CompartmentSize.SMALL),
                    "Should have 1 available after deposit");
            
            // Advance time past expiry
            clock.setTime(depositTime.plusDays(8));
            
            // Act
            int cleanedUp = service.cleanupExpiredPackages();
            
            // Assert
            assertEquals(1, cleanedUp, "Should clean up 1 expired package");
            assertEquals(2, service.getAvailableCompartments(CompartmentSize.SMALL),
                    "Compartment should be released after cleanup");
            
            pass(testName);
        } catch (AssertionError | Exception e) {
            fail(testName, e);
        }
    }

    // ==================== Strategy Tests ====================

    private static void testExactMatchAllocationStrategy() {
        String testName = "testExactMatchAllocationStrategy";
        try {
            // Arrange
            CompartmentRepository repo = new InMemoryCompartmentRepository();
            repo.save(new Compartment("c1", 1, CompartmentSize.LARGE));
            repo.save(new Compartment("c2", 2, CompartmentSize.SMALL));
            
            ExactMatchAllocationStrategy strategy = new ExactMatchAllocationStrategy();
            
            // Act
            var result = strategy.allocate(repo.findAll(), CompartmentSize.SMALL);
            
            // Assert
            assertTrue(result.isPresent(), "Should find a small compartment");
            assertEquals(CompartmentSize.SMALL, result.get().getSize(), 
                    "Should return exact size match");
            
            pass(testName);
        } catch (AssertionError | Exception e) {
            fail(testName, e);
        }
    }

    private static void testBestFitAllocationStrategy() {
        String testName = "testBestFitAllocationStrategy";
        try {
            // Arrange
            CompartmentRepository repo = new InMemoryCompartmentRepository();
            repo.save(new Compartment("c1", 1, CompartmentSize.LARGE));
            repo.save(new Compartment("c2", 2, CompartmentSize.MEDIUM));
            // No small compartments
            
            packagelocker.strategies.BestFitAllocationStrategy strategy = 
                    new packagelocker.strategies.BestFitAllocationStrategy();
            
            // Act - request small, should get medium (smallest that fits)
            var result = strategy.allocate(repo.findAll(), CompartmentSize.SMALL);
            
            // Assert
            assertTrue(result.isPresent(), "Should find a fitting compartment");
            assertEquals(CompartmentSize.MEDIUM, result.get().getSize(), 
                    "Should return smallest fitting size (medium)");
            
            pass(testName);
        } catch (AssertionError | Exception e) {
            fail(testName, e);
        }
    }

    // ==================== Token Generation Tests ====================

    private static void testUUIDTokenGenerator_Uniqueness() {
        String testName = "testUUIDTokenGenerator_Uniqueness";
        try {
            // Arrange
            UUIDAccessTokenGenerator generator = new UUIDAccessTokenGenerator();
            java.util.Set<String> tokens = new java.util.HashSet<>();
            
            // Act - generate 1000 tokens
            for (int i = 0; i < 1000; i++) {
                tokens.add(generator.generate());
            }
            
            // Assert - all should be unique
            assertEquals(1000, tokens.size(), "All 1000 tokens should be unique");
            
            pass(testName);
        } catch (AssertionError | Exception e) {
            fail(testName, e);
        }
    }

    // ==================== Test Helpers ====================

    private static LockerService createTestLockerService(int small, int medium, int large, 
                                                          LocalDateTime fixedTime) {
        return createTestLockerService(small, medium, large, new MutableClock(fixedTime));
    }

    private static LockerService createTestLockerService(int small, int medium, int large, 
                                                          Clock clock) {
        CompartmentRepository compartmentRepo = new InMemoryCompartmentRepository();
        PackageRepository packageRepo = new InMemoryPackageRepository();
        
        int num = 1;
        for (int i = 0; i < small; i++) {
            compartmentRepo.save(new Compartment("c" + num, num++, CompartmentSize.SMALL));
        }
        for (int i = 0; i < medium; i++) {
            compartmentRepo.save(new Compartment("c" + num, num++, CompartmentSize.MEDIUM));
        }
        for (int i = 0; i < large; i++) {
            compartmentRepo.save(new Compartment("c" + num, num++, CompartmentSize.LARGE));
        }
        
        CompartmentAllocationStrategy strategy = new ExactMatchAllocationStrategy();
        AccessTokenGenerator tokenGenerator = new UUIDAccessTokenGenerator();
        
        CompartmentManager compartmentManager = new CompartmentManager(compartmentRepo, strategy);
        AccessTokenService tokenService = new AccessTokenService(tokenGenerator, packageRepo, clock);
        ExpirationManager expirationManager = new ExpirationManager(packageRepo, compartmentManager, clock);
        
        return new LockerService(compartmentManager, tokenService, expirationManager, packageRepo, clock);
    }

    // ==================== Mutable Clock for Testing ====================

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

    // ==================== Assertion Helpers ====================

    private static void assertNotNull(Object obj, String message) {
        if (obj == null) {
            throw new AssertionError(message);
        }
    }

    private static void assertTrue(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }

    private static void assertEquals(Object expected, Object actual, String message) {
        if (!expected.equals(actual)) {
            throw new AssertionError(message + " - Expected: " + expected + ", Actual: " + actual);
        }
    }

    private static <T extends Exception> void assertThrows(Class<T> exceptionClass, 
                                                            Runnable runnable, 
                                                            String message) {
        try {
            runnable.run();
            throw new AssertionError(message + " - No exception was thrown");
        } catch (Exception e) {
            if (!exceptionClass.isInstance(e)) {
                throw new AssertionError(message + " - Wrong exception type. Expected: " + 
                        exceptionClass.getSimpleName() + ", Got: " + e.getClass().getSimpleName());
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
        System.out.println("❌ FAIL: " + testName);
        System.out.println("   Error: " + e.getMessage());
    }
}
