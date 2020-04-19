package exceptions;

public class UserAlreadyExistsError extends RuntimeException {

    public UserAlreadyExistsError(String username) {
        super("Username " + username + " already exists in database.");
    }
}
