package exception;

public class DatabaseConnectionInactiveError extends RuntimeException {

    public DatabaseConnectionInactiveError() {
        super("Database connection inactive.");
    }
}
