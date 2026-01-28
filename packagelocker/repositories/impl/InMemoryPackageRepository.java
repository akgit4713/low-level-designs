package packagelocker.repositories.impl;

import packagelocker.models.Package;
import packagelocker.repositories.PackageRepository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * In-memory implementation of PackageRepository.
 * Thread-safe using ConcurrentHashMap.
 */
public class InMemoryPackageRepository implements PackageRepository {
    
    private final Map<String, Package> packagesById = new ConcurrentHashMap<>();
    private final Map<String, Package> packagesByAccessCode = new ConcurrentHashMap<>();
    private final Map<String, Package> packagesByCompartmentId = new ConcurrentHashMap<>();

    @Override
    public void save(Package pkg) {
        packagesById.put(pkg.getId(), pkg);
        packagesByAccessCode.put(pkg.getAccessToken().getCode(), pkg);
        packagesByCompartmentId.put(pkg.getCompartmentId(), pkg);
    }

    @Override
    public Optional<Package> findById(String id) {
        return Optional.ofNullable(packagesById.get(id));
    }

    @Override
    public Optional<Package> findByAccessCode(String accessCode) {
        return Optional.ofNullable(packagesByAccessCode.get(accessCode));
    }

    @Override
    public Optional<Package> findByCompartmentId(String compartmentId) {
        return Optional.ofNullable(packagesByCompartmentId.get(compartmentId));
    }

    @Override
    public List<Package> findAll() {
        return new ArrayList<>(packagesById.values());
    }

    @Override
    public List<Package> findActivePackages() {
        return packagesById.values().stream()
                .filter(pkg -> !pkg.isRetrieved())
                .collect(Collectors.toList());
    }

    @Override
    public void delete(String id) {
        Package pkg = packagesById.remove(id);
        if (pkg != null) {
            packagesByAccessCode.remove(pkg.getAccessToken().getCode());
            packagesByCompartmentId.remove(pkg.getCompartmentId());
        }
    }
}
