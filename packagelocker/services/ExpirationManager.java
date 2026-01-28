package packagelocker.services;

import packagelocker.models.Package;
import packagelocker.repositories.PackageRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Manages package expiration and cleanup.
 * Responsible for freeing up compartments when tokens expire.
 */
public class ExpirationManager {
    
    private final PackageRepository packageRepository;
    private final CompartmentManager compartmentManager;
    private final Clock clock;

    public ExpirationManager(PackageRepository packageRepository,
                             CompartmentManager compartmentManager,
                             Clock clock) {
        this.packageRepository = packageRepository;
        this.compartmentManager = compartmentManager;
        this.clock = clock;
    }

    /**
     * Cleans up expired packages and releases their compartments.
     * 
     * @return the number of packages cleaned up
     */
    public int cleanupExpiredPackages() {
        LocalDateTime now = clock.now();
        
        List<Package> expiredPackages = packageRepository.findActivePackages().stream()
                .filter(pkg -> pkg.isExpired(now))
                .collect(Collectors.toList());
        
        for (Package pkg : expiredPackages) {
            // Mark token as expired
            pkg.getAccessToken().markAsExpired();
            
            // Release the compartment
            compartmentManager.release(pkg.getCompartmentId());
            
            // Remove the package from repository (or mark as cleaned up)
            packageRepository.delete(pkg.getId());
        }
        
        return expiredPackages.size();
    }

    /**
     * Gets the list of packages that will expire within the given hours.
     */
    public List<Package> getPackagesExpiringWithin(int hours) {
        LocalDateTime now = clock.now();
        LocalDateTime threshold = now.plusHours(hours);
        
        return packageRepository.findActivePackages().stream()
                .filter(pkg -> {
                    LocalDateTime expiry = pkg.getAccessToken().getExpiresAt();
                    return expiry.isAfter(now) && expiry.isBefore(threshold);
                })
                .collect(Collectors.toList());
    }

    /**
     * Checks if a specific package is expired.
     */
    public boolean isExpired(String packageId) {
        return packageRepository.findById(packageId)
                .map(pkg -> pkg.isExpired(clock.now()))
                .orElse(false);
    }
}
