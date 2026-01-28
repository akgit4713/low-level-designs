package packagelocker.factories;

import packagelocker.enums.CompartmentSize;
import packagelocker.models.Compartment;
import packagelocker.models.Locker;
import packagelocker.repositories.CompartmentRepository;
import packagelocker.repositories.PackageRepository;
import packagelocker.repositories.impl.InMemoryCompartmentRepository;
import packagelocker.repositories.impl.InMemoryPackageRepository;
import packagelocker.services.*;
import packagelocker.strategies.*;

import java.util.UUID;

/**
 * Factory for creating and wiring locker components.
 * Simplifies setup and ensures proper dependency injection.
 */
public class LockerFactory {
    
    private LockerFactory() {
        // Utility class
    }

    /**
     * Creates a fully configured LockerService with default settings.
     * 
     * @param smallCount number of small compartments
     * @param mediumCount number of medium compartments
     * @param largeCount number of large compartments
     * @return configured LockerService
     */
    public static LockerService createLockerService(int smallCount, int mediumCount, int largeCount) {
        return createLockerService(
                smallCount, 
                mediumCount, 
                largeCount,
                new ExactMatchAllocationStrategy(),
                new UUIDAccessTokenGenerator(),
                Clock.systemClock()
        );
    }

    /**
     * Creates a LockerService with custom strategies.
     */
    public static LockerService createLockerService(
            int smallCount, 
            int mediumCount, 
            int largeCount,
            CompartmentAllocationStrategy allocationStrategy,
            AccessTokenGenerator tokenGenerator,
            Clock clock) {
        
        // Create repositories
        CompartmentRepository compartmentRepo = new InMemoryCompartmentRepository();
        PackageRepository packageRepo = new InMemoryPackageRepository();
        
        // Initialize compartments
        initializeCompartments(compartmentRepo, smallCount, mediumCount, largeCount);
        
        // Create services
        CompartmentManager compartmentManager = new CompartmentManager(
                compartmentRepo, allocationStrategy);
        
        AccessTokenService accessTokenService = new AccessTokenService(
                tokenGenerator, packageRepo, clock);
        
        ExpirationManager expirationManager = new ExpirationManager(
                packageRepo, compartmentManager, clock);
        
        return new LockerService(
                compartmentManager,
                accessTokenService,
                expirationManager,
                packageRepo,
                clock
        );
    }

    /**
     * Creates a Locker model with compartments.
     */
    public static Locker createLocker(String location, int smallCount, int mediumCount, int largeCount) {
        Locker locker = new Locker(generateLockerId(), location);
        
        int compartmentNumber = 1;
        
        // Add small compartments
        for (int i = 0; i < smallCount; i++) {
            locker.addCompartment(new Compartment(
                    generateCompartmentId(),
                    compartmentNumber++,
                    CompartmentSize.SMALL
            ));
        }
        
        // Add medium compartments
        for (int i = 0; i < mediumCount; i++) {
            locker.addCompartment(new Compartment(
                    generateCompartmentId(),
                    compartmentNumber++,
                    CompartmentSize.MEDIUM
            ));
        }
        
        // Add large compartments
        for (int i = 0; i < largeCount; i++) {
            locker.addCompartment(new Compartment(
                    generateCompartmentId(),
                    compartmentNumber++,
                    CompartmentSize.LARGE
            ));
        }
        
        return locker;
    }

    private static void initializeCompartments(CompartmentRepository repo, 
                                                int smallCount, int mediumCount, int largeCount) {
        int compartmentNumber = 1;
        
        // Add small compartments
        for (int i = 0; i < smallCount; i++) {
            repo.save(new Compartment(
                    generateCompartmentId(),
                    compartmentNumber++,
                    CompartmentSize.SMALL
            ));
        }
        
        // Add medium compartments
        for (int i = 0; i < mediumCount; i++) {
            repo.save(new Compartment(
                    generateCompartmentId(),
                    compartmentNumber++,
                    CompartmentSize.MEDIUM
            ));
        }
        
        // Add large compartments
        for (int i = 0; i < largeCount; i++) {
            repo.save(new Compartment(
                    generateCompartmentId(),
                    compartmentNumber++,
                    CompartmentSize.LARGE
            ));
        }
    }

    private static String generateLockerId() {
        return "LOCKER-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private static String generateCompartmentId() {
        return "COMP-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
