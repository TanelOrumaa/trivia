package general.questions;

import java.util.List;

public class VideoQuestion extends BaseQuestion implements Question {

    private String videoAddress;

    public VideoQuestion() {
        //Constructor for creating a question from the application.

        super(QuestionType.VIDEO);

    }

    public VideoQuestion(AnswerType answerType, long questionID, boolean scoreDegradation, String question, List<Answer> answerList, int potentialPoints, int time, String videoAddress) {
        //Constructor for creating a question from the database.

        super(QuestionType.VIDEO, answerType, questionID, scoreDegradation, question, answerList, potentialPoints, time);
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
