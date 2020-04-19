package exceptions;

public class UserRegistrationError extends RuntimeException {

    public UserRegistrationError() {
        super("Failed to register new user.");
    }
}
