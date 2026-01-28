package employeedirectory.repositories;

import employeedirectory.enums.Department;
import employeedirectory.enums.EmployeeStatus;
import employeedirectory.models.Employee;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Employee operations.
 */
public interface EmployeeRepository {
    
    /**
     * Saves an employee to the repository.
     */
    Employee save(Employee employee);

    /**
     * Finds an employee by ID.
     */
    Optional<Employee> findById(String employeeId);

    /**
     * Finds employees by name (partial match).
     */
    List<Employee> findByName(String name);

    /**
     * Finds all employees in a department.
     */
    List<Employee> findByDepartment(Department department);

    /**
     * Finds all employees with a specific status.
     */
    List<Employee> findByStatus(EmployeeStatus status);

    /**
     * Finds all employees managed by a specific manager.
     */
    List<Employee> findByManager(String managerId);

    /**
     * Gets all employees.
     */
    List<Employee> findAll();

    /**
     * Deletes an employee by ID.
     */
    boolean deleteById(String employeeId);

    /**
     * Checks if an employee exists.
     */
    boolean existsById(String employeeId);

    /**
     * Gets the count of all employees.
     */
    long count();
}
