package packagelocker.services;

import packagelocker.enums.CompartmentSize;
import packagelocker.models.*;
import packagelocker.models.Package;
import packagelocker.repositories.PackageRepository;

import java.util.UUID;

/**
 * Main service that orchestrates package deposit and retrieval operations.
 * This is the primary entry point for locker operations.
 */
public class LockerService {
    
    private final CompartmentManager compartmentManager;
    private final AccessTokenService accessTokenService;
    private final ExpirationManager expirationManager;
    private final PackageRepository packageRepository;
    private final Clock clock;

    public LockerService(CompartmentManager compartmentManager,
                         AccessTokenService accessTokenService,
                         ExpirationManager expirationManager,
                         PackageRepository packageRepository,
                         Clock clock) {
        this.compartmentManager = compartmentManager;
        this.accessTokenService = accessTokenService;
        this.expirationManager = expirationManager;
        this.packageRepository = packageRepository;
        this.clock = clock;
    }

    /**
     * Deposits a package into the locker.
     * 
     * Steps:
     * 1. Find an available compartment of the requested size
     * 2. Generate a unique access token
     * 3. Create and store the package
     * 4. Return deposit result with compartment number and access code
     * 
     * @param size the size of compartment needed
     * @return DepositResult containing compartment number and access code
     * @throws packagelocker.exceptions.NoAvailableCompartmentException if no compartment available
     */
    public DepositResult depositPackage(CompartmentSize size) {
        // Cleanup expired packages first to free up space
        expirationManager.cleanupExpiredPackages();
        
        // Allocate a compartment
        Compartment compartment = compartmentManager.allocate(size);
        
        // Generate access token
        AccessToken accessToken = accessTokenService.generateToken();
        
        // Create package
        String packageId = generatePackageId();
        Package pkg = new Package(
                packageId,
                size,
                compartment.getId(),
                accessToken,
                clock.now()
        );
        
        // Save package
        packageRepository.save(pkg);
        
        return new DepositResult(
                packageId,
                compartment.getNumber(),
                accessToken.getCode(),
                accessToken.getExpiresAt()
        );
    }

    /**
     * Retrieves a package using the access code.
     * 
     * Steps:
     * 1. Validate the access code
     * 2. Get the package and compartment information
     * 3. Mark the package as retrieved
     * 4. Release the compartment
     * 5. Return retrieval result with compartment number
     * 
     * @param accessCode the access code provided by the customer
     * @return RetrievalResult containing compartment number
     * @throws packagelocker.exceptions.InvalidAccessTokenException if code is invalid
     * @throws packagelocker.exceptions.ExpiredAccessTokenException if code is expired
     * @throws packagelocker.exceptions.AlreadyUsedAccessTokenException if code was already used
     */
    public RetrievalResult retrievePackage(String accessCode) {
        // Validate access code and get package
        Package pkg = accessTokenService.validateAndGetPackage(accessCode);
        
        // Get compartment number
        Compartment compartment = compartmentManager.findById(pkg.getCompartmentId())
                .orElseThrow(() -> new IllegalStateException("Compartment not found for package"));
        
        // Mark package as retrieved
        pkg.markAsRetrieved(clock.now());
        packageRepository.save(pkg);
        
        // Release compartment
        compartmentManager.release(compartment.getId());
        
        return new RetrievalResult(
                pkg.getId(),
                compartment.getNumber(),
                pkg.getRetrievalTime()
        );
    }

    /**
     * Manually triggers cleanup of expired packages.
     * 
     * @return number of expired packages cleaned up
     */
    public int cleanupExpiredPackages() {
        return expirationManager.cleanupExpiredPackages();
    }

    /**
     * Gets the number of available compartments of a specific size.
     */
    public int getAvailableCompartments(CompartmentSize size) {
        return compartmentManager.getAvailableCount(size);
    }

    /**
     * Gets the total number of available compartments.
     */
    public int getTotalAvailableCompartments() {
        return compartmentManager.getTotalAvailableCount();
    }

    private String generatePackageId() {
        return "PKG-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
