package database;

import exception.QuestionRegistrationError;
import exception.TriviaSetRegistrationError;
import triviaset.TriviaSet;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TriviaSetsDatabaseLayer {

    public static int registerTriviaSet(DatabaseConnection dbConnection, TriviaSet triviaset, long userId) {

        try (PreparedStatement ps = dbConnection.registerTriviaSetStatement(triviaset, userId)) {

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new TriviaSetRegistrationError();
        }

        int triviaSetId = -1;

        try (ResultSet triviaSetIdResult = dbConnection.lastTriviaSetIdStatement().executeQuery()) {

            if (triviaSetIdResult.next()) {

                triviaSetId = triviaSetIdResult.getInt("id");

            }

        } catch (SQLException e){
            throw new RuntimeException("SQL query for getting triviaSetId failed!");
        }

        return triviaSetId;

    }

}
