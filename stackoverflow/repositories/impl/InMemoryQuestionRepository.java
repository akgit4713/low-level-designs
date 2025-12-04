package stackoverflow.repositories.impl;

import stackoverflow.models.Question;
import stackoverflow.models.User;
import stackoverflow.repositories.QuestionRepository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Thread-safe in-memory implementation of QuestionRepository.
 */
public class InMemoryQuestionRepository implements QuestionRepository {
    private final Map<String, Question> questions = new ConcurrentHashMap<>();

    @Override
    public Question save(Question question) {
        questions.put(question.getId(), question);
        return question;
    }

    @Override
    public Optional<Question> findById(String id) {
        return Optional.ofNullable(questions.get(id));
    }

    @Override
    public List<Question> findAll() {
        return new ArrayList<>(questions.values());
    }

    @Override
    public void delete(String id) {
        questions.remove(id);
    }

    @Override
    public boolean exists(String id) {
        return questions.containsKey(id);
    }

    @Override
    public long count() {
        return questions.size();
    }

    @Override
    public List<Question> findByTag(String tagName) {
        return questions.values().stream()
                .filter(q -> q.hasTag(tagName))
                .sorted(Comparator.comparingInt(Question::getVoteCount).reversed())
                .collect(Collectors.toList());
    }

    @Override
    public List<Question> findByAuthor(User author) {
        return questions.values().stream()
                .filter(q -> q.getAuthor().getId().equals(author.getId()))
                .sorted(Comparator.comparing(Question::getCreatedAt).reversed())
                .collect(Collectors.toList());
    }

    @Override
    public List<Question> searchByKeyword(String keyword) {
        String lowerKeyword = keyword.toLowerCase();
        return questions.values().stream()
                .filter(q -> q.getTitle().toLowerCase().contains(lowerKeyword) ||
                            q.getContent().toLowerCase().contains(lowerKeyword))
                .sorted(Comparator.comparingInt(Question::getVoteCount).reversed())
                .collect(Collectors.toList());
    }

    @Override
    public List<Question> findByTags(List<String> tagNames) {
        return questions.values().stream()
                .filter(q -> tagNames.stream().anyMatch(q::hasTag))
                .sorted(Comparator.comparingInt(Question::getVoteCount).reversed())
                .collect(Collectors.toList());
    }
}



