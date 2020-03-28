package general.questions;

import java.nio.file.Path;
import java.util.List;

public class ImageQuestion extends BaseQuestion implements Question {

    private Path imagePath;

    public ImageQuestion(boolean scoreDegradation, String question, List<Answer> correctAnswers, int potentialPoints, int time) {

        super(QuestionType.IMAGE, scoreDegradation, question, correctAnswers, potentialPoints, time);

    }

    public Path getImagePath() {

        return imagePath;

    }

    @Override
    public boolean validateQuestion() {

        return false;

    }

    @Override
    public void degradeScore() {



    }

}
