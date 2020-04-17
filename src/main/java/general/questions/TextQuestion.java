package general.questions;

import java.util.List;

public class TextQuestion extends BaseQuestion implements Question {

    public TextQuestion() {
        //Constructor for creating a question from the application.

        super(QuestionType.TEXT);

    }

    public TextQuestion(AnswerType answerType, long questionID, boolean scoreDegradation, String question, List<Answer> answerList, int potentialPoints, int time) {
        //Constructor for creating a question from the database.

        super(QuestionType.TEXT, answerType, questionID, scoreDegradation, question, answerList, potentialPoints, time);

    }

    @Override
    public boolean validateQuestion() {

        return false;

    }

    @Override
    public void degradeScore() {



    }

}
