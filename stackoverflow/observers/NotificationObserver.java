package stackoverflow.observers;

import stackoverflow.models.*;

/**
 * Observer that handles user notifications.
 */
public class NotificationObserver implements StackOverflowObserver {

    @Override
    public void onQuestionPosted(Question question) {
        System.out.println("📢 [Notification] New question posted: " + question.getTitle());
    }

    @Override
    public void onAnswerPosted(Answer answer) {
        User questionAuthor = answer.getQuestion().getAuthor();
        System.out.println("📢 [Notification] " + questionAuthor.getUsername() + 
            " - Your question received a new answer from " + answer.getAuthor().getUsername());
    }

    @Override
    public void onCommentAdded(Comment comment, Object target) {
        String targetType = target instanceof Question ? "question" : "answer";
        System.out.println("💬 [Notification] New comment on your " + targetType + 
            " by " + comment.getAuthor().getUsername());
    }

    @Override
    public void onQuestionVoted(Question question, User voter) {
        System.out.println("👍 [Notification] " + question.getAuthor().getUsername() + 
            " - Your question received a vote!");
    }

    @Override
    public void onAnswerVoted(Answer answer, User voter) {
        System.out.println("👍 [Notification] " + answer.getAuthor().getUsername() + 
            " - Your answer received a vote!");
    }

    @Override
    public void onAnswerAccepted(Answer answer) {
        System.out.println("✅ [Notification] " + answer.getAuthor().getUsername() + 
            " - Your answer was accepted! +15 reputation");
    }

    @Override
    public void onReputationChanged(User user, int oldReputation, int newReputation) {
        int change = newReputation - oldReputation;
        String emoji = change > 0 ? "📈" : "📉";
        System.out.println(emoji + " [Reputation] " + user.getUsername() + 
            ": " + oldReputation + " → " + newReputation + " (" + (change > 0 ? "+" : "") + change + ")");
    }

    @Override
    public void onUserRegistered(User user) {
        System.out.println("🎉 [Welcome] New user registered: " + user.getUsername());
    }
}



