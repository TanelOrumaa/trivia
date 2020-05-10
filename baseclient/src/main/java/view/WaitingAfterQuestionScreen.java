package view;

import baseclient.BaseClient;
import baseclient.BaseClientBackEnd;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import style.Styles;

public class WaitingAfterQuestionScreen extends Scene {

    public WaitingAfterQuestionScreen(BaseClient baseClient, boolean asHost, long nextQuestionId) {
        super(new BorderPane(), baseClient.getWidth(), baseClient.getHeight());

        double width = baseClient.getWidth();
        double height = baseClient.getHeight();

        Styles style = new Styles(width, height);
        //After answering question, player wait until others answered too.
        BorderPane waitingAfterQuestionRoot = style.getStandardBorderPane();


        if (asHost) {
            Button nextQuestion = style.getRegularButton("Next question", 8d/10, 1d/10);
            nextQuestion.setOnMouseReleased(mouseEvent -> {
                BaseClientBackEnd.addCommandToBackEnd(145, new String[] {Long.toString(nextQuestionId)}, 0);
            });
            waitingAfterQuestionRoot.setCenter(nextQuestion);
        } else {
            Label waitingText = style.getH2TitleLabel(new String[] {"Great answer!", "(probably)", "", "Next question", "coming soon."}, 8d/10, 5d/10);

            waitingAfterQuestionRoot.setCenter(waitingText);

        }




        super.setRoot(waitingAfterQuestionRoot);
    }
}
