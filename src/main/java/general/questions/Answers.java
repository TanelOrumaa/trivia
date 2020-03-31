package general.questions;

import java.util.List;

public class Answers {

    protected List<String> answers;
    protected AnswerType answerType;

    public Answers(List<String> answers, AnswerType answerType) {

        this.answers = answers;
        this.answerType = answerType;

    }

    public List<String> getAnswers() {

        return answers;

    }

    public AnswerType getAnswerType() {

        return answerType;

    }



}
