package filesystem.tests;

/**
 * Runs all test suites for the In-Memory File System.
 */
public class AllTests {
    
    public static void main(String[] args) {
        System.out.println("╔══════════════════════════════════════════╗");
        System.out.println("║   In-Memory File System - Test Suite     ║");
        System.out.println("╚══════════════════════════════════════════╝\n");
        
        try {
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            PathResolverTest.main(args);
            
            System.out.println("\n━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            FileSystemTest.main(args);
            
            System.out.println("\n━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            ShellTest.main(args);
            
            System.out.println("\n╔══════════════════════════════════════════╗");
            System.out.println("║        ALL TESTS PASSED! ✓               ║");
            System.out.println("╚══════════════════════════════════════════╝");
            
        } catch (AssertionError e) {
            System.out.println("\n╔══════════════════════════════════════════╗");
            System.out.println("║        TEST FAILED! ✗                    ║");
            System.out.println("╚══════════════════════════════════════════╝");
            System.out.println("Failure: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}

