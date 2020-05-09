package database;

import configuration.Configuration;
import exception.DatabaseConnectionInactiveError;
import question.Answer;
import question.Question;
import triviaset.TriviaSet;

import java.sql.*;

public class DatabaseConnection {

    private Connection databaseConnection;

    public DatabaseConnection() throws SQLException {
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
     * @return ResultSet object with resulting data.
     * @throws SQLException                    If SQL query fails
     * @throws DatabaseConnectionInactiveError If this database connection is no longer active
     */
    public PreparedStatement userIdByUsernameStatement(String username) throws SQLException, DatabaseConnectionInactiveError {
        if (this.isActive()) {
            PreparedStatement ps = databaseConnection.prepareStatement("SELECT u.id FROM users u WHERE username = ?;");
            ps.setString(1, username);
            return ps;

        } else throw new DatabaseConnectionInactiveError();

    }


    public PreparedStatement saltByUserIdStatement(int userId) throws SQLException, DatabaseConnectionInactiveError {
        if (this.isActive()) {
            PreparedStatement ps = databaseConnection.prepareStatement("SELECT u.salt FROM users u WHERE u.id = ?;");
            ps.setString(1, Integer.toString(userId));
            return ps;
        } else throw new DatabaseConnectionInactiveError();

    }


    public PreparedStatement passwordByUserIdStatement(int userId) throws SQLException, DatabaseConnectionInactiveError {
        if (this.isActive()) {
            PreparedStatement ps = databaseConnection.prepareStatement("SELECT u.password FROM users u WHERE u.id = ?;");
            ps.setString(1, Integer.toString(userId));
            return ps;
        } else throw new DatabaseConnectionInactiveError();

    }


    public PreparedStatement userInfoByUserIdStatement(int userId) throws SQLException, DatabaseConnectionInactiveError {
        if (this.isActive()) {
            PreparedStatement ps = databaseConnection.prepareStatement("SELECT u.username, u.nickname FROM users u WHERE u.id = ?;");
            ps.setString(1, Integer.toString(userId));
            return ps;
        } else throw new DatabaseConnectionInactiveError();

    }


    /**
     * Adds new user to database
     *
     * @throws SQLException                    If SQL query fails
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
        } else throw new DatabaseConnectionInactiveError();

    }


    public PreparedStatement registerQuestionStatement(Question question, int triviaSetId) throws SQLException, DatabaseConnectionInactiveError {

        if (this.isActive()) {

            String questionType = "";
            String answerType = "";
            String mediaPath = null;

            switch (question.getQuestionType()) {

                case TEXT:
                    questionType = "text";
                    break;
                case IMAGE:
                    questionType = "image";
                    mediaPath = question.getMediaPath();
                    break;
                case AUDIO:
                    questionType = "audio";
                    mediaPath = question.getMediaPath();
                    break;
                case VIDEO:
                    questionType = "video";
                    mediaPath = question.getMediaPath();
                    break;

            }

            switch (question.getAnswerType()) {

                case CHOICE:
                    answerType = "choice";
                    break;
                case FREEFORM:
                    answerType = "freeform";
                    break;

            }

            PreparedStatement ps = this.databaseConnection.prepareStatement(
                    "INSERT INTO questions(question_type, answer_type, score_degradation, potential_points, time, question_text, triviaset_id, media_path)" +
                            " VALUES (?, ?, ?, ?, ?, ?, ?, ?);");
            ps.setString(1, questionType);
            ps.setString(2, answerType);
            ps.setBoolean(3, question.isScoreDegradation());
            ps.setInt(4, question.getPotentialPoints());
            ps.setInt(5, question.getTime());
            ps.setString(6, question.getQuestion());
            ps.setInt(7, triviaSetId);
            ps.setString(8, mediaPath);
            return ps;

        } else throw new DatabaseConnectionInactiveError();


    }


    public PreparedStatement lastQuestionIdStatement() throws SQLException, DatabaseConnectionInactiveError {

        if (this.isActive()) {

            return databaseConnection.prepareStatement("SELECT max(id) as id FROM questions");

        } else throw new DatabaseConnectionInactiveError();

    }


    public PreparedStatement lastTriviaSetIdStatement() throws SQLException, DatabaseConnectionInactiveError {

        if (this.isActive()) {

            return databaseConnection.prepareStatement("SELECT max(id) as id FROM triviasets");

        } else throw new DatabaseConnectionInactiveError();

    }


    public PreparedStatement registerAnswerStatement(Answer answer, long questionId) throws SQLException, DatabaseConnectionInactiveError {

        if (this.isActive()) {

            PreparedStatement ps = this.databaseConnection.prepareStatement(
                    "INSERT INTO answers(question_id, answer_text, is_correct) VALUES (?, ?, ?);");
            ps.setLong(1, questionId);
            ps.setString(2, answer.getAnswerText());
            ps.setBoolean(3, answer.isCorrect());
            return ps;

        } else throw new DatabaseConnectionInactiveError();

    }


    public PreparedStatement registerTriviaSetStatement(TriviaSet triviaSet, long userId) throws SQLException, DatabaseConnectionInactiveError {

        if (this.isActive()) {

            PreparedStatement ps = this.databaseConnection.prepareStatement("INSERT INTO triviasets(name, questions_count, user_id)" +
                    " VALUES (?, ?, ?);");
            ps.setString(1, triviaSet.getName());
            ps.setInt(2, triviaSet.getNumberOfQuestions());
            ps.setLong(3, userId);
            return ps;

        } else throw new DatabaseConnectionInactiveError();


    }

    public PreparedStatement usersTriviaSetsStatement(long userId) throws SQLException, DatabaseConnectionInactiveError {
        if (this.isActive()){
            PreparedStatement ps = this.databaseConnection.prepareStatement("SELECT id, name, questions_count FROM triviasets WHERE user_id = ?;");
            ps.setLong(1, userId);
            return ps;
        } else throw new DatabaseConnectionInactiveError();

    }

}