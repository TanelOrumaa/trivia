package general.questions;

public class TextQuestion extends BaseQuestion implements Question {

    public TextQuestion(boolean scoreDegradation, String question, Answers correctAnswers, int potentialPoints, int time) {

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
