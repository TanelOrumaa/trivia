package general.questions;

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

}
