package stackoverflow.strategies;

import stackoverflow.enums.VoteType;

/**
 * Boosted reputation strategy for new users.
 * Designed to encourage new contributors.
 */
public class NewUserBoostStrategy implements ReputationStrategy {

    @Override
    public int calculateQuestionVoteReputation(VoteType voteType) {
        return voteType == VoteType.UPVOTE ? 8 : -1; // Less penalty
    }

    @Override
    public int calculateAnswerVoteReputation(VoteType voteType) {
        return voteType == VoteType.UPVOTE ? 15 : -1; // Less penalty
    }

    @Override
    public int calculateAnswerAcceptedReputation() {
        return 20;
    }

    @Override
    public int calculateDownvotePenalty() {
        return 0; // No penalty for new users downvoting
    }

    @Override
    public String getName() {
        return "New User Boost";
    }
}



