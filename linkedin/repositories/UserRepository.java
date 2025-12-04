package linkedin.repositories;

import linkedin.models.User;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends Repository<User, String> {
    Optional<User> findByEmail(String email);
    List<User> findByNameContaining(String name);
    List<User> findBySkill(String skillName);
    List<User> findByLocation(String location);
    List<User> findByIndustry(String industry);
}



