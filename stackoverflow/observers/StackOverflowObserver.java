package stackoverflow.observers;

import stackoverflow.models.*;

/**
 * Observer interface for Stack Overflow system events.
 * Allows for loose coupling of notification/analytics systems.
 */
public interface StackOverflowObserver {
    
    default void onQuestionPosted(Question question) {}
    
    default void onAnswerPosted(Answer answer) {}
    
    default void onCommentAdded(Comment comment, Object target) {}
    
    default void onQuestionVoted(Question question, User voter) {}
    
    default void onAnswerVoted(Answer answer, User voter) {}
    
    default void onAnswerAccepted(Answer answer) {}
    
    default void onReputationChanged(User user, int oldReputation, int newReputation) {}
    
    default void onUserRegistered(User user) {}
}



