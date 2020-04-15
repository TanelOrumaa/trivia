package general.questions;

public class VideoQuestion extends BaseQuestion implements Question {

    private String videoAddress;

    public VideoQuestion(QuestionType questionType, long questionID, boolean scoreDegradation, String question, Answers correctAnswers, int potentialPoints, int time, String videoAddress) {

        super(questionType, questionID, scoreDegradation, question, correctAnswers, potentialPoints, time);
        this.videoAddress = videoAddress;

    }

    public String getVideoAddress() {

        return videoAddress;

    }

    @Override
    public boolean validateQuestion() {

        return false;

    }

    @Override
    public void degradeScore() {



    }

}
