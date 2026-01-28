package employeedirectory;

import employeedirectory.enums.Department;
import employeedirectory.enums.EmployeeStatus;
import employeedirectory.enums.TeamRole;
import employeedirectory.models.Employee;
import employeedirectory.models.Team;
import employeedirectory.models.TeamMember;
import employeedirectory.repositories.EmployeeRepository;
import employeedirectory.repositories.TeamRepository;
import employeedirectory.repositories.impl.InMemoryEmployeeRepository;
import employeedirectory.repositories.impl.InMemoryTeamRepository;
import employeedirectory.services.CTCCalculatorService;
import employeedirectory.services.EmployeeService;
import employeedirectory.services.HierarchyService;
import employeedirectory.services.TeamService;

import java.util.List;

/**
 * Main facade for the Employee Directory Management System.
 * Provides a unified interface to all employee and team operations.
 * 
 * This is a singleton class that manages the entire employee directory.
 */
public class EmployeeDirectory {
    
    private static volatile EmployeeDirectory instance;
    
    private final EmployeeService employeeService;
    private final TeamService teamService;
    private final HierarchyService hierarchyService;
    private final CTCCalculatorService ctcCalculatorService;

    private EmployeeDirectory() {
        // Initialize repositories
        EmployeeRepository employeeRepository = new InMemoryEmployeeRepository();
        TeamRepository teamRepository = new InMemoryTeamRepository();
        
        // Initialize services
        this.employeeService = new EmployeeService(employeeRepository);
        this.teamService = new TeamService(teamRepository, employeeRepository);
        this.hierarchyService = new HierarchyService(employeeRepository);
        this.ctcCalculatorService = new CTCCalculatorService(employeeRepository, teamRepository);
    }

    /**
     * Gets the singleton instance of EmployeeDirectory.
     */
    public static EmployeeDirectory getInstance() {
        if (instance == null) {
            synchronized (EmployeeDirectory.class) {
                if (instance == null) {
                    instance = new EmployeeDirectory();
                }
            }
        }
        return instance;
    }

    /**
     * Resets the instance (useful for testing).
     */
    public static void resetInstance() {
        synchronized (EmployeeDirectory.class) {
            instance = null;
        }
    }

    // ==================== Employee Operations ====================

    /**
     * Adds a new employee to the directory.
     */
    public Employee addEmployee(String employeeId, String name, String email,
                                String designation, Department department, double baseSalary) {
        return employeeService.addEmployee(employeeId, name, email, designation, department, baseSalary);
    }

    /**
     * Gets an employee by ID.
     */
    public Employee getEmployee(String employeeId) {
        return employeeService.getEmployee(employeeId);
    }

    /**
     * Updates employee details.
     */
    public Employee updateEmployee(String employeeId, String name, String email,
                                   String designation, Department department) {
        return employeeService.updateEmployee(employeeId, name, email, designation, department);
    }

    /**
     * Updates employee compensation.
     */
    public Employee updateCompensation(String employeeId, double baseSalary,
                                       double bonus, double allowances) {
        return employeeService.updateCompensation(employeeId, baseSalary, bonus, allowances);
    }

    /**
     * Assigns a manager to an employee.
     */
    public void assignManager(String employeeId, String managerId) {
        employeeService.assignManager(employeeId, managerId);
    }

    /**
     * Removes the manager assignment from an employee.
     */
    public void removeManager(String employeeId) {
        employeeService.removeManager(employeeId);
    }

    /**
     * Changes employee status.
     */
    public void changeEmployeeStatus(String employeeId, EmployeeStatus status) {
        employeeService.changeStatus(employeeId, status);
    }

    /**
     * Gets all employees.
     */
    public List<Employee> getAllEmployees() {
        return employeeService.getAllEmployees();
    }

    /**
     * Gets employees by department.
     */
    public List<Employee> getEmployeesByDepartment(Department department) {
        return employeeService.getEmployeesByDepartment(department);
    }

    /**
     * Searches employees by name.
     */
    public List<Employee> searchEmployeesByName(String name) {
        return employeeService.searchByName(name);
    }

    /**
     * Gets direct reports of a manager.
     */
    public List<Employee> getDirectReports(String managerId) {
        return employeeService.getDirectReports(managerId);
    }

    /**
     * Gets all subordinates of a manager (direct and indirect).
     */
    public List<Employee> getAllSubordinates(String managerId) {
        return employeeService.getAllSubordinates(managerId);
    }

    /**
     * Gets the management chain for an employee.
     */
    public List<Employee> getManagementChain(String employeeId) {
        return employeeService.getManagementChain(employeeId);
    }

    /**
     * Deletes an employee.
     */
    public void deleteEmployee(String employeeId) {
        employeeService.deleteEmployee(employeeId);
    }

    // ==================== Team Operations ====================

