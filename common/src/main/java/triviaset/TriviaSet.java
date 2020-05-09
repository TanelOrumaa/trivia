package triviaset;

import question.Question;

import java.util.LinkedHashMap;
import java.util.List;

public class TriviaSet {

    private String name;
    private int numberOfQuestions;
    private LinkedHashMap<Integer, Question> questionMap;

    public TriviaSet(String name, int numberOfQuestions) {
        //Constructor for creating a TriviaSet just for display.

        this.name = name;
        this.numberOfQuestions = numberOfQuestions;

    }

    public TriviaSet(String name, List<Question> questions) {

        this.name = name;
        this.numberOfQuestions = questions.size();
        this.questionMap = new LinkedHashMap<>();
        if (questions != null) {

            questions.forEach(question -> questionMap.put(questions.indexOf(question), question));

        }

    }

    public void reorderQuestion(int oldIndex, int newIndex) {
        //Method for rearranging a question and shifting the other questions correctly

        if (oldIndex >= numberOfQuestions || oldIndex < 0) {

        }

        Question questionBuffer;

        if (newIndex > oldIndex) {

            if (newIndex >= numberOfQuestions - 1) {

                newIndex = numberOfQuestions - 1;

            }

                questionBuffer = questionMap.get(oldIndex);
                for (int i = oldIndex + 1; i <= newIndex; i++) {

                    questionMap.put(i - 1, questionMap.get(i));

                }
                questionMap.put(newIndex, questionBuffer);

        } else if (newIndex <= oldIndex) {

            if (newIndex < 0) {

                newIndex = 0;

            }

            questionBuffer = questionMap.get(oldIndex);
            for (int i = oldIndex; i >= newIndex; i--) {

                questionMap.put(i, questionMap.get(i - 1));

            }
            questionMap.put(newIndex, questionBuffer);

        }

    }

    public void changeQuestionTime(int questionIndex, int newTime) {

        questionMap.get(questionIndex).setTime(newTime);

    }

    public void changeQuestionPoints(int questionIndex, int newPotentialPoints) {

        questionMap.get(questionIndex).setPotentialPoints(newPotentialPoints);

    }

    public void changeQuestionText(int questionIndex, String newquestionText) {

        questionMap.get(questionIndex).setQuestion(newquestionText);

    }

    public void addQuestion(Question question) {

        questionMap.put(numberOfQuestions, question);
        numberOfQuestions++;

    }

    public boolean hasNextQuestion(int previousQuestionIndex) {

        return previousQuestionIndex < numberOfQuestions - 1;

    }

    public Question getNextQuestion(int previousQuestionIndex) {

        if (hasNextQuestion(previousQuestionIndex)) {

            return questionMap.get(previousQuestionIndex + 1);

        }

        return null;

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
