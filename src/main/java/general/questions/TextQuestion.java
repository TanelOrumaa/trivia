package general.questions;

import java.util.List;

public class TextQuestion extends BaseQuestion implements Question {

    public TextQuestion(boolean scoreDegradation, String question, List<Answer> correctAnswers, int potentialPoints, int time) {

        super(QuestionType.TEXT, scoreDegradation, question, correctAnswers, potentialPoints, time);

    }

    @Override
    public boolean validateQuestion() {

        return false;

    }

    @Override
    public void degradeScore() {



    }

}