    /**
     * Creates a new team.
     */
    public Team createTeam(String teamId, String name, Department department) {
        return teamService.createTeam(teamId, name, department);
    }

    /**
     * Gets a team by ID.
     */
    public Team getTeam(String teamId) {
        return teamService.getTeam(teamId);
    }

    /**
     * Updates team details.
     */
    public Team updateTeam(String teamId, String name, String description, Department department) {
        return teamService.updateTeam(teamId, name, description, department);
    }

    /**
     * Adds an employee to a team with a specific role.
     */
    public void addToTeam(String teamId, String employeeId, TeamRole role) {
        teamService.addMemberToTeam(teamId, employeeId, role);
    }

    /**
     * Removes an employee from a team.
     */
    public void removeFromTeam(String teamId, String employeeId) {
        teamService.removeMemberFromTeam(teamId, employeeId);
    }

    /**
     * Updates a member's role in a team.
     */
    public void updateTeamMemberRole(String teamId, String employeeId, TeamRole newRole) {
        teamService.updateMemberRole(teamId, employeeId, newRole);
    }

    /**
     * Gets all teams.
     */
    public List<Team> getAllTeams() {
        return teamService.getAllTeams();
    }

    /**
     * Gets all teams an employee belongs to.
     */
    public List<Team> getTeamsForEmployee(String employeeId) {
        return teamService.getTeamsForEmployee(employeeId);
    }

    /**
     * Gets all members of a team.
     */
    public List<TeamMember> getTeamMembers(String teamId) {
        return teamService.getTeamMembers(teamId);
    }

    /**
     * Deletes a team.
     */
    public void deleteTeam(String teamId) {
        teamService.deleteTeam(teamId);
    }

    // ==================== Hierarchy Operations ====================

    /**
     * Gets a hierarchical view starting from any employee.
     * Shows both upward (managers) and downward (subordinates) hierarchy.
     */
    public String getHierarchicalView(String employeeId) {
        return hierarchyService.getHierarchicalView(employeeId);
    }

    /**
     * Gets the complete organizational tree.
     */
    public String getOrganizationTree() {
        return hierarchyService.getOrganizationTree();
    }

    /**
     * Gets the organizational sub-tree rooted at a specific employee.
     */
    public String getSubTree(String employeeId) {
        return hierarchyService.getSubTree(employeeId);
    }

    /**
     * Gets the span of control report for all managers.
     */
    public String getSpanOfControlReport() {
        return hierarchyService.getSpanOfControlReport();
    }

    /**
     * Gets the hierarchy level of an employee.
     */
    public int getHierarchyLevel(String employeeId) {
        return hierarchyService.getHierarchyLevel(employeeId);
    }

    // ==================== CTC Operations ====================

    /**
     * Gets the CTC for a specific employee.
     */
    public double getEmployeeCTC(String employeeId) {
        return ctcCalculatorService.getEmployeeCTC(employeeId);
    }

    /**
     * Gets detailed CTC breakdown for an employee.
     */
    public CTCCalculatorService.CTCBreakdown getEmployeeCTCBreakdown(String employeeId) {
        return ctcCalculatorService.getEmployeeCTCBreakdown(employeeId);
    }

    /**
     * Gets the total CTC for a team.
     */
    public double getTeamCTC(String teamId) {
        return ctcCalculatorService.getTeamCTC(teamId);
    }

    /**
     * Gets detailed CTC breakdown for a team.
     */
    public CTCCalculatorService.TeamCTCBreakdown getTeamCTCBreakdown(String teamId) {
        return ctcCalculatorService.getTeamCTCBreakdown(teamId);
    }

    /**
     * Gets the total CTC for a manager and all their subordinates.
     */
    public double getManagerTotalCTC(String managerId) {
        return ctcCalculatorService.getManagerTotalCTC(managerId);
    }

    /**
     * Gets CTC breakdown for a manager and all subordinates.
     */
    public CTCCalculatorService.ManagerCTCBreakdown getManagerCTCBreakdown(String managerId) {
        return ctcCalculatorService.getManagerCTCBreakdown(managerId);
    }

    /**
     * Gets the total CTC for a department.
     */
    public double getDepartmentCTC(Department department) {
        return ctcCalculatorService.getDepartmentCTC(department);
    }

    /**
     * Gets CTC breakdown for a department.
     */
    public CTCCalculatorService.DepartmentCTCBreakdown getDepartmentCTCBreakdown(Department department) {
        return ctcCalculatorService.getDepartmentCTCBreakdown(department);
    }

    /**
     * Gets the total organization CTC.
     */
    public double getTotalOrganizationCTC() {
        return ctcCalculatorService.getTotalOrganizationCTC();
    }

    /**
     * Generates a comprehensive CTC report.
     */
    public String generateCTCReport() {
        return ctcCalculatorService.generateCTCReport();
    }
}
