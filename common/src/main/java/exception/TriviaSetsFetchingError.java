package exception;

public class TriviaSetsFetchingError extends RuntimeException {
    public TriviaSetsFetchingError() {
        super("Failed to fetch user's trivia sets from database");
    }
}
