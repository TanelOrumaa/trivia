package database;

import exception.DatabaseConnectionInactiveError;
import exception.IncorrectLoginInformationException;
import exception.UserAlreadyExistsError;
import exception.UserRegistrationError;
import user.User;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.sql.PreparedStatement;
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
                try (ResultSet resultSet = dbConnection.userInfoByUserIdStatement(id).executeQuery()) {
                    if (resultSet.next()){
                        String nickname = resultSet.getString("nickname");

                        return new User(id, username, nickname);

                    } else {
                        throw new IncorrectLoginInformationException();
                    }

                } catch (SQLException e) {
                    // TODO: Something wrong here with exception catching, as this error is never thrown.
                    System.out.println("Unable to fetch user data.");
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
        // Get the results from the database.
        try (ResultSet resultSet = dbConnection.userIdByUsernameStatement(username).executeQuery()) {

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

        } catch (SQLException | DatabaseConnectionInactiveError e) {
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

            // Get salt value from db.
            try (ResultSet saltResult = dbConnection.saltByUserIdStatement(user_id).executeQuery()) {

                if (saltResult.next()) {
                    String salt = saltResult.getString("salt");

                    try {
                        // Use salt to generate hash.
                        String hashedPassword = hashPassword(password, salt);

                        // Compare generated hash against user password hash in db.
                        try (ResultSet passwordResult = dbConnection.passwordByUserIdStatement(user_id).executeQuery()) {
                            if (passwordResult.next()) {
                                String correctPassword = passwordResult.getString("password");

                                // If there's another row, we've somehow fetched too many (data invalid) so we return false
                                if (passwordResult.next()) {
                                    return false;
                                }

                                // Otherwise check if passwords match
                                return hashedPassword.equals(correctPassword);

                            } else {
                                // In case of no results return false
                                return false;
                            }

                        } catch (SQLException e){
                            throw new RuntimeException("SQL query failed!");
                        }

                    } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
                        throw new RuntimeException("Hashing password failed!", e);
                    }


                }
            } catch (SQLException e) {
                throw new RuntimeException("SQL query failed!");
            } catch (DatabaseConnectionInactiveError e){
                throw new RuntimeException("Database connection is inactive!");
            }
        }
        return false;
    }


    public static void registerUser(DatabaseConnection dbConnection, String username, String password, String nickname) throws UserRegistrationError, UserAlreadyExistsError {
        // Check if user exists
        int id = getUserIdFromDatabase(dbConnection, username);
        if (id == -1){ // No such username in database
            // Generate new salt for registering this user
            String salt = generateSalt();

            try {
                String hashedPassword = hashPassword(password, salt);

                // Add new user to database
                try (PreparedStatement ps = dbConnection.registerUserStatement(username, hashedPassword, salt, nickname)){
                    ps.executeUpdate();

                } catch (SQLException e) {
                    throw new UserRegistrationError();
                }

            } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
                throw new RuntimeException("Hashing password failed!");
            }


        } else {
            // User with this username already exists in database
            throw new UserAlreadyExistsError(username);
        }
    }


    /**
     * Generates a hash for input password and salt.
     * @param password String for unhashed password.
     * @param salt String for the salt.
     * @return hashed password.
     */
    private static String hashPassword(String password, String salt) throws NoSuchAlgorithmException, InvalidKeySpecException {
        int iterationCount = 1000000;
        int outputLength = 256;
        byte[] saltBytes = hexStringToByteArray(salt);
        PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), saltBytes, iterationCount, outputLength);
        byte[] hashedBytes = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).getEncoded();

        return byteArrayToString(hashedBytes);
    }

    private static String generateSalt(){
        SecureRandom rng = new SecureRandom();
        byte[] saltBytes = new byte[16];
        rng.nextBytes(saltBytes);

        return byteArrayToString(saltBytes);
    }

    public static String byteArrayToString(byte[] bytes) {
        BigInteger bigBoi = new BigInteger(1, bytes);
        return String.format("%0" + (bytes.length << 1) + "x", bigBoi);
    }

    public static byte[] hexStringToByteArray(String hexString) {
        byte[] byteArray = new BigInteger(hexString, 16).toByteArray();
        //toByteArray produces an additional sign bit or something, so we must remove it
        if (byteArray[0] == 0) {

            byte[] output = new byte[byteArray.length - 1];
            System.arraycopy(byteArray, 1, output, 0, output.length);
            return output;
        }
        return byteArray;
    }
}
