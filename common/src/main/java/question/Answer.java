package question;

public class Answer {

    protected String answerText;
    protected boolean isCorrect;
    protected long answerID;

    public Answer(String answerText, boolean isCorrect) {
        //Constructor for creating a question from the application.

        this(-1, answerText, isCorrect);

    }

    public Answer(long answerID, String answerText, boolean isCorrect) {
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
