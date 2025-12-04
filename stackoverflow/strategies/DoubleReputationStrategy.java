package stackoverflow.strategies;

import stackoverflow.enums.VoteType;

/**
 * Double reputation strategy for special events.
 * All reputation gains are doubled.
 */
public class DoubleReputationStrategy implements ReputationStrategy {

    @Override
    public int calculateQuestionVoteReputation(VoteType voteType) {
        return voteType == VoteType.UPVOTE ? 10 : -2;
    }

    @Override
    public int calculateAnswerVoteReputation(VoteType voteType) {
        return voteType == VoteType.UPVOTE ? 20 : -2;
    }

    @Override
    public int calculateAnswerAcceptedReputation() {
        return 30;
    }

    @Override
    public int calculateDownvotePenalty() {
        return -1;
    }

    @Override
    public String getName() {
        return "Double Reputation Event";
    }
}



