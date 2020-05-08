package exception;

public class QuestionRegistrationError extends RuntimeException {

    public QuestionRegistrationError() {
        super("Failed to register new question.");
    }

}
