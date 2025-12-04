package stackoverflow.models;

import stackoverflow.enums.VoteType;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Represents an answer to a question.
 * Thread-safe for concurrent access.
 */
public class Answer implements Votable, Commentable {
    private final String id;
    private final String content;
    private final User author;
    private final Question question;
    private final List<Comment> comments;
    private final List<Vote> votes;
    private volatile boolean isAccepted;
    private final LocalDateTime createdAt;

    public Answer(String content, User author, Question question) {
        this.id = UUID.randomUUID().toString().substring(0, 8);
        this.content = content;
        this.author = author;
        this.question = question;
        this.comments = Collections.synchronizedList(new ArrayList<>());
        this.votes = Collections.synchronizedList(new ArrayList<>());
        this.isAccepted = false;
        this.createdAt = LocalDateTime.now();
    }

    public String getId() { return id; }
    public String getContent() { return content; }
    @Override
    public User getAuthor() { return author; }
    public Question getQuestion() { return question; }
    public boolean isAccepted() { return isAccepted; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    public synchronized void accept() {
        this.isAccepted = true;
    }

    public synchronized void setAccepted(boolean accepted) {
        this.isAccepted = accepted;
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
        votes.removeIf(v -> v.getVoter().getId().equals(voter.getId()));
        votes.add(new Vote(voter, voteType));
    }

    @Override
    public int getVoteCount() {
        return votes.stream().mapToInt(Vote::getValue).sum();
    }

    @Override
    public String toString() {
        String accepted = isAccepted ? " ✓" : "";
        return String.format("A[%s]%s: %s... (by %s, votes: %d)", 
            id, accepted,
            content.length() > 40 ? content.substring(0, 40) : content,
            author.getUsername(), getVoteCount());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Answer answer = (Answer) o;
        return id.equals(answer.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

