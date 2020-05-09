package exception;

public class QuestionsFetchingError extends RuntimeException {
    public QuestionsFetchingError() {
        super("Failed to fetch questions from database!");
    }
}
