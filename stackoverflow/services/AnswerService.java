package stackoverflow.services;

import stackoverflow.exceptions.AnswerNotFoundException;
import stackoverflow.exceptions.UnauthorizedException;
import stackoverflow.models.Answer;
import stackoverflow.models.Question;
import stackoverflow.models.User;
import stackoverflow.repositories.AnswerRepository;

import java.util.List;

/**
 * Service for answer management operations.
 */
public class AnswerService {
    private final AnswerRepository answerRepository;

    public AnswerService(AnswerRepository answerRepository) {
        this.answerRepository = answerRepository;
    }

    public Answer createAnswer(User author, Question question, String content) {
        Answer answer = new Answer(content, author, question);
        answer = answerRepository.save(answer);
        question.addAnswer(answer);
        author.addAnswer(answer);
        return answer;
    }

    public Answer getAnswerById(String answerId) {
        return answerRepository.findById(answerId)
                .orElseThrow(() -> new AnswerNotFoundException(answerId));
    }

    public List<Answer> getAnswersByQuestion(Question question) {
        return answerRepository.findByQuestion(question);
    }

    public List<Answer> getAnswersByAuthor(User author) {
        return answerRepository.findByAuthor(author);
    }

    public void acceptAnswer(User questionAuthor, Answer answer) {
        Question question = answer.getQuestion();
        
        if (!question.getAuthor().getId().equals(questionAuthor.getId())) {
            throw new UnauthorizedException("Only the question author can accept answers");
        }

        // Unaccept previously accepted answer if any
        question.getAnswers().stream()
                .filter(Answer::isAccepted)
                .forEach(a -> a.setAccepted(false));

        answer.accept();
        answerRepository.save(answer);
    }

    public long getAnswerCount() {
        return answerRepository.count();
    }
}



