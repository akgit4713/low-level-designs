package stackoverflow.services;

import stackoverflow.enums.VoteType;
import stackoverflow.exceptions.VotingException;
import stackoverflow.models.Answer;
import stackoverflow.models.Question;
import stackoverflow.models.User;
import stackoverflow.models.Votable;
import stackoverflow.strategies.ReputationStrategy;

/**
 * Service for voting operations with reputation management.
 */
public class VoteService {
    private final ReputationStrategy reputationStrategy;

    public VoteService(ReputationStrategy reputationStrategy) {
        this.reputationStrategy = reputationStrategy;
    }

    public void voteOnQuestion(User voter, Question question, VoteType voteType) {
        validateVote(voter, question);
        
        // Apply vote
        question.vote(voter, voteType);
        
        // Update author's reputation
        int reputationChange = reputationStrategy.calculateQuestionVoteReputation(voteType);
        question.getAuthor().updateReputation(reputationChange);
        
        // Penalize downvoter
        if (voteType == VoteType.DOWNVOTE) {
            voter.updateReputation(reputationStrategy.calculateDownvotePenalty());
        }
    }

    public void voteOnAnswer(User voter, Answer answer, VoteType voteType) {
        validateVote(voter, answer);
        
        // Apply vote
        answer.vote(voter, voteType);
        
        // Update author's reputation
        int reputationChange = reputationStrategy.calculateAnswerVoteReputation(voteType);
        answer.getAuthor().updateReputation(reputationChange);
        
        // Penalize downvoter
        if (voteType == VoteType.DOWNVOTE) {
            voter.updateReputation(reputationStrategy.calculateDownvotePenalty());
        }
    }

    public void applyAcceptedAnswerReputation(Answer answer) {
        int bonus = reputationStrategy.calculateAnswerAcceptedReputation();
        answer.getAuthor().updateReputation(bonus);
    }

    private void validateVote(User voter, Votable votable) {
        if (voter.getId().equals(votable.getAuthor().getId())) {
            throw new VotingException("Cannot vote on your own content");
        }
    }

    public ReputationStrategy getReputationStrategy() {
        return reputationStrategy;
    }
}



