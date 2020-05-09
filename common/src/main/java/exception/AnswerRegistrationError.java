package exception;

public class AnswerRegistrationError extends RuntimeException {

    public AnswerRegistrationError() {
        super("Failed to register new answer.");
    }

}
