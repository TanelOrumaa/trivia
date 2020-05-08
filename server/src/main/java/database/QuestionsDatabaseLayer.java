package database;

import exception.AnswerRegistrationError;
import exception.QuestionRegistrationError;
import question.Answer;
import question.Question;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class QuestionsDatabaseLayer {

    public static void registerQuestion(DatabaseConnection dbConnection, Question question, int triviaSetId) {

        try (PreparedStatement ps = dbConnection.registerQuestionStatement(question, triviaSetId)) {

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new QuestionRegistrationError();
        }

        long questionId = -1;

        try (ResultSet questionIdResult = dbConnection.lastQuestionIdStatement().executeQuery()) {

            if (questionIdResult.next()) {

                questionId = questionIdResult.getLong("id");

            }

        } catch (SQLException e){
            throw new RuntimeException("SQL query for getting questionId failed!");
        }

        for (Answer answer : question.getAnswerList()) {

            try (PreparedStatement ps = dbConnection.registerAnswerStatement(answer, questionId)) {

                ps.executeUpdate();

            } catch (SQLException e) {
                throw new AnswerRegistrationError();
            }

        }

    }

}
