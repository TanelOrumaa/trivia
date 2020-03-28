package general.questions;

import java.nio.file.Path;
import java.util.List;

public class ImageQuestion extends BaseQuestion implements Question {


    public ImageQuestion(QuestionType questionType) {

        super(questionType);

    }

    public ImageQuestion(QuestionType questionType, boolean scoreDegradation, String question, List<String> correctAnswers, int potentialPoints, int time) {

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
