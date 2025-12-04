package airline.enums;

/**
 * Roles for users in the airline management system.
 */
public enum UserRole {
    PASSENGER("Regular passenger"),
    AIRLINE_STAFF("Airline employee"),
    ADMINISTRATOR("System administrator");

    private final String description;

    UserRole(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public boolean canManageFlights() {
        return this == AIRLINE_STAFF || this == ADMINISTRATOR;
    }

    public boolean canManageCrew() {
        return this == AIRLINE_STAFF || this == ADMINISTRATOR;
    }

    public boolean canViewAllBookings() {
        return this == AIRLINE_STAFF || this == ADMINISTRATOR;
    }

    public boolean canProcessRefunds() {
        return this == AIRLINE_STAFF || this == ADMINISTRATOR;
    }
}



