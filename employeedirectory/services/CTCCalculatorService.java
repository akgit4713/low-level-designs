package employeedirectory.services;

import employeedirectory.enums.Department;
import employeedirectory.exceptions.EmployeeNotFoundException;
import employeedirectory.exceptions.TeamNotFoundException;
import employeedirectory.models.Employee;
import employeedirectory.models.Team;
import employeedirectory.models.TeamMember;
import employeedirectory.repositories.EmployeeRepository;
import employeedirectory.repositories.TeamRepository;

import java.util.*;

/**
 * Service for CTC (Cost to Company) calculations.
 */
public class CTCCalculatorService {
    
    private final EmployeeRepository employeeRepository;
    private final TeamRepository teamRepository;

    public CTCCalculatorService(EmployeeRepository employeeRepository, TeamRepository teamRepository) {
        this.employeeRepository = employeeRepository;
        this.teamRepository = teamRepository;
    }

    /**
     * Gets the CTC for a specific employee.
     */
    public double getEmployeeCTC(String employeeId) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EmployeeNotFoundException(employeeId));
        return employee.calculateCTC();
    }

    /**
     * Gets detailed CTC breakdown for an employee.
     */
    public CTCBreakdown getEmployeeCTCBreakdown(String employeeId) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EmployeeNotFoundException(employeeId));
        
        return new CTCBreakdown(
                employee.getName(),
                employee.getBaseSalary(),
                employee.getBonus(),
                employee.getAllowances(),
                employee.calculateCTC()
        );
    }

    /**
     * Gets the total CTC for a team.
     */
    public double getTeamCTC(String teamId) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new TeamNotFoundException(teamId));
        return team.calculateTotalCTC();
    }

    /**
     * Gets detailed CTC breakdown for a team.
     */
    public TeamCTCBreakdown getTeamCTCBreakdown(String teamId) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new TeamNotFoundException(teamId));
        
        List<CTCBreakdown> memberBreakdowns = new ArrayList<>();
        for (TeamMember member : team.getMembers()) {
            Employee emp = member.getEmployee();
            memberBreakdowns.add(new CTCBreakdown(
                    emp.getName(),
                    emp.getBaseSalary(),
                    emp.getBonus(),
                    emp.getAllowances(),
                    emp.calculateCTC()
            ));
        }
        
        return new TeamCTCBreakdown(
                team.getName(),
                team.getSize(),
                team.calculateTotalCTC(),
                team.calculateAverageCTC(),
                memberBreakdowns
        );
    }

    /**
     * Gets the total CTC for a manager and all their subordinates.
     */
    public double getManagerTotalCTC(String managerId) {
        Employee manager = employeeRepository.findById(managerId)
                .orElseThrow(() -> new EmployeeNotFoundException(managerId));
        
        double total = manager.calculateCTC();
        for (Employee subordinate : manager.getAllSubordinates()) {
            total += subordinate.calculateCTC();
        }
        return total;
    }

    /**
     * Gets CTC breakdown for a manager and all subordinates.
     */
    public ManagerCTCBreakdown getManagerCTCBreakdown(String managerId) {
        Employee manager = employeeRepository.findById(managerId)
                .orElseThrow(() -> new EmployeeNotFoundException(managerId));
        
        List<Employee> allSubordinates = manager.getAllSubordinates();
        
        double managerCTC = manager.calculateCTC();
        double subordinatesCTC = allSubordinates.stream()
                .mapToDouble(Employee::calculateCTC)
                .sum();
        
        return new ManagerCTCBreakdown(
                manager.getName(),
                manager.getDesignation(),
                managerCTC,
                allSubordinates.size(),
                subordinatesCTC,
                managerCTC + subordinatesCTC
        );
    }

    /**
     * Gets the total CTC for a department.
     */
    public double getDepartmentCTC(Department department) {
        return employeeRepository.findByDepartment(department).stream()
                .mapToDouble(Employee::calculateCTC)
                .sum();
    }

    /**
     * Gets CTC breakdown for a department.
     */
    public DepartmentCTCBreakdown getDepartmentCTCBreakdown(Department department) {
        List<Employee> employees = employeeRepository.findByDepartment(department);
        
        double totalCTC = employees.stream()
                .mapToDouble(Employee::calculateCTC)
                .sum();
        
        double avgCTC = employees.isEmpty() ? 0 : totalCTC / employees.size();
        
        double minCTC = employees.stream()
                .mapToDouble(Employee::calculateCTC)
                .min()
                .orElse(0);
        
        double maxCTC = employees.stream()
                .mapToDouble(Employee::calculateCTC)
                .max()
                .orElse(0);
        
        return new DepartmentCTCBreakdown(
                department.getDisplayName(),
                employees.size(),
                totalCTC,
                avgCTC,
                minCTC,
                maxCTC
        );
    }

    /**
     * Gets the total organization CTC.
     */
    public double getTotalOrganizationCTC() {
        return employeeRepository.findAll().stream()
                .mapToDouble(Employee::calculateCTC)
                .sum();
    }

    /**
     * Generates a comprehensive CTC report.
     */
    public String generateCTCReport() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== Organization CTC Report ===\n\n");
        
        double totalOrgCTC = getTotalOrganizationCTC();
        long totalEmployees = employeeRepository.count();
        
        sb.append(String.format("Total Organization CTC: $%.2f%n", totalOrgCTC));
        sb.append(String.format("Total Employees: %d%n", totalEmployees));
        sb.append(String.format("Average CTC per Employee: $%.2f%n%n", 
                totalEmployees > 0 ? totalOrgCTC / totalEmployees : 0));
        
        // Department-wise breakdown
        sb.append("--- Department-wise Breakdown ---\n");
        sb.append(String.format("%-20s %-12s %-15s %-15s%n", 
                "Department", "Employees", "Total CTC", "Avg CTC"));
        sb.append("-".repeat(62)).append("\n");
        
        for (Department dept : Department.values()) {
            DepartmentCTCBreakdown breakdown = getDepartmentCTCBreakdown(dept);
            if (breakdown.employeeCount() > 0) {
                sb.append(String.format("%-20s %-12d $%-14.2f $%-14.2f%n",
                        breakdown.departmentName(),
                        breakdown.employeeCount(),
                        breakdown.totalCTC(),
                        breakdown.averageCTC()));
            }
        }
        
        // Team-wise breakdown
        sb.append("\n--- Team-wise Breakdown ---\n");
        sb.append(String.format("%-25s %-10s %-15s %-15s%n", 
                "Team", "Members", "Total CTC", "Avg CTC"));
        sb.append("-".repeat(65)).append("\n");
        
        for (Team team : teamRepository.findAll()) {
            sb.append(String.format("%-25s %-10d $%-14.2f $%-14.2f%n",
                    truncate(team.getName(), 25),
                    team.getSize(),
                    team.calculateTotalCTC(),
                    team.calculateAverageCTC()));
        }
        
        return sb.toString();
    }

    private String truncate(String str, int maxLen) {
        if (str == null) return "";
        return str.length() > maxLen ? str.substring(0, maxLen - 3) + "..." : str;
    }

    // DTO Records for CTC breakdowns
    
    public record CTCBreakdown(
            String employeeName,
            double baseSalary,
            double bonus,
            double allowances,
            double totalCTC
    ) {
        @Override
        public String toString() {
            return String.format(
                    "%s: Base=$%.2f, Bonus=$%.2f, Allowances=$%.2f, Total CTC=$%.2f",
                    employeeName, baseSalary, bonus, allowances, totalCTC);
        }
    }

    public record TeamCTCBreakdown(
            String teamName,
            int memberCount,
            double totalCTC,
            double averageCTC,
            List<CTCBreakdown> memberBreakdowns
    ) {
        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append(String.format("Team: %s%n", teamName));
            sb.append(String.format("Members: %d%n", memberCount));
            sb.append(String.format("Total CTC: $%.2f%n", totalCTC));
            sb.append(String.format("Average CTC: $%.2f%n", averageCTC));
            sb.append("Member Breakdown:\n");
            for (CTCBreakdown member : memberBreakdowns) {
                sb.append("  - ").append(member).append("\n");
            }
            return sb.toString();
        }
    }

    public record ManagerCTCBreakdown(
            String managerName,
            String designation,
            double managerCTC,
            int subordinateCount,
            double subordinatesCTC,
            double totalCTC
    ) {
        @Override
        public String toString() {
            return String.format(
                    "%s (%s): Own CTC=$%.2f, %d Subordinates CTC=$%.2f, Total=$%.2f",
                    managerName, designation, managerCTC, subordinateCount, subordinatesCTC, totalCTC);
        }
    }

    public record DepartmentCTCBreakdown(
            String departmentName,
            int employeeCount,
            double totalCTC,
            double averageCTC,
            double minCTC,
            double maxCTC
    ) {
        @Override
        public String toString() {
            return String.format(
                    "%s: %d employees, Total=$%.2f, Avg=$%.2f, Min=$%.2f, Max=$%.2f",
                    departmentName, employeeCount, totalCTC, averageCTC, minCTC, maxCTC);
        }
    }
}
