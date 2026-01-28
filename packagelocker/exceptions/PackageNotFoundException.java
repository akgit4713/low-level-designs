package packagelocker.exceptions;

/**
 * Thrown when a package cannot be found.
 */
public class PackageNotFoundException extends LockerException {
    
    private final String packageId;

    public PackageNotFoundException(String packageId) {
        super(String.format("Package with ID '%s' not found.", packageId));
        this.packageId = packageId;
    }

    public String getPackageId() {
        return packageId;
    }
}
