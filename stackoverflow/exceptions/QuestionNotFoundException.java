package stackoverflow.exceptions;

/**
 * Exception thrown when a question is not found.
 */
public class QuestionNotFoundException extends StackOverflowException {
    public QuestionNotFoundException(String questionId) {
        super("Question not found: " + questionId);
    }
}



