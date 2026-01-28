package packagelocker.tests;

import packagelocker.enums.CompartmentSize;
import packagelocker.exceptions.NoAvailableCompartmentException;
import packagelocker.models.Compartment;
import packagelocker.repositories.CompartmentRepository;
import packagelocker.repositories.impl.InMemoryCompartmentRepository;
import packagelocker.services.CompartmentManager;
import packagelocker.strategies.BestFitAllocationStrategy;
import packagelocker.strategies.ExactMatchAllocationStrategy;

/**
 * Unit tests for CompartmentManager.
 */
public class CompartmentManagerTest {
    
    private static int testsRun = 0;
    private static int testsPassed = 0;
    private static int testsFailed = 0;

    public static void main(String[] args) {
        System.out.println("╔══════════════════════════════════════════════════════════════╗");
        System.out.println("║          COMPARTMENT MANAGER - UNIT TESTS                    ║");
        System.out.println("╚══════════════════════════════════════════════════════════════╝");
        System.out.println();

        testAllocate_ExactMatch_Success();
        testAllocate_ExactMatch_NoMatch();
        testAllocate_BestFit_UpsizesWhenNeeded();
        testRelease_MakesCompartmentAvailable();
        testGetAvailableCount_ReturnsCorrectCount();
        
        // Print summary
        System.out.println("\n" + "═".repeat(60));
        System.out.println("Total: " + testsRun + " | Passed: " + testsPassed + 
                " ✅ | Failed: " + testsFailed + " ❌");
        System.out.println("═".repeat(60));
    }

    private static void testAllocate_ExactMatch_Success() {
        String testName = "testAllocate_ExactMatch_Success";
        try {
            // Arrange
            CompartmentRepository repo = createRepo(2, 1, 1);
            CompartmentManager manager = new CompartmentManager(repo, new ExactMatchAllocationStrategy());
            
            // Act
            Compartment result = manager.allocate(CompartmentSize.SMALL);
            
            // Assert
            assertNotNull(result, "Should return a compartment");
            assertEquals(CompartmentSize.SMALL, result.getSize(), "Should be small size");
            assertTrue(result.isOccupied(), "Should be marked as occupied");
            
            pass(testName);
        } catch (AssertionError | Exception e) {
            fail(testName, e);
        }
    }

    private static void testAllocate_ExactMatch_NoMatch() {
        String testName = "testAllocate_ExactMatch_NoMatch";
        try {
            // Arrange - no large compartments
            CompartmentRepository repo = createRepo(2, 1, 0);
            CompartmentManager manager = new CompartmentManager(repo, new ExactMatchAllocationStrategy());
            
            // Act & Assert
            assertThrows(NoAvailableCompartmentException.class, () -> {
                manager.allocate(CompartmentSize.LARGE);
            }, "Should throw exception when no exact match");
            
            pass(testName);
        } catch (AssertionError | Exception e) {
            fail(testName, e);
        }
    }

    private static void testAllocate_BestFit_UpsizesWhenNeeded() {
        String testName = "testAllocate_BestFit_UpsizesWhenNeeded";
        try {
            // Arrange - no small compartments, only medium and large
            CompartmentRepository repo = createRepo(0, 1, 1);
            CompartmentManager manager = new CompartmentManager(repo, new BestFitAllocationStrategy());
            
            // Act - request small, should get medium
            Compartment result = manager.allocate(CompartmentSize.SMALL);
            
            // Assert
            assertNotNull(result, "Should return a compartment");
            assertEquals(CompartmentSize.MEDIUM, result.getSize(), 
                    "Should upsize to medium (smallest that fits)");
            
            pass(testName);
        } catch (AssertionError | Exception e) {
            fail(testName, e);
        }
    }

    private static void testRelease_MakesCompartmentAvailable() {
        String testName = "testRelease_MakesCompartmentAvailable";
        try {
            // Arrange
            CompartmentRepository repo = createRepo(1, 0, 0);
            CompartmentManager manager = new CompartmentManager(repo, new ExactMatchAllocationStrategy());
            
            Compartment allocated = manager.allocate(CompartmentSize.SMALL);
            assertEquals(0, manager.getAvailableCount(CompartmentSize.SMALL), 
                    "Should have 0 available after allocation");
            
            // Act
            manager.release(allocated.getId());
            
            // Assert
            assertEquals(1, manager.getAvailableCount(CompartmentSize.SMALL), 
                    "Should have 1 available after release");
            
            pass(testName);
        } catch (AssertionError | Exception e) {
            fail(testName, e);
        }
    }

    private static void testGetAvailableCount_ReturnsCorrectCount() {
        String testName = "testGetAvailableCount_ReturnsCorrectCount";
        try {
            // Arrange
            CompartmentRepository repo = createRepo(5, 3, 2);
            CompartmentManager manager = new CompartmentManager(repo, new ExactMatchAllocationStrategy());
            
            // Assert initial counts
            assertEquals(5, manager.getAvailableCount(CompartmentSize.SMALL), "Small count");
            assertEquals(3, manager.getAvailableCount(CompartmentSize.MEDIUM), "Medium count");
            assertEquals(2, manager.getAvailableCount(CompartmentSize.LARGE), "Large count");
            assertEquals(10, manager.getTotalAvailableCount(), "Total count");
            
            // Allocate some
            manager.allocate(CompartmentSize.SMALL);
            manager.allocate(CompartmentSize.MEDIUM);
            
            // Assert updated counts
            assertEquals(4, manager.getAvailableCount(CompartmentSize.SMALL), "Small after alloc");
            assertEquals(2, manager.getAvailableCount(CompartmentSize.MEDIUM), "Medium after alloc");
            assertEquals(8, manager.getTotalAvailableCount(), "Total after alloc");
            
            pass(testName);
        } catch (AssertionError | Exception e) {
            fail(testName, e);
        }
    }

    // ==================== Helpers ====================

    private static CompartmentRepository createRepo(int small, int medium, int large) {
        CompartmentRepository repo = new InMemoryCompartmentRepository();
        int num = 1;
        for (int i = 0; i < small; i++) {
            repo.save(new Compartment("c" + num, num++, CompartmentSize.SMALL));
        }
        for (int i = 0; i < medium; i++) {
            repo.save(new Compartment("c" + num, num++, CompartmentSize.MEDIUM));
        }
        for (int i = 0; i < large; i++) {
            repo.save(new Compartment("c" + num, num++, CompartmentSize.LARGE));
        }
        return repo;
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
