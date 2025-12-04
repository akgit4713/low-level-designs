package stackoverflow.strategies;

import stackoverflow.enums.VoteType;

/**
 * Strategy interface for calculating reputation changes.
 * Allows different reputation schemes to be plugged in.
 */
public interface ReputationStrategy {
    
    /**
     * Calculate reputation change for question author when their question is voted.
     */
    int calculateQuestionVoteReputation(VoteType voteType);
    
    /**
     * Calculate reputation change for answer author when their answer is voted.
     */
    int calculateAnswerVoteReputation(VoteType voteType);
    
    /**
     * Calculate reputation change for answer author when their answer is accepted.
     */
    int calculateAnswerAcceptedReputation();
    
    /**
     * Calculate reputation change for voter when they downvote.
     */
    int calculateDownvotePenalty();
    
    /**
     * Get the name of this strategy.
     */
    String getName();
}



