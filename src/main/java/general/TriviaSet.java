package general;

import general.questions.Question;

import java.util.LinkedHashMap;
import java.util.List;

public class TriviaSet {

    private String name;
    private int numberOfQuestions;
    private LinkedHashMap<Integer, Question> questionMap;

    public TriviaSet(String name, List<Question> questions) {

        this.name = name;
        this.numberOfQuestions = questions.size();
        this.questionMap = new LinkedHashMap<>();
        if (questions != null) {

            questions.forEach(question -> questionMap.put(questions.indexOf(question), question));

        }

    }

    void reorderQuestion(int oldIndex, int newIndex) {
        //Method for rearranging a question and shifting the other questions correctly

        if (oldIndex < 0) {

        } else if (oldIndex >= questionMap.size()) {


        } else if (newIndex > oldIndex) {

            if (newIndex >= questionMap.size() - 1) {

                newIndex = questionMap.size() - 1;

            }

            Question questionAtOldIndex = questionMap.get(oldIndex);
            Question questionAtNewIndex = questionMap.get(newIndex);
            questionMap.put(newIndex, questionAtOldIndex);
            for (int i = newIndex - 1; i > oldIndex - 1; i--) {

                questionAtOldIndex = questionMap.get(i);
                questionMap.put(i, questionAtNewIndex);
                questionAtNewIndex = questionAtOldIndex;

            }

        } else if (newIndex <= oldIndex) {

            if (newIndex < 0) {

                newIndex = 0;

            }

            Question questionAtOldIndex = questionMap.get(oldIndex);
            Question questionAtNewIndex = questionMap.get(newIndex);
            questionMap.put(newIndex, questionAtOldIndex);
            for (int i = newIndex + 1; i < oldIndex + 1; i++) {

                questionAtOldIndex = questionMap.get(i);
                questionMap.put(i, questionAtNewIndex);
                questionAtNewIndex = questionAtOldIndex;

            }

        }

    }

    void addQuestion(Question question) {

        questionMap.put(questionMap.size(), question);
        numberOfQuestions++;

    }

    boolean hasNextQuestion(int previousQuestionIndex) {

        return previousQuestionIndex >= questionMap.size();

    }

    Question getNextQuestion(int previousQuestionIndex) {

        return questionMap.get(previousQuestionIndex + 1);

    }

    public String getName() {
        return name;
    }

    public LinkedHashMap<Integer, Question> getQuestionMap() {
        return questionMap;
    }

    public int getNumberOfQuestions() {
        return numberOfQuestions;
    }

    public void setName(String name) {
        this.name = name;
    }

}
