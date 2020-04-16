package Exceptions;

public class IncorrectLoginInformationException extends RuntimeException {

    public IncorrectLoginInformationException() {
        super("Incorrect username and/or password.");
    }
}
