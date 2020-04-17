package general.questions;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import general.User;
import general.UserDeserializer;

import java.util.ArrayList;
import java.util.List;

public class BaseQuestion {

    protected QuestionType questionType;
    protected AnswerType answerType;
    protected long questionID;
    protected boolean scoreDegradation;
    protected String question;
    protected List<Answer> answerList;
    protected int potentialPoints;
    protected int time;
    protected final int minTime = 10; //Minimum answer time in seconds
    protected final int maxTime = 600;

    public static void main(String[] args) {

        List<Answer> answers = new ArrayList<>();
        answers.add(new Answer("siin", true, 1));
        answers.add(new Answer("seal", false, 2));
        answers.add(new Answer("Lible", true, 3));

        TextQuestion textQuestion = new TextQuestion(AnswerType.CHOICE, 1, true, "Kus sa oled", answers, 69, 420);
        ImageQuestion imageQuestion = new ImageQuestion(AnswerType.FREEFORM, 1, true, "Kus sa oled", answers, 69, 420, "teerada");
        AudioQuestion audioQuestion = new AudioQuestion(AnswerType.CHOICE, 1, false, "peeter", answers, 666, 420, "youtube");
        VideoQuestion videoQuestion = new VideoQuestion(AnswerType.CHOICE, 1, true, "Kus sa oled", answers, 69, 420, "twitter");

        Gson gson = new GsonBuilder().registerTypeAdapter(Question.class, new QuestionSerializer()).create();
        System.out.println(gson.toJson(textQuestion));
        System.out.println(gson.toJson(imageQuestion));
        System.out.println(gson.toJson(audioQuestion));
        System.out.println(gson.toJson(videoQuestion));

        Gson gson2 = new GsonBuilder().registerTypeAdapter(Question.class, new QuestionDeserializer()).create();
        Question audioQuestion2 = gson2.fromJson(gson.toJson(audioQuestion), Question.class);
        System.out.println(audioQuestion2.getPotentialPoints());
    }

    public BaseQuestion(QuestionType questionType) {
        //Constructor for initializing a question in the application.

        this.questionType = questionType;

    }

    public BaseQuestion(QuestionType questionType, AnswerType answerType, long questionID, boolean scoreDegradation,
                        String question, List<Answer> answerList, int potentialPoints, int time) {
        //Constructor for creating a question from the database.

        this.questionType = questionType;
        this.answerType = answerType;
        this.questionID = questionID;
        this.scoreDegradation = scoreDegradation;
        this.question = question;
        this.answerList = answerList;
        this.potentialPoints = potentialPoints;
        this.time = time;

    }

    boolean validateQuestion() {

        if     (question == null ||
                question.length() < 1 ||
                question.length() > 600 ||
                potentialPoints < 0 ||
                time < minTime ||
                time > maxTime) {

            return false;

        }

        return true;

    }

    double degradeScore(double timeSpent) {
        //If point degradation is set to true, this method is called
        // to multiply the potential points with a quotient in range (0, 1),
        // which is specified in a specific type of question
        // and can be modified when creating the question.

        return potentialPoints * (1 - timeSpent/time);

    }

    public void addAnswer(Answer answer) {
        //Todo: Check whether answer can be added or not.

        this.answerList.add(answer);

    }

    public QuestionType getQuestionType() {
        return questionType;
    }

    public AnswerType getAnswerType() {
        return answerType;
    }

    public long getQuestionID() {
        return questionID;
    }

    public boolean isScoreDegradation() {
        return scoreDegradation;
    }

    public String getQuestion() {
        return question;
    }

    public List<Answer> getAnswerList() {
        return answerList;
    }

    public int getPotentialPoints() {
        return potentialPoints;
    }

    public int getTime() {
        return time;
    }

    public int getMinTime() {
        return minTime;
    }

    public int getMaxTime() {
        return maxTime;
    }

    public void setQuestionType(QuestionType questionType) {
        this.questionType = questionType;
    }

    public void setAnswerType(AnswerType answerType) {
        this.answerType = answerType;
    }

    public void setScoreDegradation(boolean scoreDegradation) {
        this.scoreDegradation = scoreDegradation;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public void setPotentialPoints(int potentialPoints) {
        this.potentialPoints = potentialPoints;
    }

    public void setTime(int time) {
        this.time = time;
    }
}
