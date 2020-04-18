package general;

import general.questions.Question;

import java.util.LinkedHashMap;
import java.util.List;

public class TriviaSet {

    private String name;
    private LinkedHashMap<Integer, Question> questionListManager;

    public TriviaSet(String name, List<Question> questions) {

        this.name = name;
        this.questionListManager = new LinkedHashMap<>();
        if (questions != null) {

            questions.forEach(question -> questionListManager.put(questions.indexOf(question), question));

        }

    }

    void reorderQuestion(int oldIndex, int newIndex) {
        //Method for rearranging a question and shifting the other questions correctly

        if (oldIndex < 0) {

        } else if (oldIndex >= questionListManager.size()) {


        } else if (newIndex > oldIndex) {

            if (newIndex >= questionListManager.size() - 1) {

                newIndex = questionListManager.size() - 1;

            }

            Question questionAtOldIndex = questionListManager.get(oldIndex);
            Question questionAtNewIndex = questionListManager.get(newIndex);
            questionListManager.put(newIndex, questionAtOldIndex);
            for (int i = newIndex - 1; i > oldIndex - 1; i--) {

                questionAtOldIndex = questionListManager.get(i);
                questionListManager.put(i, questionAtNewIndex);
                questionAtNewIndex = questionAtOldIndex;

            }

        } else if (newIndex <= oldIndex) {

            if (newIndex < 0) {

                newIndex = 0;

            }

            Question questionAtOldIndex = questionListManager.get(oldIndex);
            Question questionAtNewIndex = questionListManager.get(newIndex);
            questionListManager.put(newIndex, questionAtOldIndex);
            for (int i = newIndex + 1; i < oldIndex + 1; i++) {

                questionAtOldIndex = questionListManager.get(i);
                questionListManager.put(i, questionAtNewIndex);
                questionAtNewIndex = questionAtOldIndex;

            }

        }

    }

    void addQuestion(Question question) {

        questionListManager.put(questionListManager.size(), question);

    }

    boolean hasNextQuestion(int previousQuestionIndex) {

        return previousQuestionIndex >= questionListManager.size();

    }

    Question getNextQuestion(int previousQuestionIndex) {

        return questionListManager.get(previousQuestionIndex + 1);

    }

    public String getName() {
        return name;
    }

    public LinkedHashMap<Integer, Question> getQuestionListManager() {
        return questionListManager;
    }

    public void setName(String name) {
        this.name = name;
    }

}
