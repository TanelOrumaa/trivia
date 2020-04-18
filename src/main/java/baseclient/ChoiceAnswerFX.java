package baseclient;

import general.questions.Answer;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;

import static baseclient.BaseClientBackEnd.addCommand;

public class ChoiceAnswerFX {

    public static void addAnswerGraphics(VBox mainBox, List<Answer> answers, BaseClient frontEnd) {

        //Multiple-choice question. Player choose one correct answer.
        ScrollPane choiceAnswerScroller = new ScrollPane();
        choiceAnswerScroller.setFitToWidth(true);


        ArrayList<Button> answerButtons = new ArrayList<>();
        for (int i = 0; i < answers.size(); i++) {
            Button answerButton = new Button(answers.get(i).getAnswerText());
            answerButton.setPrefSize(frontEnd.getWidth() / 4 * 3, frontEnd.getHeight() / 10);

            final String answerNumber = Integer.toString(i);
            answerButton.setOnAction(actionEvent -> {
                // Inform back-end about user's answer
                addCommand(201, new String[] {answerNumber}, 0);
            });
            answerButtons.add(answerButton);
        }

        VBox answerBox = new VBox();
        answerButtons.forEach((button -> answerBox.getChildren().add(button)));

        choiceAnswerScroller.setContent(answerBox);

        mainBox.getChildren().add(choiceAnswerScroller);
    }


}
