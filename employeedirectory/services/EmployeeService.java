package employeedirectory.services;

import employeedirectory.enums.Department;
import employeedirectory.enums.EmployeeStatus;
import employeedirectory.exceptions.DuplicateEmployeeException;
import employeedirectory.exceptions.EmployeeNotFoundException;
import employeedirectory.exceptions.InvalidHierarchyException;
import employeedirectory.models.Employee;
import employeedirectory.repositories.EmployeeRepository;

import java.util.List;

/**
 * Service for managing employees.
 */
public class EmployeeService {
    
    private final EmployeeRepository employeeRepository;

    public EmployeeService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    /**
     * Adds a new employee to the directory.
     */
    public Employee addEmployee(String employeeId, String name, String email,
                                String designation, Department department, double baseSalary) {
        if (employeeRepository.existsById(employeeId)) {
            throw new DuplicateEmployeeException(employeeId);
        }
        
        Employee employee = new Employee(employeeId, name, email, designation, department, baseSalary);
        return employeeRepository.save(employee);
    }

    /**
     * Gets an employee by ID.
     */
    public Employee getEmployee(String employeeId) {
        return employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EmployeeNotFoundException(employeeId));
    }

    /**
     * Updates employee details.
     */
    public Employee updateEmployee(String employeeId, String name, String email,
                                   String designation, Department department) {
        Employee employee = getEmployee(employeeId);
        
        if (name != null) employee.setName(name);
        if (email != null) employee.setEmail(email);
        if (designation != null) employee.setDesignation(designation);
        if (department != null) employee.setDepartment(department);
        
        return employeeRepository.save(employee);
    }

    /**
     * Updates employee compensation.
     */
    public Employee updateCompensation(String employeeId, double baseSalary, 
                                       double bonus, double allowances) {
        Employee employee = getEmployee(employeeId);
        employee.setBaseSalary(baseSalary);
        employee.setBonus(bonus);
        employee.setAllowances(allowances);
        return employeeRepository.save(employee);
    }

    /**
     * Assigns a manager to an employee.
     * Validates that no circular hierarchy is created.
     */
    public void assignManager(String employeeId, String managerId) {
        Employee employee = getEmployee(employeeId);
        Employee manager = getEmployee(managerId);

        // Check for self-assignment
        if (employeeId.equals(managerId)) {
            throw new InvalidHierarchyException("An employee cannot be their own manager");
        }

        // Check for circular hierarchy
        if (wouldCreateCycle(employee, manager)) {
            throw new InvalidHierarchyException(
                "Assigning this manager would create a circular hierarchy");
        }

        employee.setManager(manager);
        employeeRepository.save(employee);
    }

    /**
     * Removes the manager assignment from an employee.
     */
    public void removeManager(String employeeId) {
        Employee employee = getEmployee(employeeId);
        employee.setManager(null);
        employeeRepository.save(employee);
    }

    /**
     * Changes employee status.
     */
    public void changeStatus(String employeeId, EmployeeStatus status) {
        Employee employee = getEmployee(employeeId);
        employee.setStatus(status);
        employeeRepository.save(employee);
    }

    /**
     * Gets all employees.
     */
    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    /**
     * Gets employees by department.
     */
    public List<Employee> getEmployeesByDepartment(Department department) {
        return employeeRepository.findByDepartment(department);
    }

    /**
     * Gets employees by status.
     */
    public List<Employee> getEmployeesByStatus(EmployeeStatus status) {
        return employeeRepository.findByStatus(status);
    }

    /**
     * Searches employees by name.
     */
    public List<Employee> searchByName(String name) {
        return employeeRepository.findByName(name);
    }

    /**
     * Gets direct reports of a manager.
     */
    public List<Employee> getDirectReports(String managerId) {
        Employee manager = getEmployee(managerId);
        return manager.getDirectReports();
    }

    /**
     * Gets all subordinates (direct and indirect) of a manager.
     */
    public List<Employee> getAllSubordinates(String managerId) {
        Employee manager = getEmployee(managerId);
        return manager.getAllSubordinates();
    }

    /**
     * Gets the management chain for an employee.
     */
    public List<Employee> getManagementChain(String employeeId) {
        Employee employee = getEmployee(employeeId);
        return employee.getManagementChain();
    }

    /**
     * Gets employees without a manager (top-level employees).
     */
    public List<Employee> getTopLevelEmployees() {
        return employeeRepository.findAll().stream()
                .filter(e -> e.getManager() == null)
                .toList();
    }

    /**
     * Deletes an employee.
     * Reassigns their direct reports to their manager first.
     */
    public void deleteEmployee(String employeeId) {
        Employee employee = getEmployee(employeeId);
        Employee manager = employee.getManager();
        
        // Reassign direct reports to the employee's manager
        for (Employee report : employee.getDirectReports()) {
            report.setManager(manager);
            employeeRepository.save(report);
        }
        
        // Remove from manager's direct reports
        employee.setManager(null);
        
        employeeRepository.deleteById(employeeId);
    }

    /**
     * Checks if assigning a manager would create a circular hierarchy.
     */
    private boolean wouldCreateCycle(Employee employee, Employee potentialManager) {
        Employee current = potentialManager;
        while (current != null) {
            if (current.equals(employee)) {
                return true;
            }
            current = current.getManager();
        }
        return false;
    }
}
