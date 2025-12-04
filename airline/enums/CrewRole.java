package airline.enums;

/**
 * Roles for crew members.
 */
public enum CrewRole {
    PILOT("Captain/Pilot", true),
    CO_PILOT("Co-Pilot/First Officer", true),
    PURSER("Cabin Manager/Purser", false),
    FLIGHT_ATTENDANT("Flight Attendant", false);

    private final String displayName;
    private final boolean cockpitCrew;

    CrewRole(String displayName, boolean cockpitCrew) {
        this.displayName = displayName;
        this.cockpitCrew = cockpitCrew;
    }

    public String getDisplayName() {
        return displayName;
    }

    public boolean isCockpitCrew() {
        return cockpitCrew;
    }
}



