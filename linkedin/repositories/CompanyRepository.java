package linkedin.repositories;

import linkedin.models.Company;
import java.util.List;
import java.util.Optional;

public interface CompanyRepository extends Repository<Company, String> {
    Optional<Company> findByName(String name);
    List<Company> findByNameContaining(String name);
    List<Company> findByIndustry(String industry);
    List<Company> findByLocation(String location);
    List<Company> findByEmployeeId(String employeeId);
}



