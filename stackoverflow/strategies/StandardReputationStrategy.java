package stackoverflow.strategies;

import stackoverflow.enums.VoteType;

/**
 * Standard Stack Overflow reputation rules.
 * - Question upvote: +5
 * - Question downvote: -2
 * - Answer upvote: +10
 * - Answer downvote: -2
 * - Answer accepted: +15
 * - Downvoting others: -1
 */
public class StandardReputationStrategy implements ReputationStrategy {

    @Override
    public int calculateQuestionVoteReputation(VoteType voteType) {
        return voteType == VoteType.UPVOTE ? 5 : -2;
    }

    @Override
    public int calculateAnswerVoteReputation(VoteType voteType) {
        return voteType == VoteType.UPVOTE ? 10 : -2;
    }

    @Override
    public int calculateAnswerAcceptedReputation() {
        return 15;
    }

    @Override
    public int calculateDownvotePenalty() {
        return -1;
    }

    @Override
    public String getName() {
        return "Standard";
    }
}



