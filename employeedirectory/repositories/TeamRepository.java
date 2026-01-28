package employeedirectory.repositories;

import employeedirectory.enums.Department;
import employeedirectory.models.Team;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Team operations.
 */
public interface TeamRepository {
    
    /**
     * Saves a team to the repository.
     */
    Team save(Team team);

    /**
     * Finds a team by ID.
     */
    Optional<Team> findById(String teamId);

    /**
     * Finds teams by name (partial match).
     */
    List<Team> findByName(String name);

    /**
     * Finds all teams in a department.
     */
    List<Team> findByDepartment(Department department);

    /**
     * Finds all teams that contain a specific employee.
     */
    List<Team> findByEmployee(String employeeId);

    /**
     * Gets all teams.
     */
    List<Team> findAll();

    /**
     * Deletes a team by ID.
     */
    boolean deleteById(String teamId);

    /**
     * Checks if a team exists.
     */
    boolean existsById(String teamId);

    /**
     * Gets the count of all teams.
     */
    long count();
}
