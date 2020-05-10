package triviaset;

import question.Question;

import java.util.LinkedHashMap;
import java.util.List;

public class TriviaSet {

    private Long id;
    private String name;
    private int numberOfQuestions;
    private LinkedHashMap<Integer, Question> questionMap;
    private Long ownerId;

    public TriviaSet(Long id, String name, int numberOfQuestions, Long ownerId) {
        //Constructor for creating a TriviaSet just for display.
        this.id = id;
        this.name = name;
        this.numberOfQuestions = numberOfQuestions;
        this.ownerId = ownerId;
    }

    public TriviaSet(Long id, String name, List<Question> questions, Long ownerId) {

        this.id = id;
        this.name = name;
        this.numberOfQuestions = questions.size();
        this.questionMap = new LinkedHashMap<>();
        if (questions != null) {

            questions.forEach(question -> questionMap.put(questions.indexOf(question), question));

        }
        this.ownerId = ownerId;

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

    public String getQuestionAnswer(long questionId, long answerId) {
        return this.getQuestionWithId(questionId).getAnswerWithId(answerId).getAnswerText();
    }

    private Question getQuestionWithId(long questionId) {

        for (int key : questionMap.keySet()) {
            if (questionMap.get(key).getQuestionID() == questionId) {
                return questionMap.get(key);
            }
        }
        return null;
    }

    public Long getId() {
        return id;
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

    public Long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }
}
