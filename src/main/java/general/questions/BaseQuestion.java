package general.questions;

public class BaseQuestion {

    protected QuestionType questionType;
    protected boolean scoreDegradation;
    protected String question;
    protected Answers correctAnswers;
    protected int potentialPoints;
    protected int time;
    protected final int minTime = 10; //Minimum answer time in seconds
    protected final int maxTime = 600;

    public BaseQuestion(QuestionType questionType) {

        this.questionType = questionType;

    }

    public BaseQuestion(QuestionType questionType, boolean scoreDegradation, String question, Answers correctAnswers, int potentialPoints, int time) {
        this.questionType = questionType;
        this.scoreDegradation = scoreDegradation;
        this.question = question;
        this.correctAnswers = correctAnswers;
        this.potentialPoints = potentialPoints;
        this.time = time;
    }

    public String getQuestion() {

        return question;

    }

    public int getPotentialPoints() {

        return potentialPoints;

    }

    public int getTime() {

        return time;

    }

    public Answers getCorrectAnswers() {

        return correctAnswers;

    }

    public QuestionType getQuestionType() {

        return questionType;

    }

    boolean validateQuestion() {

        if     (question == null ||
                question.length() < 1 ||
                question.length() > 600 ||
                potentialPoints < 0 ||
                time < minTime ||
                time > maxTime) {

            return false;

        }

        return true;

    }

    double degradeScore(double timeSpent) {
        //If point degradation is set to true, this method is called
        // to multiply the potential points with a quotient in range (0, 1),
        // which is specified in a specific type of question
        // and can be modified when creating the question.

        return potentialPoints * (1 - timeSpent/time);

    }

}
