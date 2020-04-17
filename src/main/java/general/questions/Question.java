package general.questions;

import java.util.List;

interface Question {

    boolean validateQuestion();

    void degradeScore();

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
