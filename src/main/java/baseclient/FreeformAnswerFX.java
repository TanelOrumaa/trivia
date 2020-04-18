package baseclient;

import general.commands.Command;
import general.questions.Answer;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.List;

public class FreeformAnswerFX {

    public static void addAnswerGraphics(VBox mainBox, List<Answer> answers, BaseClient frontEnd) {

        VBox answerTextArea = new VBox(20);

        TextArea answerTextAreaInput = new TextArea();
        answerTextAreaInput.setPrefSize(frontEnd.getWidth() / 4 * 3,frontEnd.getHeight() / 5);
        answerTextAreaInput.setWrapText(true);
        HBox answerTextAreaInputBox = new HBox(answerTextAreaInput);

        Button answerButton = new Button("Answer");
        answerButton.setOnAction(actionEvent -> {
            // generate command - inform backend that client has answered and what the answer was - and and invoke it
            Command buttonPressedCommand = new Command(201, new String[] {answerTextAreaInput.getText()});
            frontEnd.addCommandToFrontEnd(buttonPressedCommand);
        });
        HBox answerTextAreaButtonBox = new HBox(answerButton);


        mainBox.getChildren().addAll(answerTextAreaInputBox, answerTextAreaButtonBox);

        answerTextAreaInputBox.setAlignment(Pos.CENTER);
        answerTextAreaButtonBox.setAlignment(Pos.CENTER);
        answerTextArea.setAlignment(Pos.CENTER);

    }

}
