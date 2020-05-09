package database;

import exception.QuestionRegistrationError;
import exception.TriviaSetRegistrationError;
import exception.TriviaSetsFetchingError;
import triviaset.TriviaSet;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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

    public static List<TriviaSet> readUsersTriviaSets(DatabaseConnection dbConnection, long userId){
        try (ResultSet resultSet = dbConnection.usersTriviaSetsStatement(userId).executeQuery()) {
            List<TriviaSet> triviaSetList = new ArrayList<>();

            while (resultSet.next()){
                int triviaSetId = resultSet.getInt("id");
                String triviaSetName = resultSet.getString("name");
                int triviaSetQuestionsCount = resultSet.getInt("questions_count");

                System.out.println("Trivia set: " + triviaSetId + ", " + triviaSetName + ", " + triviaSetQuestionsCount);
                TriviaSet triviaSet = new TriviaSet(triviaSetId, triviaSetName, triviaSetQuestionsCount);
                triviaSetList.add(triviaSet);
            }

            return triviaSetList;

        } catch (SQLException e) {
            throw new TriviaSetsFetchingError();
        }
    }

}
