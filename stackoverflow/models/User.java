package stackoverflow.models;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Represents a user in the Stack Overflow system.
 * Thread-safe for concurrent access.
 */
public class User {
    private final String id;
    private final String username;
    private final String email;
    private final AtomicInteger reputation;
    private final List<Question> questions;
    private final List<Answer> answers;
    private final LocalDateTime createdAt;

    public User(String username, String email) {
        this.id = UUID.randomUUID().toString().substring(0, 8);
        this.username = username;
        this.email = email;
        this.reputation = new AtomicInteger(1); // Starting reputation
        this.questions = Collections.synchronizedList(new ArrayList<>());
        this.answers = Collections.synchronizedList(new ArrayList<>());
        this.createdAt = LocalDateTime.now();
    }

    public String getId() { return id; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public int getReputation() { return reputation.get(); }
    public List<Question> getQuestions() { return new ArrayList<>(questions); }
    public List<Answer> getAnswers() { return new ArrayList<>(answers); }
    public LocalDateTime getCreatedAt() { return createdAt; }

    public int updateReputation(int points) {
        return reputation.updateAndGet(current -> Math.max(1, current + points));
    }

    public void addQuestion(Question question) {
        questions.add(question);
    }

    public void addAnswer(Answer answer) {
        answers.add(answer);
    }

    public int getQuestionCount() {
        return questions.size();
    }

    public int getAnswerCount() {
        return answers.size();
    }

    @Override
    public String toString() {
        return String.format("User[%s, rep=%d, Q=%d, A=%d]", 
            username, reputation.get(), questions.size(), answers.size());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id.equals(user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

