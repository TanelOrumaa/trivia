package baseclient;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class QuestionTextArea {

    public static Scene change(Stage primaryStage, BaseClient frontEnd) {

        double width = frontEnd.getWidth();
        double height = frontEnd.getHeight();

        //Text area question. Player writes answer.
        BorderPane questionTextAreaRoot = new BorderPane();
        Scene questionTextAreaScreen = new Scene(questionTextAreaRoot, width, height);
        VBox questionTextArea = new VBox(20);

        Label questionTextAreaQuestionText = new Label("Question");
        questionTextAreaQuestionText.setWrapText(true);
        HBox questionTextAreaQuestion = new HBox(questionTextAreaQuestionText);

        TextArea questionTextAreaInput = new TextArea();
        questionTextAreaInput.setPrefSize(width / 4 * 3,height / 5);
        questionTextAreaInput.setWrapText(true);
        HBox questionTextAreaInputBox = new HBox(questionTextAreaInput);

        Button questionTextAreaButton = new Button("Answer");
        questionTextAreaButton.setOnAction(new EventHandler<ActionEvent>() {
            //Event handler: send text to server
            @Override
            public void handle(ActionEvent actionEvent) {
                //text.getText(); and send to somewhere
                primaryStage.setScene(WaitingAfterQuestion.change(primaryStage, frontEnd));
            }
        });
        HBox questionTextAreaButtonBox = new HBox(questionTextAreaButton);


        questionTextArea.getChildren().addAll(questionTextAreaQuestion, questionTextAreaInputBox, questionTextAreaButtonBox);

        questionTextAreaQuestion.setAlignment(Pos.CENTER);
        questionTextAreaInputBox.setAlignment(Pos.CENTER);
        questionTextAreaButtonBox.setAlignment(Pos.CENTER);
        questionTextArea.setAlignment(Pos.CENTER);

        questionTextAreaRoot.setCenter(questionTextArea);

        return questionTextAreaScreen;
    }


}
