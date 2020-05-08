package exception;

public class TriviaSetRegistrationError extends RuntimeException {

    public TriviaSetRegistrationError() {
        super("Failed to register new Triviaset.");
    }

}
