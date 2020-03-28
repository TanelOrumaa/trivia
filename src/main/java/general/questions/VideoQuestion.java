package general.questions;

import java.util.List;

public class VideoQuestion extends BaseQuestion implements Question {

    private String address;

    public String getAddress() {

        return address;

    }

    public VideoQuestion(QuestionType questionType) {

        super(questionType);

    }

    public VideoQuestion(QuestionType questionType, boolean scoreDegradation, String question, List<String> correctAnswers, int potentialPoints, int time) {

        super(questionType, scoreDegradation, question, correctAnswers, potentialPoints, time);

    }

    @Override
    public boolean validateQuestion() {

        return false;

    }

    @Override
    public void degradeScore() {



    }

}
