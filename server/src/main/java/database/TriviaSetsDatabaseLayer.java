package database;

import exception.QuestionsFetchingError;
import exception.TriviaSetRegistrationError;
import exception.TriviaSetsFetchingError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import question.*;
import triviaset.TriviaSet;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TriviaSetsDatabaseLayer {

    static Logger LOG = LoggerFactory.getLogger(TriviaSetsDatabaseLayer.class);

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
                long triviaSetId = resultSet.getLong("id");
                String triviaSetName = resultSet.getString("name");
                int triviaSetQuestionsCount = resultSet.getInt("questions_count");

                System.out.println("Trivia set: " + triviaSetId + ", " + triviaSetName + ", " + triviaSetQuestionsCount);
                TriviaSet triviaSet = new TriviaSet(triviaSetId, triviaSetName, triviaSetQuestionsCount, userId);
                triviaSetList.add(triviaSet);
            }

            return triviaSetList;

        } catch (SQLException e) {
            throw new TriviaSetsFetchingError();
        }
    }


    public static TriviaSet readFullTriviaSet(DatabaseConnection dbConnection, long triviaSetId) {
        try (ResultSet triviaSetResultSet = dbConnection.getTriviaSetByIdStatement(triviaSetId).executeQuery()) {
                triviaSetResultSet.next();
                String triviaSetName = triviaSetResultSet.getString("name");
                int questionsCount = triviaSetResultSet.getInt("questions_count");
                long userId = triviaSetResultSet.getLong("user_id");

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
                                long answerId = answersResultSet.getLong("id");
                                String answerText = answersResultSet.getString("answer_text");
                                boolean isCorrect = answersResultSet.getBoolean("is_correct");
                                answerList.add(new Answer(answerId, answerText, isCorrect));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
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

                    if (questionsList.size() == questionsCount) {
                        return new TriviaSet(triviaSetId, triviaSetName, questionsList, userId);
                    } else {
                        throw new RuntimeException("Invalid number of questions fetched.");
                    }
                }


        } catch (SQLException e) {
            throw new QuestionsFetchingError();
        }
    }

}
