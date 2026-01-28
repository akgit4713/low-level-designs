package packagelocker;

import packagelocker.enums.CompartmentSize;
import packagelocker.exceptions.*;
import packagelocker.factories.LockerFactory;
import packagelocker.models.DepositResult;
import packagelocker.models.RetrievalResult;
import packagelocker.services.LockerService;

/**
 * Demonstration of the Package Locker System.
 */
public class Main {
    
    public static void main(String[] args) {
        System.out.println("╔══════════════════════════════════════════════════════════════╗");
        System.out.println("║              PACKAGE LOCKER SYSTEM DEMO                      ║");
        System.out.println("╚══════════════════════════════════════════════════════════════╝");
        System.out.println();
        
        // Create a locker with 5 small, 3 medium, and 2 large compartments
        LockerService lockerService = LockerFactory.createLockerService(5, 3, 2);
        
        printLockerStatus(lockerService);
        
        // Scenario 1: Carrier deposits packages
        System.out.println("\n📦 SCENARIO 1: Carrier deposits packages");
        System.out.println("─".repeat(50));
        
        DepositResult smallPackage = lockerService.depositPackage(CompartmentSize.SMALL);
        printDepositResult("Small Package", smallPackage);
        
        DepositResult mediumPackage = lockerService.depositPackage(CompartmentSize.MEDIUM);
        printDepositResult("Medium Package", mediumPackage);
        
        DepositResult largePackage = lockerService.depositPackage(CompartmentSize.LARGE);
        printDepositResult("Large Package", largePackage);
        
        printLockerStatus(lockerService);
        
        // Scenario 2: Customer retrieves package with valid code
        System.out.println("\n✅ SCENARIO 2: Customer retrieves package with valid code");
        System.out.println("─".repeat(50));
        
        try {
            RetrievalResult retrieval = lockerService.retrievePackage(smallPackage.getAccessCode());
            printRetrievalResult(retrieval);
        } catch (LockerException e) {
            System.out.println("Error: " + e.getMessage());
        }
        
        printLockerStatus(lockerService);
        
        // Scenario 3: Attempt to use the same code again
        System.out.println("\n🔄 SCENARIO 3: Attempt to reuse the same access code");
        System.out.println("─".repeat(50));
        
        try {
            lockerService.retrievePackage(smallPackage.getAccessCode());
        } catch (AlreadyUsedAccessTokenException e) {
            System.out.println("❌ Error (Expected): " + e.getMessage());
        }
        
        // Scenario 4: Attempt with invalid code
        System.out.println("\n🚫 SCENARIO 4: Attempt with invalid access code");
        System.out.println("─".repeat(50));
        
        try {
            lockerService.retrievePackage("INVALID12345");
        } catch (InvalidAccessTokenException e) {
            System.out.println("❌ Error (Expected): " + e.getMessage());
        }
        
        // Scenario 5: Fill up compartments
        System.out.println("\n📦 SCENARIO 5: Fill up large compartments");
        System.out.println("─".repeat(50));
        
        try {
            // We have 2 large compartments, 1 is already used
            DepositResult anotherLarge = lockerService.depositPackage(CompartmentSize.LARGE);
            printDepositResult("Another Large Package", anotherLarge);
            
            // This should fail - no more large compartments
            System.out.println("\nAttempting to deposit another large package...");
            lockerService.depositPackage(CompartmentSize.LARGE);
        } catch (NoAvailableCompartmentException e) {
            System.out.println("❌ Error (Expected): " + e.getMessage());
        }
        
        // Final status
        System.out.println("\n" + "═".repeat(50));
        printLockerStatus(lockerService);
        
        System.out.println("\n✨ Demo completed successfully!");
    }
    
    private static void printDepositResult(String label, DepositResult result) {
        System.out.println("\n  " + label + ":");
        System.out.println("    Package ID:     " + result.getPackageId());
        System.out.println("    Compartment:    #" + result.getCompartmentNumber());
        System.out.println("    Access Code:    " + result.getAccessCode());
        System.out.println("    Expires:        " + result.getExpiresAt());
    }
    
    private static void printRetrievalResult(RetrievalResult result) {
        System.out.println("\n  Package Retrieved Successfully!");
        System.out.println("    Package ID:     " + result.getPackageId());
        System.out.println("    Compartment:    #" + result.getCompartmentNumber());
        System.out.println("    Retrieved At:   " + result.getRetrievedAt());
    }
    
    private static void printLockerStatus(LockerService service) {
        System.out.println("\n📊 LOCKER STATUS:");
        System.out.println("  Small compartments available:  " + 
                service.getAvailableCompartments(CompartmentSize.SMALL) + "/5");
        System.out.println("  Medium compartments available: " + 
                service.getAvailableCompartments(CompartmentSize.MEDIUM) + "/3");
        System.out.println("  Large compartments available:  " + 
                service.getAvailableCompartments(CompartmentSize.LARGE) + "/2");
        System.out.println("  Total available:               " + 
                service.getTotalAvailableCompartments() + "/10");
    }
}
