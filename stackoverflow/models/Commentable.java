package stackoverflow.models;

import java.util.List;

/**
 * Interface for items that can have comments.
 */
public interface Commentable {
    void addComment(Comment comment);
    List<Comment> getComments();
}



