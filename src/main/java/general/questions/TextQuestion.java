package general.questions;

public class TextQuestion extends BaseQuestion implements Question {

    public TextQuestion(QuestionType questionType, long questionID, boolean scoreDegradation, String question, Answers correctAnswers, int potentialPoints, int time) {

        super(questionType, questionID, scoreDegradation, question, correctAnswers, potentialPoints, time);

    }

    @Override
    public boolean validateQuestion() {

        return false;

    }

    @Override
    public void degradeScore() {



    }

}
