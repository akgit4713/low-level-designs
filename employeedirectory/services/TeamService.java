package employeedirectory.services;

import employeedirectory.enums.Department;
import employeedirectory.enums.TeamRole;
import employeedirectory.exceptions.DuplicateTeamException;
import employeedirectory.exceptions.EmployeeNotFoundException;
import employeedirectory.exceptions.TeamNotFoundException;
import employeedirectory.models.Employee;
import employeedirectory.models.Team;
import employeedirectory.models.TeamMember;
import employeedirectory.repositories.EmployeeRepository;
import employeedirectory.repositories.TeamRepository;

import java.util.List;

/**
 * Service for managing teams.
 */
public class TeamService {
    
    private final TeamRepository teamRepository;
    private final EmployeeRepository employeeRepository;

    public TeamService(TeamRepository teamRepository, EmployeeRepository employeeRepository) {
        this.teamRepository = teamRepository;
        this.employeeRepository = employeeRepository;
    }

    /**
     * Creates a new team.
     */
    public Team createTeam(String teamId, String name, Department department) {
        if (teamRepository.existsById(teamId)) {
            throw new DuplicateTeamException(teamId);
        }
        
        Team team = new Team(teamId, name, department);
        return teamRepository.save(team);
    }

    /**
     * Gets a team by ID.
     */
    public Team getTeam(String teamId) {
        return teamRepository.findById(teamId)
                .orElseThrow(() -> new TeamNotFoundException(teamId));
    }

    /**
     * Updates team details.
     */
    public Team updateTeam(String teamId, String name, String description, Department department) {
        Team team = getTeam(teamId);
        
        if (name != null) team.setName(name);
        if (description != null) team.setDescription(description);
        if (department != null) team.setDepartment(department);
        
        return teamRepository.save(team);
    }

    /**
     * Adds an employee to a team with a specific role.
     */
    public void addMemberToTeam(String teamId, String employeeId, TeamRole role) {
        Team team = getTeam(teamId);
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EmployeeNotFoundException(employeeId));
        
        team.addMember(employee, role);
        teamRepository.save(team);
    }

    /**
     * Removes an employee from a team.
     */
    public void removeMemberFromTeam(String teamId, String employeeId) {
        Team team = getTeam(teamId);
        team.removeMember(employeeId);
        teamRepository.save(team);
    }

    /**
     * Updates a member's role in a team.
     */
    public void updateMemberRole(String teamId, String employeeId, TeamRole newRole) {
        Team team = getTeam(teamId);
        if (!team.updateMemberRole(employeeId, newRole)) {
            throw new EmployeeNotFoundException(employeeId);
        }
        teamRepository.save(team);
    }

    /**
     * Gets all teams.
     */
    public List<Team> getAllTeams() {
        return teamRepository.findAll();
    }

    /**
     * Gets teams by department.
     */
    public List<Team> getTeamsByDepartment(Department department) {
        return teamRepository.findByDepartment(department);
    }

    /**
     * Gets all teams an employee belongs to.
     */
    public List<Team> getTeamsForEmployee(String employeeId) {
        return teamRepository.findByEmployee(employeeId);
    }

    /**
     * Gets all members of a team.
     */
    public List<TeamMember> getTeamMembers(String teamId) {
        Team team = getTeam(teamId);
        return team.getMembers();
    }

    /**
     * Gets team leads of a team.
     */
    public List<Employee> getTeamLeads(String teamId) {
        Team team = getTeam(teamId);
        return team.getTeamLeads();
    }

    /**
     * Searches teams by name.
     */
    public List<Team> searchByName(String name) {
        return teamRepository.findByName(name);
    }

    /**
     * Deletes a team.
     */
    public void deleteTeam(String teamId) {
        if (!teamRepository.existsById(teamId)) {
            throw new TeamNotFoundException(teamId);
        }
        teamRepository.deleteById(teamId);
    }
}
