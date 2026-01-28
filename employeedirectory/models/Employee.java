package employeedirectory.models;

import employeedirectory.enums.Department;
import employeedirectory.enums.EmployeeStatus;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Represents an employee in the organization.
 * Forms a tree structure with manager-reportee relationships.
 */
public class Employee {
    private final String employeeId;
    private String name;
    private String email;
    private String designation;
    private Department department;
    private double baseSalary;
    private double bonus;
    private double allowances;
    private EmployeeStatus status;
    private LocalDate joiningDate;
    private Employee manager;
    private final List<Employee> directReports;

    public Employee(String employeeId, String name, String email, String designation,
                    Department department, double baseSalary) {
        this.employeeId = employeeId;
        this.name = name;
        this.email = email;
        this.designation = designation;
        this.department = department;
        this.baseSalary = baseSalary;
        this.bonus = 0;
        this.allowances = 0;
        this.status = EmployeeStatus.ACTIVE;
        this.joiningDate = LocalDate.now();
        this.manager = null;
        this.directReports = new ArrayList<>();
    }

    // Getters
    public String getEmployeeId() {
        return employeeId;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getDesignation() {
        return designation;
    }

    public Department getDepartment() {
        return department;
    }

    public double getBaseSalary() {
        return baseSalary;
    }

    public double getBonus() {
        return bonus;
    }

    public double getAllowances() {
        return allowances;
    }

    public EmployeeStatus getStatus() {
        return status;
    }

    public LocalDate getJoiningDate() {
        return joiningDate;
    }

    public Employee getManager() {
        return manager;
    }

    public List<Employee> getDirectReports() {
        return Collections.unmodifiableList(directReports);
    }

    // Setters
    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    public void setBaseSalary(double baseSalary) {
        this.baseSalary = baseSalary;
    }

    public void setBonus(double bonus) {
        this.bonus = bonus;
    }

    public void setAllowances(double allowances) {
        this.allowances = allowances;
    }

    public void setStatus(EmployeeStatus status) {
        this.status = status;
    }

    public void setJoiningDate(LocalDate joiningDate) {
        this.joiningDate = joiningDate;
    }

    /**
     * Sets the manager for this employee.
     * Handles bidirectional relationship.
     */
    public void setManager(Employee manager) {
        // Remove from old manager's direct reports
        if (this.manager != null) {
            this.manager.removeDirectReport(this);
        }
        
        this.manager = manager;
        
        // Add to new manager's direct reports
        if (manager != null) {
            manager.addDirectReport(this);
        }
    }

    /**
     * Adds a direct report to this employee.
     */
    void addDirectReport(Employee employee) {
        if (!directReports.contains(employee)) {
            directReports.add(employee);
        }
    }

    /**
     * Removes a direct report from this employee.
     */
    void removeDirectReport(Employee employee) {
        directReports.remove(employee);
    }

    /**
     * Calculates the CTC (Cost to Company) for this employee.
     * CTC = Base Salary + Bonus + Allowances
     */
    public double calculateCTC() {
        return baseSalary + bonus + allowances;
    }

    /**
     * Checks if this employee is a manager (has direct reports).
     */
    public boolean isManager() {
        return !directReports.isEmpty();
    }

    /**
     * Gets the management chain (all managers up to the top).
     */
    public List<Employee> getManagementChain() {
        List<Employee> chain = new ArrayList<>();
        Employee current = this.manager;
        while (current != null) {
            chain.add(current);
            current = current.getManager();
        }
        return chain;
    }

    /**
     * Gets all subordinates recursively (direct and indirect reports).
     */
    public List<Employee> getAllSubordinates() {
        List<Employee> subordinates = new ArrayList<>();
        collectSubordinates(this, subordinates);
        return subordinates;
    }

    private void collectSubordinates(Employee employee, List<Employee> subordinates) {
        for (Employee report : employee.getDirectReports()) {
            subordinates.add(report);
            collectSubordinates(report, subordinates);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Employee employee = (Employee) o;
        return Objects.equals(employeeId, employee.employeeId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(employeeId);
    }

    @Override
    public String toString() {
        return String.format("Employee[id=%s, name=%s, designation=%s, department=%s]",
                employeeId, name, designation, department.getDisplayName());
    }
}
