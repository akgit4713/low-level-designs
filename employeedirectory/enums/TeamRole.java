package employeedirectory.enums;

/**
 * Represents the role of an employee within a team.
 */
public enum TeamRole {
    TEAM_LEAD("Team Lead"),
    MEMBER("Member"),
    TECH_LEAD("Tech Lead"),
    MANAGER("Manager"),
    CONTRIBUTOR("Contributor");

    private final String displayName;

    TeamRole(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
