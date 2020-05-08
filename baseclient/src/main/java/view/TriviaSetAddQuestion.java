package view;

import baseclient.BaseClient;
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
import triviaset.TriviaSet;

import java.util.List;

public class TriviaSetAddQuestion extends Scene {

    private static final Font textFont = Font.font("Berlin Sans FB Demi", 20);

    public TriviaSetAddQuestion(BaseClient baseClient, TriviaSet triviaSet) {
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

        TextField titleInput = new TextField("Enter question");

        Label questionTypeLabel = new Label("Choose type of question");
        ChoiceBox questionTypes = new ChoiceBox(FXCollections.observableArrayList(List.of("Text", "Image", "Audio", "Video")));
        questionTypes.valueProperty().addListener((observableValue, oldValue, newValue) -> {
            switch((String)questionTypes.getValue()) {
                case "Text":
                    break;
                case "Image":
                    break;
                case "Audio":
                    break;
                case "Video":
                    break;
            }
        });

        Label answerTypeLabel = new Label("Choose type of answer");
        ChoiceBox answerType = new ChoiceBox(FXCollections.observableArrayList(List.of("Free-form", "Choice")));
        answerType.valueProperty().addListener((observableValue, oldValue, newValue) -> {
            switch((String)answerType.getValue()) {
                case "Free-form":
                    break;
                case "Choice":
                    break;
            }
        });


        Button addButton = new Button("Add this question");
        addButton.setOnMouseReleased(mouseEvent -> {
            //Add question to triviaset.
            baseClient.setGuiStage(new TriviaSetAddQuestion(baseClient, triviaSet));
        });

        Button finishButton = new Button("Finish");
        addButton.setOnMouseReleased(mouseEvent -> {
            //Finish adding questions.
            baseClient.setGuiStage(new TriviaSetMenu(baseClient));
        });

        Button backButton = new Button("Quit");
        backButton.setOnMouseReleased(mouseEvent -> {
            //Quit creating trivia set.
            baseClient.setGuiStage(new TriviaSetMenu(baseClient));
        });

        VBox buttonsArea = new VBox(titleInput, questionTypeLabel, questionTypes, answerTypeLabel, answerType, addButton, finishButton, backButton);

        mainBox.getChildren().addAll(usernameLabelArea, buttonsArea);

        root.setCenter(mainBox);

        super.setRoot(root);

    }

}
