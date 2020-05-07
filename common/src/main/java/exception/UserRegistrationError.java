package exception;

public class UserRegistrationError extends RuntimeException {

    public UserRegistrationError() {
        super("Failed to register new user.");
    }
}
