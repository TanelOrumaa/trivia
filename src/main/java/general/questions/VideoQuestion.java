package general.questions;

public class VideoQuestion extends BaseQuestion implements Question {

    private String videoAddress;

    public String getVideoAddress() {

        return videoAddress;

    }

    public VideoQuestion(boolean scoreDegradation, String question, Answers correctAnswers, int potentialPoints, int time) {

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
