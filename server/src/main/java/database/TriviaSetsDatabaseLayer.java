package database;

import exception.QuestionRegistrationError;
import exception.QuestionsFetchingError;
import exception.TriviaSetRegistrationError;
import exception.TriviaSetsFetchingError;
import question.*;
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

            } catch (SQLException e) {
                throw new RuntimeException("SQL query for getting triviaSetId failed!", e);
            }

        } catch (SQLException e) {
            throw new TriviaSetRegistrationError();
        }

        return triviaSetId;

    }

    public static List<TriviaSet> readUsersTriviaSets(DatabaseConnection dbConnection, long userId) {
        try (ResultSet resultSet = dbConnection.usersTriviaSetsStatement(userId).executeQuery()) {
            List<TriviaSet> triviaSetList = new ArrayList<>();

            while (resultSet.next()) {
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


    public static TriviaSet readFullTriviaSet(DatabaseConnection dbConnection, int triviaSetId, String triviaSetName) {
        try (ResultSet questionsResultSet = dbConnection.questionsByTriviaSetIdStatement(triviaSetId).executeQuery()) {
            List<Question> questionsList = new ArrayList<>();
            while (questionsResultSet.next()) {
                // Get info for each question
                long questionId = questionsResultSet.getLong("id");
                String questionType = questionsResultSet.getString("question_type");
                String answerType = questionsResultSet.getString("answer_type");
                boolean scoreDegradation = questionsResultSet.getBoolean("score_degradation");
                int potentialpoints = questionsResultSet.getInt("potential_points");
                int time = questionsResultSet.getInt("time");
                String questionText = questionsResultSet.getString("question_text");
                String mediaPath = questionsResultSet.getString("media_path");

                List<Answer> answerList = new ArrayList<>();

                // Get answers for each question
                try (ResultSet answersResultSet = dbConnection.answersByQuestionIdStatement(questionId).executeQuery()) {
                    while (answersResultSet.next()) {
                        String answerText = answersResultSet.getString("answer_text");
                        boolean isCorrect = answersResultSet.getBoolean("is_correct");
                        answerList.add(new Answer(answerText, isCorrect));
                    }
                }

                AnswerType answerTypeEnum = null;
                switch (answerType) {
                    case "freeform":
                        answerTypeEnum = AnswerType.FREEFORM;
                        break;
                    case "choice":
                        answerTypeEnum = AnswerType.CHOICE;
                        break;

                }

                Question question = null;
                switch (questionType) {
                    case "text":
                        question = new TextQuestion(answerTypeEnum, questionId, scoreDegradation, questionText, answerList, potentialpoints, time);
                        break;
                    case "image":
                        question = new ImageQuestion(answerTypeEnum, questionId, scoreDegradation, questionText, answerList, potentialpoints, time, mediaPath);
                        break;
                    case "audio":
                        question = new AudioQuestion(answerTypeEnum, questionId, scoreDegradation, questionText, answerList, potentialpoints, time, mediaPath);
                        break;
                    case "video":
                        question = new VideoQuestion(answerTypeEnum, questionId, scoreDegradation, questionText, answerList, potentialpoints, time, mediaPath);
                        break;
                }

                questionsList.add(question);
            }

            return new TriviaSet(triviaSetId, triviaSetName, questionsList);

        } catch (SQLException e) {
            throw new QuestionsFetchingError();
        }
    }

}
