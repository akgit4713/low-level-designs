package employeedirectory.models;

import employeedirectory.enums.TeamRole;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Represents a member of a team with their role in that team.
 * An employee can have different roles in different teams.
 */
public class TeamMember {
    private final Employee employee;
    private TeamRole role;
    private final LocalDate joinedDate;

    public TeamMember(Employee employee, TeamRole role) {
        this.employee = employee;
        this.role = role;
        this.joinedDate = LocalDate.now();
    }

    public Employee getEmployee() {
        return employee;
    }

    public TeamRole getRole() {
        return role;
    }

    public void setRole(TeamRole role) {
        this.role = role;
    }

    public LocalDate getJoinedDate() {
        return joinedDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TeamMember that = (TeamMember) o;
        return Objects.equals(employee, that.employee);
    }

    @Override
    public int hashCode() {
        return Objects.hash(employee);
    }

    @Override
    public String toString() {
        return String.format("TeamMember[employee=%s, role=%s]",
                employee.getName(), role.getDisplayName());
    }
}
