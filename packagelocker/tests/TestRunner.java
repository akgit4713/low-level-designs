package packagelocker.tests;

/**
 * Runs all unit tests for the Package Locker System.
 */
public class TestRunner {
    
    public static void main(String[] args) {
        System.out.println();
        System.out.println("╔══════════════════════════════════════════════════════════════╗");
        System.out.println("║     PACKAGE LOCKER SYSTEM - COMPLETE TEST SUITE              ║");
        System.out.println("╚══════════════════════════════════════════════════════════════╝");
        System.out.println();
        
        System.out.println("Running LockerServiceTest...");
        System.out.println("─".repeat(60));
        LockerServiceTest.main(args);
        
        System.out.println("\n\nRunning CompartmentManagerTest...");
        System.out.println("─".repeat(60));
        CompartmentManagerTest.main(args);
        
        System.out.println("\n\nRunning AccessTokenServiceTest...");
        System.out.println("─".repeat(60));
        AccessTokenServiceTest.main(args);
        
        System.out.println("\n\n" + "═".repeat(60));
        System.out.println("ALL TEST SUITES COMPLETED");
        System.out.println("═".repeat(60));
    }
}
