package baseclient;

import general.questions.AnswerType;
import general.questions.Question;
import general.questions.QuestionType;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;

public class QuestionScene {

    public static Scene change(BaseClient frontEnd, Question question){

        Label questionText = new Label("question.getQuestion() võiks olla siin");
        questionText.setWrapText(true);
        HBox questionBox = new HBox(questionText);
        questionBox.setPadding(new Insets(15));
        questionBox.setAlignment(Pos.CENTER);

        VBox mainBox = new VBox(100);
        mainBox.getChildren().add(questionBox);

        QuestionType questionType = QuestionType.TEXT;

        switch (questionType){
            case IMAGE:
                Rectangle imageBox = new Rectangle(300,200);
                mainBox.getChildren().add(imageBox);
            case AUDIO:
                // play music
            case VIDEO:
                // play video
                Rectangle videoBox = new Rectangle(300,200);
                mainBox.getChildren().add(videoBox);
        }

        // add answer GUI
        AnswerType answerType = question.getAnswerType();
        if (answerType == AnswerType.CHOICE) ChoiceAnswerFX.addAnswerGraphics(mainBox, question.getAnswerList(), frontEnd);
        if (answerType == AnswerType.FREEFORM) FreeformAnswerFX.addAnswerGraphics(mainBox, question.getAnswerList(), frontEnd);

        return new Scene(mainBox, frontEnd.getWidth(), frontEnd.getHeight());
    }
}
