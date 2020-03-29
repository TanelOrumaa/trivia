package general.questions;

public class Answer {

    protected String answer;
    protected AnswerType answerType;

    public Answer(String answer, AnswerType answerType) {
        
        this.answer = answer;
        this.answerType = answerType;

    }

    public String getAnswer() {

        return answer;

    }

    public AnswerType getAnswerType() {

        return answerType;

    }

}
