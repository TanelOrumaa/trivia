package baseclient;

import general.questions.Answer;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;

import static baseclient.BaseClientBackEnd.addCommandToBackEnd;

public class ChoiceAnswerFX {

    public static void addAnswerGraphics(VBox mainBox, List<Answer> answers, BaseClient frontEnd) {

        //Multiple-choice question. Player choose one correct answer.
               ArrayList<Button> answerButtons = new ArrayList<>();
        for (int i = 0; i < answers.size(); i++) {
            Button answerButton = new Button(answers.get(i).getAnswerText());
            answerButton.setPrefSize(frontEnd.getWidth() / 4 * 3, frontEnd.getHeight() / 10);

            final String answerNumber = Integer.toString(i);
            answerButton.setOnAction(actionEvent -> {
                // Inform back-end about user's answer
                addCommandToBackEnd(201, new String[] {answerNumber}, 0);
            });
            answerButtons.add(answerButton);
        }

        VBox answerBox = new VBox();
        answerButtons.forEach((button -> answerBox.getChildren().add(button)));

        mainBox.getChildren().add(answerBox);
    }


}
