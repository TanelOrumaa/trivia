package database;

import Exceptions.IncorrectLoginInformationException;
import general.User;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UsersDatabaseLayer {

    /**
     * Validates if the provided username exists in the database and if the password matches with it.
     *
     * @param username String for the username.
     * @param password String for the unhashed password.
     * @throws IncorrectLoginInformationException If the username doesn't exist in the database or if the password doesn't match.
     * @return User object if user is valid.
     */
    public static User validateUser(DatabaseConnection dbConnection, String username, String password) throws IncorrectLoginInformationException {
        // Check if user exists.
        int id = getUserIdFromDatabase(dbConnection, username);
        if (id != -1) {
            // Check if passwords match.
            if (isPasswordValidForUser(dbConnection, id, password)) {
                try {
                    ResultSet resultSet = dbConnection.runSelectQuery(String.format("SELECT u.username, u.first_name, u.last_name FROM users u WHERE u.id = '%d';", id));
                    return new User(id, resultSet.getString(1), resultSet.getString(2), resultSet.getString(3));
                } catch (SQLException e) {
                    throw new RuntimeException("Unable to fetch user data.", e);
                }
            }
        }
        throw new IncorrectLoginInformationException();
    }

    /**
     * Finds corresponding user id to the provided username from database and returns it.
     *
     * @param dbConnection - DatabaseConnection object.
     * @param username     - String username for which we are fetching the id for.
     * @return Corresponding id from the db or -1 in case SQL statement fails, there's more than one result or the username doesn't exist.
     */
    public static int getUserIdFromDatabase(DatabaseConnection dbConnection, String username) {
        try {
            // Get the results from the database.
            ResultSet resultSet = dbConnection.runSelectQuery(String.format("SELECT u.id FROM users u WHERE u.username = '%s';", username));
            // If there's at least one result, get the id.
            if (resultSet.next()) {
                int id = resultSet.getInt("id");
                // If there's another row, we've somehow fetched too many (data invalid) so we return -1.
                if (resultSet.next()) {
                    return -1;
                }

                // Otherwise return the id.
                return id;
            } else {
                // In case of no results return -1.
                return -1;
            }

        } catch (SQLException e) {
            return -1;
        }
    }

    /**
     * Validates if the password argument matches with the one user actually has.
     * @param dbConnection - DatabaseConnection object.
     * @param user_id - Integer for the user id, should be validated to exist before this method is called.
     * @param password - String for the password, should be unhashed.
     * @return true if the password matches with the one in the database and false otherwise.
     */
    public static boolean isPasswordValidForUser(DatabaseConnection dbConnection, int user_id, String password) {
        if (user_id != -1) {

            try {
                // Get salt value from db.
                ResultSet saltResult = dbConnection.runSelectQuery(String.format("SELECT u.salt FROM users u WHERE u.id = %d;", user_id));
                if (saltResult.next()) {
                    String salt = saltResult.getString("salt");

                    // Use salt to generate hash.
                    String hash = hashPassword(password, salt);

                    // Compare generated hash against user password hash in db.
                    ResultSet passwordMatchResult = dbConnection.runSelectQuery(String.format("SELECT '%s'=u.password AS valid_password FROM users u WHERE u.id = %d;", hash, user_id));
                    if (passwordMatchResult.next()) {
                        return passwordMatchResult.getBoolean("valid_password");
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }

    /**
     * Generates a hash for input password and salt.
     * @param password String for unhashed password.
     * @param salt String for the salt.
     * @return hashed password.
     */
    private static String hashPassword(String password, String salt) {
        // TODO: Implement hashing.
        return password;
    }
}
