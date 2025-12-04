package stackoverflow.repositories;

import stackoverflow.models.Tag;

import java.util.Optional;

/**
 * Repository interface for Tag operations.
 */
public interface TagRepository extends Repository<Tag, String> {
    Optional<Tag> findByName(String name);
    Tag getOrCreate(String name);
}



