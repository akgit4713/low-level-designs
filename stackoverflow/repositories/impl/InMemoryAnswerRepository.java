package stackoverflow.repositories.impl;

import stackoverflow.models.Answer;
import stackoverflow.models.Question;
import stackoverflow.models.User;
import stackoverflow.repositories.AnswerRepository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Thread-safe in-memory implementation of AnswerRepository.
 */
public class InMemoryAnswerRepository implements AnswerRepository {
    private final Map<String, Answer> answers = new ConcurrentHashMap<>();

    @Override
    public Answer save(Answer answer) {
        answers.put(answer.getId(), answer);
        return answer;
    }

    @Override
    public Optional<Answer> findById(String id) {
        return Optional.ofNullable(answers.get(id));
    }

    @Override
    public List<Answer> findAll() {
        return new ArrayList<>(answers.values());
    }

    @Override
    public void delete(String id) {
        answers.remove(id);
    }

    @Override
    public boolean exists(String id) {
        return answers.containsKey(id);
    }

    @Override
    public long count() {
        return answers.size();
    }

    @Override
    public List<Answer> findByQuestion(Question question) {
        return answers.values().stream()
                .filter(a -> a.getQuestion().getId().equals(question.getId()))
                .sorted(Comparator.comparingInt(Answer::getVoteCount).reversed())
                .collect(Collectors.toList());
    }

    @Override
    public List<Answer> findByAuthor(User author) {
        return answers.values().stream()
                .filter(a -> a.getAuthor().getId().equals(author.getId()))
                .sorted(Comparator.comparing(Answer::getCreatedAt).reversed())
                .collect(Collectors.toList());
    }
}



