package database;

import configuration.Configuration;
import exception.DatabaseConnectionInactiveError;

import java.sql.*;

public class DatabaseConnection {

    private Connection databaseConnection;

    public DatabaseConnection() throws SQLException{
        this.databaseConnection = createDatabaseConnection();
    }

    // Creates a database connection object which can later be used to run queries.
    private Connection createDatabaseConnection() throws SQLException {
        // Return the database connection.
        return DriverManager.getConnection(Configuration.DB_CONNECT_STRING, Configuration.DB_USERNAME, Configuration.DB_PASSWORD);
    }

    // Close this connection.
    public void closeDatabaseConnection() throws SQLException {
        try {
            this.databaseConnection.close();
        } catch (SQLException e) {
            if (!databaseConnection.isClosed()) {
                throw new RuntimeException("Couldn't close database", e);
            }
        }
    }

    public boolean isActive() {
        try {
            return this.databaseConnection.isValid(Configuration.DB_CONNECTION_TIMEOUT_SECONDS);
        } catch (SQLException e) {
            return false;
        }
    }


    /**
     *
     * @return ResultSet object with resulting data.
     * @throws SQLException If SQL query fails
     * @throws DatabaseConnectionInactiveError If this database connection is no longer active
     */
    public PreparedStatement userIdByUsernameStatement(String username) throws SQLException, DatabaseConnectionInactiveError {
        if (this.isActive()) {
            PreparedStatement ps = databaseConnection.prepareStatement("SELECT u.id FROM users u WHERE username = ?;");
            ps.setString(1, username);
            return ps;

        } else {
            throw new DatabaseConnectionInactiveError();
        }
    }


    public PreparedStatement saltByUserIdStatement(int userId) throws SQLException, DatabaseConnectionInactiveError {
        if (this.isActive()) {
            PreparedStatement ps = databaseConnection.prepareStatement("SELECT u.salt FROM users u WHERE u.id = ?;");
            ps.setString(1, Integer.toString(userId));
            return ps;
        } else {
            throw new DatabaseConnectionInactiveError();
        }
    }


    public PreparedStatement passwordByUserIdStatement(int userId) throws SQLException, DatabaseConnectionInactiveError {
        if (this.isActive()) {
            PreparedStatement ps = databaseConnection.prepareStatement("SELECT u.password FROM users u WHERE u.id = ?;");
            ps.setString(1, Integer.toString(userId));
            return ps;
        } else {
            throw new DatabaseConnectionInactiveError();
        }
    }


    public PreparedStatement userInfoByUserIdStatement(int userId) throws SQLException, DatabaseConnectionInactiveError {
        if (this.isActive()) {
            PreparedStatement ps = databaseConnection.prepareStatement("SELECT u.username, u.nickname FROM users u WHERE u.id = ?;");
            ps.setString(1, Integer.toString(userId));
            return ps;
        } else {
            throw new DatabaseConnectionInactiveError();
        }
    }


    /**
     * Adds new user to database
     * @throws SQLException If SQL query fails
     * @throws DatabaseConnectionInactiveError If this database connection is no longer active
     */
    public PreparedStatement registerUserStatement(String username, String password, String salt, String nickname) throws SQLException, DatabaseConnectionInactiveError {
        if (this.isActive()) {
            PreparedStatement ps = this.databaseConnection.prepareStatement("INSERT INTO users(username, password, salt, nickname) VALUES (?, ?, ?, ?);");
            ps.setString(1, username);
            ps.setString(2, password);
            ps.setString(3, salt);
            ps.setString(4, nickname);
            return ps;
        } else {
            throw new DatabaseConnectionInactiveError();
        }
    }
}