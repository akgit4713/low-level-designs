package employeedirectory.repositories.impl;

import employeedirectory.enums.Department;
import employeedirectory.enums.EmployeeStatus;
import employeedirectory.models.Employee;
import employeedirectory.repositories.EmployeeRepository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * In-memory implementation of EmployeeRepository.
 * Thread-safe using ConcurrentHashMap.
 */
public class InMemoryEmployeeRepository implements EmployeeRepository {
    
    private final Map<String, Employee> employees = new ConcurrentHashMap<>();

    @Override
    public Employee save(Employee employee) {
        employees.put(employee.getEmployeeId(), employee);
        return employee;
    }

    @Override
    public Optional<Employee> findById(String employeeId) {
        return Optional.ofNullable(employees.get(employeeId));
    }

    @Override
    public List<Employee> findByName(String name) {
        String lowerName = name.toLowerCase();
        return employees.values().stream()
                .filter(e -> e.getName().toLowerCase().contains(lowerName))
                .collect(Collectors.toList());
    }

    @Override
    public List<Employee> findByDepartment(Department department) {
        return employees.values().stream()
                .filter(e -> e.getDepartment() == department)
                .collect(Collectors.toList());
    }

    @Override
    public List<Employee> findByStatus(EmployeeStatus status) {
        return employees.values().stream()
                .filter(e -> e.getStatus() == status)
                .collect(Collectors.toList());
    }

    @Override
    public List<Employee> findByManager(String managerId) {
        return employees.values().stream()
                .filter(e -> e.getManager() != null && 
                        e.getManager().getEmployeeId().equals(managerId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Employee> findAll() {
        return new ArrayList<>(employees.values());
    }

    @Override
    public boolean deleteById(String employeeId) {
        return employees.remove(employeeId) != null;
    }

    @Override
    public boolean existsById(String employeeId) {
        return employees.containsKey(employeeId);
    }

    @Override
    public long count() {
        return employees.size();
    }
}
