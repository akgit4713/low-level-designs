package stackoverflow.models;

import stackoverflow.enums.VoteType;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Represents a question posted by a user.
 * Thread-safe for concurrent access.
 */
public class Question implements Votable, Commentable {
    private final String id;
    private final String title;
    private final String content;
    private final User author;
    private final List<Answer> answers;
    private final List<Comment> comments;
    private final Set<Tag> tags;
    private final List<Vote> votes;
    private final LocalDateTime createdAt;
    private final AtomicInteger viewCount;

    public Question(String title, String content, User author, List<Tag> tags) {
        this.id = UUID.randomUUID().toString().substring(0, 8);
        this.title = title;
        this.content = content;
        this.author = author;
        this.answers = Collections.synchronizedList(new ArrayList<>());
        this.comments = Collections.synchronizedList(new ArrayList<>());
        this.tags = Collections.synchronizedSet(new HashSet<>(tags));
        this.votes = Collections.synchronizedList(new ArrayList<>());
        this.createdAt = LocalDateTime.now();
        this.viewCount = new AtomicInteger(0);
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getContent() { return content; }
    @Override
    public User getAuthor() { return author; }
    public List<Answer> getAnswers() { return new ArrayList<>(answers); }
    public Set<Tag> getTags() { return new HashSet<>(tags); }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public int getViewCount() { return viewCount.get(); }

    public void incrementViewCount() {
        viewCount.incrementAndGet();
    }

    public void addAnswer(Answer answer) {
        answers.add(answer);
    }

    @Override
    public void addComment(Comment comment) {
        comments.add(comment);
    }

    @Override
    public List<Comment> getComments() {
        return new ArrayList<>(comments);
    }

    @Override
    public synchronized void vote(User voter, VoteType voteType) {
        // Check if user already voted - remove previous vote
        votes.removeIf(v -> v.getVoter().getId().equals(voter.getId()));
        votes.add(new Vote(voter, voteType));
    }

    @Override
    public int getVoteCount() {
        return votes.stream().mapToInt(Vote::getValue).sum();
    }

    public boolean hasTag(String tagName) {
        return tags.stream().anyMatch(t -> t.getName().equalsIgnoreCase(tagName));
    }

    public void addTag(Tag tag) {
        tags.add(tag);
    }

    @Override
    public String toString() {
        return String.format("Q[%s]: %s (by %s, votes: %d, answers: %d)", 
            id, title, author.getUsername(), getVoteCount(), answers.size());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Question question = (Question) o;
        return id.equals(question.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

