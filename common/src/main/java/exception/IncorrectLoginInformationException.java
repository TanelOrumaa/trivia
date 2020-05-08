package exception;

public class IncorrectLoginInformationException extends RuntimeException {

    public IncorrectLoginInformationException() {
        super("Incorrect username and/or password.");
    }
}
