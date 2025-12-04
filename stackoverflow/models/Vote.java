package stackoverflow.models;

import stackoverflow.enums.VoteType;

/**
 * Represents a vote on a question or answer.
 */
public class Vote {
    private final User voter;
    private final VoteType voteType;

    public Vote(User voter, VoteType voteType) {
        this.voter = voter;
        this.voteType = voteType;
    }

    public User getVoter() { return voter; }
    public VoteType getVoteType() { return voteType; }

    public int getValue() {
        return voteType.getValue();
    }
}



