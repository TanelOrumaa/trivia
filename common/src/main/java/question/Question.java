package question;

import java.util.List;

public interface Question {

    boolean validateQuestion();

    void degradeScore();

    String getMediaPath();

    QuestionType getQuestionType();

    AnswerType getAnswerType();

    long getQuestionID();

    boolean isScoreDegradation();

    String getQuestion();

    List<Answer> getAnswerList();

    int getPotentialPoints();

    int getTime();

    int getMinTime();

    int getMaxTime();

    void setTime(int time);

    void setQuestionType(QuestionType questionType);

    void setAnswerType(AnswerType answerType);

    void setScoreDegradation(boolean scoreDegradation);

    void setQuestion(String question);

    void setPotentialPoints(int potentialPoints);

}
