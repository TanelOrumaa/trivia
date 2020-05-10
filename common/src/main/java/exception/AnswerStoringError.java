package exception;

public class AnswerStoringError extends RuntimeException {
    public AnswerStoringError() {
        super("Failed to store user's answer!");
    }
}
