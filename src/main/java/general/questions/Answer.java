package general.questions;

public class Answer {

    protected String answerText;
    protected boolean isCorrect;
    protected long answerID;

    public Answer(String answerText, boolean isCorrect) {
        //Constructor for creating a question from the application.

        new Answer(answerText, isCorrect, -1);

    }

    public Answer(String answerText, boolean isCorrect, long answerID) {
        //Constructor for creating an existing answer from the database.

        this.answerText = answerText;
        this.isCorrect = isCorrect;
        this.answerID = answerID;

    }

    public String getAnswerText() {
        return answerText;
    }

    public void setAnswerText(String answerText) {
        this.answerText = answerText;
    }

    public boolean isCorrect() {
        return isCorrect;
    }

    public void setCorrect(boolean correct) {
        isCorrect = correct;
    }

    public long getAnswerID() {
        return answerID;
    }

}
