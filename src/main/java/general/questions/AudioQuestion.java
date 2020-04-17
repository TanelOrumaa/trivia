package general.questions;

import java.util.List;

public class AudioQuestion extends BaseQuestion implements Question {

    private String audioAddress;

    public AudioQuestion() {
        //Constructor for creating a question from the application.

        super(QuestionType.AUDIO);

    }

    public AudioQuestion(AnswerType answerType, long questionID, boolean scoreDegradation, String question, List<Answer> answerList, int potentialPoints, int time, String audioAddress) {
        //Constructor for creating a question from the database.

        super(QuestionType.AUDIO, answerType, questionID, scoreDegradation, question, answerList, potentialPoints, time);
        this.audioAddress = audioAddress;

    }

    public String getAudioAddress() {

        return audioAddress;

    }

    @Override
    public boolean validateQuestion() {

        return false;

    }

    @Override
    public void degradeScore() {



    }

}
