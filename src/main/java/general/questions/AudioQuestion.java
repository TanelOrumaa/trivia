package general.questions;

public class AudioQuestion extends BaseQuestion implements Question {

    private String audioAddress;

    public String getAudioAddress() {

        return audioAddress;

    }

    public AudioQuestion(boolean scoreDegradation, String question, Answers correctAnswers, int potentialPoints, int time) {

        super(QuestionType.AUDIO, scoreDegradation, question, correctAnswers, potentialPoints, time);

    }

    @Override
    public boolean validateQuestion() {

        return false;

    }

    @Override
    public void degradeScore() {



    }

}
