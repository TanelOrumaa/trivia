package question;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.TreeMap;

public class QuestionQueue {

    // Logger
    static final Logger LOG = LoggerFactory.getLogger(QuestionQueue.class);

    TreeMap<Long, Question> questionList;

    public QuestionQueue() {
        this.questionList = new TreeMap<>();
    }

    public void addQuestion(Question question) {
        questionList.put(question.getQuestionID(), question);
    }

    public Question getQuestion(Long id) {
        LOG.debug("Fetching question with id " + id);
        Question question = questionList.get(id);
        return question;
    }

    public void clearQueue() {
        questionList.clear();
    }
}
