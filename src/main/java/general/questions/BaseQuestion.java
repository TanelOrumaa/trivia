package general.questions;

public class BaseQuestion {

    protected QuestionType questionType;
    protected boolean scoreDegradation;
    protected String question;
    protected int points;
    protected int time;
    protected final int minTime = 10; //Minimum answer time in seconds

    public BaseQuestion(QuestionType questionType, boolean scoreDegradation, String question, int points, int time) {

        this.questionType = questionType;
        this.scoreDegradation = scoreDegradation;
        this.question = question;
        this.points = points;
        this.time = time;

    }

    boolean validateQuestion() {

        if (question == null || question.length() < 1 || points < 0 || time < minTime) {

            return false;

        }

        return true;

    }

    void degradeScore(double degradationQuotient) {
        //If point degradation is set to true, this method is called
        // to multiply the potential points with a quotient in range (0, 1),
        // which is specified in a specific type of question
        // and can be modified when creating the question.

        this.points *= degradationQuotient;

    }

}
