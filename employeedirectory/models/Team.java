package employeedirectory.models;

import employeedirectory.enums.Department;
import employeedirectory.enums.TeamRole;

import java.time.LocalDate;
import java.util.*;

/**
 * Represents a team in the organization.
 * A team has members with different roles.
 */
public class Team {
    private final String teamId;
    private String name;
    private String description;
    private Department department;
    private final LocalDate createdDate;
    private final Map<String, TeamMember> members; // employeeId -> TeamMember

    public Team(String teamId, String name, Department department) {
        this.teamId = teamId;
        this.name = name;
        this.department = department;
        this.description = "";
        this.createdDate = LocalDate.now();
        this.members = new HashMap<>();
    }

    // Getters
    public String getTeamId() {
        return teamId;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Department getDepartment() {
        return department;
    }

    public LocalDate getCreatedDate() {
        return createdDate;
    }

    // Setters
    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    /**
     * Adds a member to the team with a specific role.
     */
    public void addMember(Employee employee, TeamRole role) {
        TeamMember member = new TeamMember(employee, role);
        members.put(employee.getEmployeeId(), member);
    }

    /**
     * Removes a member from the team.
     */
    public boolean removeMember(String employeeId) {
        return members.remove(employeeId) != null;
    }

    /**
     * Updates the role of a team member.
     */
    public boolean updateMemberRole(String employeeId, TeamRole newRole) {
        TeamMember member = members.get(employeeId);
        if (member != null) {
            member.setRole(newRole);
            return true;
        }
        return false;
    }

    /**
     * Gets all members of the team.
     */
    public List<TeamMember> getMembers() {
        return new ArrayList<>(members.values());
    }

    /**
     * Gets a specific member by employee ID.
     */
    public Optional<TeamMember> getMember(String employeeId) {
        return Optional.ofNullable(members.get(employeeId));
    }

    /**
     * Gets the team lead(s).
     */
    public List<Employee> getTeamLeads() {
        return members.values().stream()
                .filter(m -> m.getRole() == TeamRole.TEAM_LEAD || m.getRole() == TeamRole.MANAGER)
                .map(TeamMember::getEmployee)
                .toList();
    }

    /**
     * Gets members by role.
     */
    public List<Employee> getMembersByRole(TeamRole role) {
        return members.values().stream()
                .filter(m -> m.getRole() == role)
                .map(TeamMember::getEmployee)
                .toList();
    }

    /**
     * Checks if an employee is a member of this team.
     */
    public boolean hasMember(String employeeId) {
        return members.containsKey(employeeId);
    }

    /**
     * Gets the team size.
     */
    public int getSize() {
        return members.size();
    }

    /**
     * Calculates the total CTC for the team.
     */
    public double calculateTotalCTC() {
        return members.values().stream()
                .mapToDouble(m -> m.getEmployee().calculateCTC())
                .sum();
    }

    /**
     * Calculates the average CTC for the team.
     */
    public double calculateAverageCTC() {
        if (members.isEmpty()) {
            return 0;
        }
        return calculateTotalCTC() / members.size();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Team team = (Team) o;
        return Objects.equals(teamId, team.teamId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(teamId);
    }

    @Override
    public String toString() {
        return String.format("Team[id=%s, name=%s, department=%s, size=%d]",
                teamId, name, department.getDisplayName(), members.size());
    }
}
