package database;

import exception.QuestionRegistrationError;
import exception.TriviaSetRegistrationError;
import triviaset.TriviaSet;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TriviaSetsDatabaseLayer {

    public static int registerTriviaSet(DatabaseConnection dbConnection, TriviaSet triviaset, long userId) {

        int triviaSetId = -1;

        try (PreparedStatement ps = dbConnection.registerTriviaSetStatement(triviaset, userId)) {

            ps.executeUpdate();
            try (ResultSet triviaSetIdResult = dbConnection.lastTriviaSetIdStatement().executeQuery()) {

                if (triviaSetIdResult.next()) {

                    triviaSetId = triviaSetIdResult.getInt("id");

                }

            } catch (SQLException e){
                throw new RuntimeException("SQL query for getting triviaSetId failed!", e);
            }

        } catch (SQLException e) {
            throw new TriviaSetRegistrationError();
        }

        /*try (ResultSet triviaSetIdResult = dbConnection.lastTriviaSetIdStatement().executeQuery()) {

            if (triviaSetIdResult.next()) {

                triviaSetId = triviaSetIdResult.getInt("id");

            }

        } catch (SQLException e){
            throw new RuntimeException("SQL query for getting triviaSetId failed!");
        }*/

        return triviaSetId;

    }

}
