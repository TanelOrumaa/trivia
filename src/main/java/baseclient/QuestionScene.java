package baseclient;

import general.commands.Command;
import general.questions.Answer;
import general.questions.AnswerType;
import general.questions.Question;
import general.questions.QuestionType;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import static baseclient.BaseClientBackEnd.addCommandToBackEnd;

public class QuestionScene extends Scene {

    static final Logger LOG = LoggerFactory.getLogger(QuestionScene.class);

    public QuestionScene(BaseClient baseClient, Question question) {
        super(new ScrollPane(), baseClient.getWidth(), baseClient.getHeight());


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
        if (answerType == AnswerType.CHOICE) addMultipleChoiceAnswer(mainBox, question.getAnswerList(), baseClient);
        if (answerType == AnswerType.FREEFORM) addFreeFormAnswer(mainBox, question.getAnswerList(), baseClient);

        scroller.setContent(mainBox);

        super.setRoot(scroller);
    }

    private static void addFreeFormAnswer(VBox mainBox, List<Answer> answers, BaseClient frontEnd) {

        VBox answerTextArea = new VBox(20);

        TextArea answerTextAreaInput = new TextArea();
        answerTextAreaInput.setPrefSize(frontEnd.getWidth() / 4 * 3,frontEnd.getHeight() / 5);
        answerTextAreaInput.setWrapText(true);
        HBox answerTextAreaInputBox = new HBox(answerTextAreaInput);
        // TODO: Answers missing?
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

    private static void addMultipleChoiceAnswer(VBox mainBox, List<Answer> answers, BaseClient frontEnd) {

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
