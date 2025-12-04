package stackoverflow.observers;

import stackoverflow.models.*;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Observer that tracks system statistics.
 */
public class StatisticsObserver implements StackOverflowObserver {
    
    private final AtomicInteger totalQuestions = new AtomicInteger(0);
    private final AtomicInteger totalAnswers = new AtomicInteger(0);
    private final AtomicInteger totalComments = new AtomicInteger(0);
    private final AtomicInteger totalVotes = new AtomicInteger(0);
    private final AtomicInteger totalUsers = new AtomicInteger(0);
    private final AtomicInteger acceptedAnswers = new AtomicInteger(0);

    @Override
    public void onQuestionPosted(Question question) {
        totalQuestions.incrementAndGet();
    }

    @Override
    public void onAnswerPosted(Answer answer) {
        totalAnswers.incrementAndGet();
    }

    @Override
    public void onCommentAdded(Comment comment, Object target) {
        totalComments.incrementAndGet();
    }

    @Override
    public void onQuestionVoted(Question question, User voter) {
        totalVotes.incrementAndGet();
    }

    @Override
    public void onAnswerVoted(Answer answer, User voter) {
        totalVotes.incrementAndGet();
    }

    @Override
    public void onAnswerAccepted(Answer answer) {
        acceptedAnswers.incrementAndGet();
    }

    @Override
    public void onUserRegistered(User user) {
        totalUsers.incrementAndGet();
    }

    public void printStatistics() {
        System.out.println("\n═══════════════════════════════════════");
        System.out.println("        SYSTEM STATISTICS");
        System.out.println("═══════════════════════════════════════");
        System.out.println("Total Users:           " + totalUsers.get());
        System.out.println("Total Questions:       " + totalQuestions.get());
        System.out.println("Total Answers:         " + totalAnswers.get());
        System.out.println("Total Comments:        " + totalComments.get());
        System.out.println("Total Votes:           " + totalVotes.get());
        System.out.println("Accepted Answers:      " + acceptedAnswers.get());
        System.out.println("═══════════════════════════════════════\n");
    }

    // Getters for testing
    public int getTotalQuestions() { return totalQuestions.get(); }
    public int getTotalAnswers() { return totalAnswers.get(); }
    public int getTotalComments() { return totalComments.get(); }
    public int getTotalVotes() { return totalVotes.get(); }
    public int getTotalUsers() { return totalUsers.get(); }
    public int getAcceptedAnswers() { return acceptedAnswers.get(); }
}



