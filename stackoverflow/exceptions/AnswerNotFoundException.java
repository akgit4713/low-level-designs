package stackoverflow.exceptions;

/**
 * Exception thrown when an answer is not found.
 */
public class AnswerNotFoundException extends StackOverflowException {
    public AnswerNotFoundException(String answerId) {
        super("Answer not found: " + answerId);
    }
}



