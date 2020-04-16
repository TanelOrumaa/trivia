package general.baseclient;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class WaitingAfterQuestion {

    public static Scene change(Stage primaryStage, BaseClient frontEnd) {

        double width = frontEnd.getWidth();
        double height = frontEnd.getHeight();

        //After answering question, player wait until others answered too.
        BorderPane waitingAfterQuestionRoot = new BorderPane();
        final Scene waitingAfterQuestionScreen = new Scene(waitingAfterQuestionRoot, width, height);
        final VBox waitingAfterQuestion = new VBox(10);
        waitingAfterQuestion.setStyle("-fx-background-color: ROYALBLUE;");

        Label waitingAfterQuestionText = new Label("Let's wait others");

        waitingAfterQuestion.setAlignment(Pos.CENTER);

        waitingAfterQuestion.getChildren().addAll(waitingAfterQuestionText);

        waitingAfterQuestionRoot.setCenter(waitingAfterQuestion);

        return waitingAfterQuestionScreen;
    }
}
