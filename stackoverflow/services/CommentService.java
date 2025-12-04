package stackoverflow.services;

import stackoverflow.models.Answer;
import stackoverflow.models.Comment;
import stackoverflow.models.Commentable;
import stackoverflow.models.Question;
import stackoverflow.models.User;

/**
 * Service for comment operations.
 */
public class CommentService {

    public Comment addCommentToQuestion(User author, Question question, String content) {
        Comment comment = new Comment(content, author);
        question.addComment(comment);
        return comment;
    }

    public Comment addCommentToAnswer(User author, Answer answer, String content) {
        Comment comment = new Comment(content, author);
        answer.addComment(comment);
        return comment;
    }

    public Comment addComment(User author, Commentable target, String content) {
        Comment comment = new Comment(content, author);
        target.addComment(comment);
        return comment;
    }
}



