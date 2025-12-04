package stackoverflow.models;

import stackoverflow.enums.VoteType;

/**
 * Interface for items that can be voted on.
 */
public interface Votable {
    void vote(User voter, VoteType voteType);
    int getVoteCount();
    User getAuthor();
}



