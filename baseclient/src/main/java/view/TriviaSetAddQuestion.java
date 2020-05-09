package view;

import baseclient.BaseClient;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import command.Command;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import popup.ErrorMessage;
import question.*;
import triviaset.TriviaSet;
import triviaset.TriviaSetSerializerFull;

import java.util.ArrayList;
import java.util.List;

import static baseclient.BaseClientBackEnd.addCommandToBackEnd;

public class TriviaSetAddQuestion extends Scene {

    private static final Font textFont = Font.font("Berlin Sans FB Demi", 20);
    private List<Answer> answersList = new ArrayList<>();

    public TriviaSetAddQuestion(BaseClient baseClient, TriviaSet triviaSet) {
        //TODO : ülevaade lisatud küsimustest

        super(new BorderPane(), baseClient.getWidth(), baseClient.getHeight());
        double width = baseClient.getWidth();
        double height = baseClient.getHeight();

        BorderPane root = new BorderPane();
        VBox mainBox = new VBox(20);
        mainBox.setStyle("-fx-background-color: ROYALBLUE;");

        final Label usernameLabel = new Label(baseClient.getUsername());
        usernameLabel.setFont(textFont);

        HBox usernameLabelArea = new HBox(usernameLabel);
        usernameLabelArea.setAlignment(Pos.TOP_RIGHT);
        usernameLabelArea.setPadding(new Insets(width * 0.05, width * 0.05, width * 0.05, width * 0.05));


        //Question-answers

        TextField questionText = new TextField("Enter question");
        Label questionTypeLabel = new Label("Choose type of question");
        ChoiceBox questionTypes = new ChoiceBox(FXCollections.observableArrayList(List.of("Text", "Image", "Audio", "Video")));
        questionTypes.getSelectionModel().select(0);
        CheckBox scoreDegr = new CheckBox("Score degradation");
        TextField timeField = new TextField("Time");
        TextField potentialPointsField = new TextField("PotentialPoints");
        VBox questionBox = new VBox(questionText, questionTypeLabel, questionTypes, scoreDegr, timeField, potentialPointsField);

        Label answerTypeLabel = new Label("Choose type of answer");
        ChoiceBox answerType = new ChoiceBox(FXCollections.observableArrayList(List.of("Free-form", "Choice")));
        Label answerTextLabel = new Label("Enter answer text");
        TextField answerText = new TextField();
        CheckBox isCorrect = new CheckBox("Correct answer");
        Button addAnswerButton = new Button("Add answer");
        VBox answerBox = new VBox(answerTypeLabel, answerType);


        Label pathLabel = new Label("");
        TextField path = new TextField();


        questionTypes.valueProperty().addListener((observableValue, oldValue, newValue) -> {

            switch ((String) questionTypes.getValue()) {
                case "Text":
                    if (questionBox.getChildren().contains(pathLabel)) {
                        questionBox.getChildren().removeAll(pathLabel, path);
                    }
                    break;
                case "Image":
                    pathLabel.setText("Insert image path");
                    if (!questionBox.getChildren().contains(pathLabel)) {
                        questionBox.getChildren().addAll(pathLabel, path);
                    }
                    break;
                case "Audio":
                    pathLabel.setText("Insert audio URL");
                    if (!questionBox.getChildren().contains(pathLabel)) {
                        questionBox.getChildren().addAll(pathLabel, path);
                    }
                    break;
                case "Video":
                    pathLabel.setText("Insert video URL");
                    if (!questionBox.getChildren().contains(pathLabel)) {
                        questionBox.getChildren().addAll(pathLabel, path);
                    }
                    break;
            }
        });

        answerType.valueProperty().addListener((observableValue, oldValue, newValue) -> {
            if (!answerBox.getChildren().contains(answerTextLabel)) {
                answerBox.getChildren().addAll(answerTextLabel, answerText, addAnswerButton);
            }
            switch ((String) answerType.getValue()) {
                case "Free-form":
                    answerBox.getChildren().remove(isCorrect);
                    break;
                case "Choice":
                    if (!answerBox.getChildren().contains(isCorrect)) {
                        answerBox.getChildren().add(isCorrect);
                    }
                    break;
            }
        });

        addAnswerButton.setOnMouseReleased(mouseEvent -> {
            if (answerBox.getChildren().contains(isCorrect)) {
                answersList.add(new Answer(answerText.getText(), isCorrect.isSelected()));
            } else {
                answersList.add(new Answer(answerText.getText(), true));
            }
            System.out.println(answersList.size());
            answerText.clear();
        });


        //Buttons
        Button addButton = new Button("Add this question");
        addButton.setOnMouseReleased(mouseEvent -> {
            //Add question to triviaset.
            Question question = null;
            switch ((String) questionTypes.getValue()) {
                case "Text":
                    question = new TextQuestion();
                    break;
                case "Image":
                    question = new ImageQuestion();
                    break;
                case "Audio":
                    question = new AudioQuestion();
                    break;
                case "Video":
                    question = new VideoQuestion();
                    break;
            }
            if (questionText.getText().equals("")) {
                ErrorMessage.popUp("Question field can't be empty!");
                return;
            }
            question.setQuestion(questionText.getText());
            int questionTime;
            try {
                questionTime = Integer.parseInt(timeField.getText());
                if (questionTime < question.getMinTime() || questionTime > question.getMaxTime()) {
                    ErrorMessage.popUp("Time value should be between " + question.getMinTime() + " and " + question.getMaxTime());
                    return;
                }
                question.setTime(questionTime);
            } catch (NumberFormatException e) {
                ErrorMessage.popUp("Time must be number");
                return;
            }
            int potentialPoints;
            try {
                potentialPoints = Integer.parseInt(potentialPointsField.getText());
                question.setPotentialPoints(potentialPoints);
            } catch (NumberFormatException e) {
                ErrorMessage.popUp("Potential points field must be number");
                return;
            }
            switch ((String) answerType.getValue()) {
                case "Free-form":
                    question.setAnswerType(AnswerType.FREEFORM);
                    break;
                case "Choice":
                    question.setAnswerType(AnswerType.CHOICE);
                    break;
                default:
                    ErrorMessage.popUp("No answers are added");
                    return;
            }
            question.setScoreDegradation(scoreDegr.isSelected());
            answersList.forEach(question::addAnswer);
            triviaSet.addQuestion(question);
            System.out.println(triviaSet.getQuestionMap());
            baseClient.setGuiStage(new TriviaSetAddQuestion(baseClient, triviaSet));
        });

        Button finishButton = new Button("Finish adding questions");
        finishButton.setOnMouseReleased(mouseEvent -> {
            //Finish adding questions.
            Gson gson = new GsonBuilder().registerTypeAdapter(TriviaSet.class, new TriviaSetSerializerFull()).create();
            String triviaSetAsJson = gson.toJson(triviaSet);
            System.out.println("TriviaSetAddQuestion " + triviaSetAsJson);
            addCommandToBackEnd(215, new String[]{triviaSetAsJson}, 0);
        });

        Button backButton = new Button("Quit");
        backButton.setOnMouseReleased(mouseEvent -> {
            //Quit creating trivia set.
            baseClient.setGuiStage(new TriviaSetMenu(baseClient));
        });

        VBox buttonsArea = new VBox(addButton, finishButton, backButton);


        mainBox.getChildren().addAll(usernameLabel, questionBox, answerBox, buttonsArea);

        root.setCenter(mainBox);

        super.setRoot(root);

    }


}
