package stackoverflow.services;

import stackoverflow.models.Question;
import stackoverflow.models.User;
import stackoverflow.repositories.QuestionRepository;
import stackoverflow.repositories.UserRepository;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for advanced search operations.
 */
public class SearchService {
    private final QuestionRepository questionRepository;
    private final UserRepository userRepository;

    public SearchService(QuestionRepository questionRepository, UserRepository userRepository) {
        this.questionRepository = questionRepository;
        this.userRepository = userRepository;
    }

    /**
     * Search questions by keyword in title or content.
     */
    public List<Question> searchByKeyword(String keyword) {
        return questionRepository.searchByKeyword(keyword);
    }

    /**
     * Search questions by tag.
     */
    public List<Question> searchByTag(String tagName) {
        return questionRepository.findByTag(tagName);
    }

    /**
     * Search questions by multiple tags (OR logic).
     */
    public List<Question> searchByTags(List<String> tagNames) {
        return questionRepository.findByTags(tagNames);
    }

    /**
     * Search questions by author's username.
     */
    public List<Question> searchByAuthor(String username) {
        return userRepository.findByUsername(username)
                .map(questionRepository::findByAuthor)
                .orElse(new ArrayList<>());
    }

    /**
     * Search questions by user profile (returns all questions by the user).
     */
    public List<Question> searchByUser(User user) {
        return questionRepository.findByAuthor(user);
    }

    /**
     * Advanced search with multiple criteria.
     */
    public List<Question> advancedSearch(String keyword, List<String> tags, String username) {
        List<Question> results = new ArrayList<>();

        // Start with keyword search if provided
        if (keyword != null && !keyword.isEmpty()) {
            results = searchByKeyword(keyword);
        } else {
            results = questionRepository.findAll();
        }

        // Filter by tags if provided
        if (tags != null && !tags.isEmpty()) {
            results = results.stream()
                    .filter(q -> tags.stream().anyMatch(q::hasTag))
                    .collect(Collectors.toList());
        }

        // Filter by author if provided
        if (username != null && !username.isEmpty()) {
            results = results.stream()
                    .filter(q -> q.getAuthor().getUsername().equalsIgnoreCase(username))
                    .collect(Collectors.toList());
        }

        // Sort by vote count (descending)
        return results.stream()
                .sorted(Comparator.comparingInt(Question::getVoteCount).reversed())
                .collect(Collectors.toList());
    }

    /**
     * Get trending questions (most votes in recent time).
     */
    public List<Question> getTrendingQuestions(int limit) {
        return questionRepository.findAll().stream()
                .sorted(Comparator.comparingInt(Question::getVoteCount).reversed())
                .limit(limit)
                .collect(Collectors.toList());
    }

    /**
     * Get unanswered questions.
     */
    public List<Question> getUnansweredQuestions() {
        return questionRepository.findAll().stream()
                .filter(q -> q.getAnswers().isEmpty())
                .sorted(Comparator.comparing(Question::getCreatedAt).reversed())
                .collect(Collectors.toList());
    }
}



