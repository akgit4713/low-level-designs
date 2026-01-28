package employeedirectory.services;

import employeedirectory.exceptions.EmployeeNotFoundException;
import employeedirectory.models.Employee;
import employeedirectory.repositories.EmployeeRepository;

import java.util.ArrayList;
import java.util.List;

/**
 * Service for hierarchical operations and visualizations.
 */
public class HierarchyService {
    
    private final EmployeeRepository employeeRepository;

    public HierarchyService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    /**
     * Generates a hierarchical view starting from any employee (showing both upward and downward hierarchy).
     */
    public String getHierarchicalView(String employeeId) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EmployeeNotFoundException(employeeId));
        
        StringBuilder sb = new StringBuilder();
        
        // Show management chain (upward hierarchy)
        List<Employee> managementChain = employee.getManagementChain();
        if (!managementChain.isEmpty()) {
            sb.append("=== Management Chain (Upward) ===\n");
            for (int i = managementChain.size() - 1; i >= 0; i--) {
                Employee manager = managementChain.get(i);
                int level = managementChain.size() - i - 1;
                sb.append(getIndent(level))
                  .append("└── ")
                  .append(formatEmployee(manager))
                  .append("\n");
            }
            sb.append(getIndent(managementChain.size()))
              .append("└── ")
              .append(formatEmployee(employee))
              .append(" ← (Current)\n");
        } else {
            sb.append("=== ")
              .append(formatEmployee(employee))
              .append(" (Top Level) ===\n");
        }
        
        // Show direct reports (downward hierarchy)
        if (!employee.getDirectReports().isEmpty()) {
            sb.append("\n=== Subordinates (Downward) ===\n");
            sb.append(formatEmployee(employee)).append(" (Current)\n");
            appendSubordinates(sb, employee, 0);
        }
        
        return sb.toString();
    }

    /**
     * Generates the complete organizational tree from all top-level employees.
     */
    public String getOrganizationTree() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== Organization Hierarchy ===\n\n");
        
        List<Employee> topLevel = getTopLevelEmployees();
        
        if (topLevel.isEmpty()) {
            sb.append("No employees in the organization.\n");
            return sb.toString();
        }
        
        for (Employee root : topLevel) {
            appendTree(sb, root, 0, true);
            sb.append("\n");
        }
        
        return sb.toString();
    }

    /**
     * Gets the organizational tree rooted at a specific employee.
     */
    public String getSubTree(String employeeId) {
        Employee root = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EmployeeNotFoundException(employeeId));
        
        StringBuilder sb = new StringBuilder();
        sb.append("=== Sub-Tree for ")
          .append(root.getName())
          .append(" ===\n\n");
        
        appendTree(sb, root, 0, true);
        
        return sb.toString();
    }

    /**
     * Gets employees at a specific level in the hierarchy.
     * Level 0 = top-level employees (no manager).
     */
    public List<Employee> getEmployeesAtLevel(int level) {
        List<Employee> result = new ArrayList<>();
        
        if (level == 0) {
            return getTopLevelEmployees();
        }
        
        for (Employee employee : employeeRepository.findAll()) {
            if (getHierarchyLevel(employee) == level) {
                result.add(employee);
            }
        }
        
        return result;
    }

    /**
     * Gets the hierarchy level of an employee.
     * Level 0 = no manager (top level).
     */
    public int getHierarchyLevel(String employeeId) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EmployeeNotFoundException(employeeId));
        return getHierarchyLevel(employee);
    }

    /**
     * Calculates the depth of the organizational tree.
     */
    public int getOrganizationDepth() {
        int maxDepth = 0;
        for (Employee employee : employeeRepository.findAll()) {
            int depth = getHierarchyLevel(employee);
            maxDepth = Math.max(maxDepth, depth);
        }
        return maxDepth;
    }

    /**
     * Gets the span of control (number of direct reports) for all managers.
     */
    public String getSpanOfControlReport() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== Span of Control Report ===\n\n");
        
        List<Employee> managers = employeeRepository.findAll().stream()
                .filter(Employee::isManager)
                .toList();
        
        if (managers.isEmpty()) {
            sb.append("No managers found.\n");
            return sb.toString();
        }
        
        sb.append(String.format("%-20s %-20s %-15s %-15s%n", 
                "Employee", "Designation", "Direct Reports", "Total Reports"));
        sb.append("-".repeat(70)).append("\n");
        
        for (Employee manager : managers) {
            sb.append(String.format("%-20s %-20s %-15d %-15d%n",
                    truncate(manager.getName(), 20),
                    truncate(manager.getDesignation(), 20),
                    manager.getDirectReports().size(),
                    manager.getAllSubordinates().size()));
        }
        
        return sb.toString();
    }

    // Private helper methods

    private List<Employee> getTopLevelEmployees() {
        return employeeRepository.findAll().stream()
                .filter(e -> e.getManager() == null)
                .toList();
    }

    private int getHierarchyLevel(Employee employee) {
        int level = 0;
        Employee current = employee;
        while (current.getManager() != null) {
            level++;
            current = current.getManager();
        }
        return level;
    }

    private void appendTree(StringBuilder sb, Employee employee, int depth, boolean isLast) {
        String prefix = depth == 0 ? "" : getTreePrefix(depth, isLast);
        sb.append(prefix)
          .append(formatEmployee(employee))
          .append("\n");
        
        List<Employee> reports = employee.getDirectReports();
        for (int i = 0; i < reports.size(); i++) {
            boolean lastChild = (i == reports.size() - 1);
            appendTree(sb, reports.get(i), depth + 1, lastChild);
        }
    }

    private void appendSubordinates(StringBuilder sb, Employee employee, int depth) {
        List<Employee> reports = employee.getDirectReports();
        for (int i = 0; i < reports.size(); i++) {
            Employee report = reports.get(i);
            boolean isLast = (i == reports.size() - 1);
            String connector = isLast ? "└── " : "├── ";
            
            sb.append(getIndent(depth + 1))
              .append(connector)
              .append(formatEmployee(report))
              .append("\n");
            
            if (!report.getDirectReports().isEmpty()) {
                appendSubordinates(sb, report, depth + 1);
            }
        }
    }

    private String getIndent(int level) {
        return "    ".repeat(level);
    }

    private String getTreePrefix(int depth, boolean isLast) {
        StringBuilder prefix = new StringBuilder();
        for (int i = 0; i < depth - 1; i++) {
            prefix.append("│   ");
        }
        prefix.append(isLast ? "└── " : "├── ");
        return prefix.toString();
    }

    private String formatEmployee(Employee e) {
        return String.format("%s (%s, %s)", 
                e.getName(), 
                e.getDesignation(), 
                e.getDepartment().getDisplayName());
    }

    private String truncate(String str, int maxLen) {
        if (str == null) return "";
        return str.length() > maxLen ? str.substring(0, maxLen - 3) + "..." : str;
    }
}
