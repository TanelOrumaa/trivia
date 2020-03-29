package general.questions;

import java.util.List;

public class AudioQuestion extends BaseQuestion implements Question {

    private String audioAddress;

    public String getAudioAddress() {

        return audioAddress;

    }

    public AudioQuestion(boolean scoreDegradation, String question, List<Answer> correctAnswers, int potentialPoints, int time) {

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
