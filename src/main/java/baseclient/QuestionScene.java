package baseclient;

import general.questions.AnswerType;
import general.questions.Question;
import general.questions.QuestionType;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import server.PlayerClientConnection;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class QuestionScene {

    static final Logger LOG = LoggerFactory.getLogger(PlayerClientConnection.class);

    public static Scene change(BaseClient frontEnd, Question question) {

        ScrollPane scroller = new ScrollPane();
        scroller.setFitToWidth(true);

        VBox mainBox = new VBox(20);
        mainBox.setAlignment(Pos.CENTER);

        Label questionText = new Label(question.getQuestion());
        questionText.setWrapText(true);
        HBox questionTextBox = new HBox(questionText);
        questionTextBox.setPadding(new Insets(15));
        questionTextBox.setAlignment(Pos.CENTER);

        mainBox.getChildren().add(questionTextBox);

        QuestionType questionType = question.getQuestionType();

        switch (questionType){
            case IMAGE:
                try {
                    ImageView imageView = new ImageView(new Image(new FileInputStream(question.getMediaPath())));
                    HBox imageBox = new HBox(imageView);
                    imageBox.setPadding(new Insets(15));
                    imageBox.setAlignment(Pos.CENTER);

                    imageView.setPreserveRatio(true);
                    imageView.fitWidthProperty().bind(imageBox.widthProperty());

                    mainBox.getChildren().add(imageBox);
                } catch (FileNotFoundException e) {
                    // Handle this situation.
                    LOG.error("File not found.");
                }
                break;
            case AUDIO:
                // play music
                break;
            case VIDEO:
                // play video
                Rectangle videoBox = new Rectangle(300,200);
                mainBox.getChildren().add(videoBox);
                break;
        }

        // add answer GUI
        AnswerType answerType = question.getAnswerType();
        if (answerType == AnswerType.CHOICE) ChoiceAnswerFX.addAnswerGraphics(mainBox, question.getAnswerList(), frontEnd);
        if (answerType == AnswerType.FREEFORM) FreeformAnswerFX.addAnswerGraphics(mainBox, question.getAnswerList(), frontEnd);

        scroller.setContent(mainBox);

        return new Scene(scroller, frontEnd.getWidth(), frontEnd.getHeight());
    }
}
