package packagelocker.repositories;

import packagelocker.models.Package;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Package persistence.
 */
public interface PackageRepository {
    
    void save(Package pkg);
    
    Optional<Package> findById(String id);
    
    Optional<Package> findByAccessCode(String accessCode);
    
    Optional<Package> findByCompartmentId(String compartmentId);
    
    List<Package> findAll();
    
    List<Package> findActivePackages();
    
    void delete(String id);
}
