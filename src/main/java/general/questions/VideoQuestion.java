package general.questions;

import java.util.List;

public class VideoQuestion extends BaseQuestion implements Question {

    private String videoAddress;

    public String getVideoAddress() {

        return videoAddress;

    }

    public VideoQuestion(boolean scoreDegradation, String question, List<Answer> correctAnswers, int potentialPoints, int time) {

        super(QuestionType.VIDEO, scoreDegradation, question, correctAnswers, potentialPoints, time);

    }

    @Override
    public boolean validateQuestion() {

        return false;

    }

    @Override
    public void degradeScore() {



    }

}
