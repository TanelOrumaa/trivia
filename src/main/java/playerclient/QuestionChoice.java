package playerclient;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.ArrayList;

public class QuestionChoice {

    public static Scene change(Stage primaryStage) {

        double width = PlayerClient.getWidth();
        double height = PlayerClient.getHeight();

        //Multiple-choice question. Player choose one correct answer.
        ScrollPane questionChoiceScroller = new ScrollPane();
        questionChoiceScroller.setFitToWidth(true);

        Scene questionChoiceScreen = new Scene(questionChoiceScroller, width, height);
        VBox questionChoice = new VBox(20);

        Label questionChoiceQuestionText = new Label("Question");
        questionChoiceQuestionText.setWrapText(true);
        HBox questionChoiceQuestion = new HBox(questionChoiceQuestionText);
        questionChoiceQuestion.setPadding(new Insets(15));

        int questionChoiceNumber = 10;
        ArrayList<Button> questionChoiceOptions = new ArrayList<Button>();
        for (int i = 0; i < questionChoiceNumber; i++) {
            Button questionChoiceOptionButton = new Button(Integer.toString(i));
            questionChoiceOptionButton.setPrefSize(width / 4 * 3, height / 10);
            questionChoiceOptionButton.setOnAction(questionChoiceOptionButtonAction(primaryStage, i));
            questionChoiceOptions.add(questionChoiceOptionButton);
        }

        questionChoice.getChildren().add(questionChoiceQuestion);
        questionChoice.getChildren().addAll(questionChoiceOptions);

        questionChoiceQuestion.setAlignment(Pos.CENTER);
        questionChoice.setAlignment(Pos.CENTER);

        questionChoiceScroller.setContent(questionChoice);

        return questionChoiceScreen;
    }

    //Event handler: multiple-choice question's: get index of button that was clicked, and should send it to backend.
    //int i is the index
    static EventHandler<ActionEvent> questionChoiceOptionButtonAction(final Stage primaryStage, final int i) {
        return new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                primaryStage.setScene(WaitingAfterQuestion.change(primaryStage));
            }
        };
    }

}
