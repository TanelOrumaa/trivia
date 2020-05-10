package view;

import baseclient.BaseClient;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import style.Styles;

public class WaitingAfterQuestionScreen extends Scene {

    public WaitingAfterQuestionScreen(BaseClient baseClient, boolean asHost) {
        super(new BorderPane(), baseClient.getWidth(), baseClient.getHeight());

        double width = baseClient.getWidth();
        double height = baseClient.getHeight();

        Styles style = new Styles(width, height);
        //After answering question, player wait until others answered too.
        BorderPane waitingAfterQuestionRoot = style.getStandardBorderPane();

        Label waitingText = style.getH2TitleLabel(new String[] {"Great answer!", "(probably)", "", "Next question", "coming soon."}, 8d/10, 5d/10);

        waitingAfterQuestionRoot.setCenter(waitingText);


        super.setRoot(waitingAfterQuestionRoot);
    }
}
