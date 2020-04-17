package baseclient;

import general.Command;
import general.questions.Answers;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;

public class ChoiceAnswerFX {

    public static void addAnswerGraphics(VBox mainBox, List<String> answers, BaseClient frontEnd) {

        //Multiple-choice question. Player choose one correct answer.
        ScrollPane choiceAnswerScroller = new ScrollPane();
        choiceAnswerScroller.setFitToWidth(true);


        ArrayList<Button> answerButtons = new ArrayList<>();
        for (int i = 0; i < answers.size(); i++) {
            Button answerButton = new Button(answers.get(i));
            answerButton.setPrefSize(frontEnd.getWidth() / 4 * 3, frontEnd.getHeight() / 10);

            final String answerNumber = Integer.toString(i);
            answerButton.setOnAction(actionEvent -> {
                // generate command - inform backend which button was pressed - and and invoke it
                Command buttonPressedCommand = new Command(201, new String[] {answerNumber});
                frontEnd.addCommandAndInvoke(buttonPressedCommand);
            });
            answerButtons.add(answerButton);
        }

        VBox answerBox = new VBox();
        answerButtons.forEach((button -> answerBox.getChildren().add(button)));

        choiceAnswerScroller.setContent(answerBox);

        mainBox.getChildren().add(choiceAnswerScroller);
    }


}
