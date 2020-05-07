package baseclient;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

public class WaitingAfterQuestionScreen extends Scene {

    public WaitingAfterQuestionScreen(BaseClient baseClient) {
        super(new BorderPane(), baseClient.getWidth(), baseClient.getHeight());

        double width = baseClient.getWidth();
        double height = baseClient.getHeight();

        //After answering question, player wait until others answered too.
        BorderPane waitingAfterQuestionRoot = new BorderPane();
        final VBox waitingAfterQuestion = new VBox(10);
        waitingAfterQuestion.setStyle("-fx-background-color: ROYALBLUE;");

        Label waitingAfterQuestionText = new Label("Let's wait others");

        waitingAfterQuestion.setAlignment(Pos.CENTER);

        waitingAfterQuestion.getChildren().addAll(waitingAfterQuestionText);

        waitingAfterQuestionRoot.setCenter(waitingAfterQuestion);

        super.setRoot(waitingAfterQuestionRoot);
    }
}
