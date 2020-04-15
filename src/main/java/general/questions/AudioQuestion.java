package general.questions;

public class AudioQuestion extends BaseQuestion implements Question {

    private String audioAddress;

    public AudioQuestion(QuestionType questionType, long questionID, boolean scoreDegradation, String question, Answers correctAnswers, int potentialPoints, int time, String audioAddress) {

        super(questionType, questionID, scoreDegradation, question, correctAnswers, potentialPoints, time);
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
