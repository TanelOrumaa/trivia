package general.questions;

import java.util.TreeMap;

public class QuestionQueue {

    TreeMap<Long, Question> questionList;

    public QuestionQueue() {
        this.questionList = new TreeMap<>();
    }

    public void addQuestion(Question question) {
        questionList.put(question.getQuestionID(), question);
    }

    public Question getQuestion(Long id) {
        Question question = questionList.get(id);
        questionList.remove(id);
        return question;
    }

    public void clearQueue() {
        questionList.clear();
    }
}
