package general.questions;

import java.util.List;

public class TextQuestion extends BaseQuestion implements Question {


    public TextQuestion(QuestionType questionType) {

        super(questionType);

    }

    public TextQuestion(QuestionType questionType, boolean scoreDegradation, String question, List<String> correctAnswers, int potentialPoints, int time) {

        super(questionType, scoreDegradation, question, correctAnswers, potentialPoints, time);

    }

    @Override
    public boolean validateQuestion() {

        return false;

    }

    @Override
    public void degradeScore() {



    }

}
