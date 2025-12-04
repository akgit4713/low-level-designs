package stackoverflow.repositories;

import stackoverflow.models.Question;
import stackoverflow.models.User;

import java.util.List;

/**
 * Repository interface for Question operations.
 */
public interface QuestionRepository extends Repository<Question, String> {
    List<Question> findByTag(String tagName);
    List<Question> findByAuthor(User author);
    List<Question> searchByKeyword(String keyword);
    List<Question> findByTags(List<String> tagNames);
}



