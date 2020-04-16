package database;

import configuration.Config;

import java.sql.*;

public class DatabaseConnection {

    private Connection databaseConnection;

    public DatabaseConnection() throws SQLException{
        this.databaseConnection = createDatabaseConnection();
    }

    // Creates a database connection object which can later be used to run queries.
    private Connection createDatabaseConnection() throws SQLException {
        // Return the database connection.
        return DriverManager.getConnection(Config.DB_CONNECT_STRING, Config.DB_USERNAME, Config.DB_PASSWORD);
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
            return this.databaseConnection.isValid(Config.DB_CONNECTION_TIMEOUT_SECONDS);
        } catch (SQLException e) {
            return false;
        }
    }

    /**
     * Runs input query with this database connection object.
     * @param query An SQL query with correct syntax.
     * @return ResultSet object with resulting data.
     * @throws SQLException If the SQL query is invalid or there's a problem with database connection.
     */
    public ResultSet runSelectQuery(String query) throws SQLException {
        if (this.isActive()) {
            return this.databaseConnection.createStatement().executeQuery(query);
        } else {
            // TODO: Should we create a new connection?
            return null;
        }
    }

    /**
     * Runs input query with this database connection object.
     * @param query An SQL query with correct syntax.
     * @return 0 if nothing was done and a number indicating the number of updated/inserted/deleted rows otherwise.
     * @throws SQLException If the SQL query is invalid or there's a problem with database connection.
     */
    public int runQuery(String query) throws SQLException {
        if (this.isActive()) {
            return this.databaseConnection.createStatement().executeUpdate(query);
        } else {
            // TODO: Should we create a new connection?
            return -1;
        }
    }
}