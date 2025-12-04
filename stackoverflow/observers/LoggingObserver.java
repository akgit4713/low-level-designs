package stackoverflow.observers;

import stackoverflow.models.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Observer that logs all system events.
 */
public class LoggingObserver implements StackOverflowObserver {
    
    private static final DateTimeFormatter FORMATTER = 
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final boolean verbose;

    public LoggingObserver() {
        this(false);
    }

    public LoggingObserver(boolean verbose) {
        this.verbose = verbose;
    }

    private String timestamp() {
        return "[" + LocalDateTime.now().format(FORMATTER) + "]";
    }

    @Override
    public void onQuestionPosted(Question question) {
        if (verbose) {
            System.out.println(timestamp() + " [LOG] QUESTION_POSTED: id=" + question.getId() + 
                ", author=" + question.getAuthor().getUsername() + 
                ", title=\"" + question.getTitle() + "\"");
        }
    }

    @Override
    public void onAnswerPosted(Answer answer) {
        if (verbose) {
            System.out.println(timestamp() + " [LOG] ANSWER_POSTED: id=" + answer.getId() + 
                ", author=" + answer.getAuthor().getUsername() + 
                ", questionId=" + answer.getQuestion().getId());
        }
    }

    @Override
    public void onCommentAdded(Comment comment, Object target) {
        if (verbose) {
            String targetId = target instanceof Question 
                ? ((Question) target).getId() 
                : ((Answer) target).getId();
            System.out.println(timestamp() + " [LOG] COMMENT_ADDED: author=" + 
                comment.getAuthor().getUsername() + ", targetId=" + targetId);
        }
    }

    @Override
    public void onQuestionVoted(Question question, User voter) {
        if (verbose) {
            System.out.println(timestamp() + " [LOG] QUESTION_VOTED: questionId=" + 
                question.getId() + ", voter=" + voter.getUsername());
        }
    }

    @Override
    public void onAnswerVoted(Answer answer, User voter) {
        if (verbose) {
            System.out.println(timestamp() + " [LOG] ANSWER_VOTED: answerId=" + 
                answer.getId() + ", voter=" + voter.getUsername());
        }
    }

    @Override
    public void onAnswerAccepted(Answer answer) {
        if (verbose) {
            System.out.println(timestamp() + " [LOG] ANSWER_ACCEPTED: answerId=" + 
                answer.getId() + ", questionId=" + answer.getQuestion().getId());
        }
    }

    @Override
    public void onReputationChanged(User user, int oldReputation, int newReputation) {
        if (verbose) {
            System.out.println(timestamp() + " [LOG] REPUTATION_CHANGED: user=" + 
                user.getUsername() + ", change=" + (newReputation - oldReputation));
        }
    }

    @Override
    public void onUserRegistered(User user) {
        if (verbose) {
            System.out.println(timestamp() + " [LOG] USER_REGISTERED: id=" + 
                user.getId() + ", username=" + user.getUsername());
        }
    }
}



