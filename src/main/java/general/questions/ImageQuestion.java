package general.questions;

import java.nio.file.Path;
import java.util.List;

public class ImageQuestion extends BaseQuestion implements Question {

    private String imagePath;

    public ImageQuestion() {
        //Constructor for creating a question from the application.

        super(QuestionType.IMAGE);

    }

    public ImageQuestion(AnswerType answerType, long questionID, boolean scoreDegradation, String question, List<Answer> answerList, int potentialPoints, int time, String imagePath) {
        //Constructor for creating a question from the database.

        super(QuestionType.IMAGE, answerType, questionID, scoreDegradation, question, answerList, potentialPoints, time);
        this.imagePath = imagePath;

    }

    public String getMediaPath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    @Override
    public boolean validateQuestion() {

        return false;

    }

    @Override
    public void degradeScore() {



    }

}
