package stackoverflow.repositories;

import stackoverflow.models.Answer;
import stackoverflow.models.Question;
import stackoverflow.models.User;

import java.util.List;

/**
 * Repository interface for Answer operations.
 */
public interface AnswerRepository extends Repository<Answer, String> {
    List<Answer> findByQuestion(Question question);
    List<Answer> findByAuthor(User author);
}



