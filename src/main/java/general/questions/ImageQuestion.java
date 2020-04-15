package general.questions;

import java.nio.file.Path;

public class ImageQuestion extends BaseQuestion implements Question {

    private Path imagePath;

    public ImageQuestion(QuestionType questionType, long questionID, boolean scoreDegradation, String question, Answers correctAnswers, int potentialPoints, int time, Path imagePath) {

        super(questionType, questionID, scoreDegradation, question, correctAnswers, potentialPoints, time);
        this.imagePath = imagePath;

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
