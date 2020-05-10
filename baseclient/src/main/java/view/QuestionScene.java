package view;

import baseclient.BaseClient;
import baseclient.ClientType;
import command.Command;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import question.Answer;
import question.AnswerType;
import question.Question;
import style.Styles;

import java.util.ArrayList;
import java.util.List;

import static baseclient.BaseClientBackEnd.addCommandToBackEnd;

public class QuestionScene extends Scene {

    static final Logger LOG = LoggerFactory.getLogger(QuestionScene.class);

    public QuestionScene(BaseClient baseClient, Question question, boolean asHost) {
        super(new ScrollPane(), baseClient.getWidth(), baseClient.getHeight());

        double width = baseClient.getWidth();
        double height = baseClient.getHeight();

        Styles style = new Styles(width, height);

        BorderPane questionViewRoot = style.getStandardBorderPane();
        Label questionLabel = style.getQuestionLabel(question.getQuestion(), 8d/10, 4d/10);

        BorderPane.setAlignment(questionLabel, Pos.CENTER);
        questionViewRoot.setTop(questionLabel);

        if (!asHost) {
            AnswerType answerType = question.getAnswerType();

            switch (answerType) {
                case CHOICE:
                    // Add the scrollable answers.
                    ScrollPane answersListScrollable = style.getStandardScrollPane();
                    answersListScrollable.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

                    VBox answersList = style.getStandardVBox();
                    for (Answer answer : question.getAnswerList()) {

                        Button answerButton = style.getAnswerButton(answer.getAnswerText(), 8d/10, 3d/15);
                        if (baseClient.getType() == ClientType.PLAYER) {
                            answerButton.setOnMouseReleased(mouseEvent -> {
                                addCommandToBackEnd(203, new String[] {Long.toString(question.getQuestionID()), Long.toString(answer.getAnswerID())}, 0);
                                baseClient.setGuiStage(new WaitingAfterQuestionScreen(baseClient, asHost));

                            });
                        }

                        answersList.getChildren().add(answerButton);
                    }
                    HBox answersPanel = style.getStandardHBox();
                    Region leftRegion = new Region();
                    Region rightRegion = new Region();
                    HBox.setHgrow(leftRegion, Priority.ALWAYS);
                    HBox.setHgrow(rightRegion, Priority.ALWAYS);

                    answersListScrollable.setContent(answersList);
                    answersPanel.getChildren().addAll(leftRegion, answersListScrollable, rightRegion);
                    questionViewRoot.setCenter(answersPanel);
                    break;
                case FREEFORM:
                    // Show the free text box.
                    VBox answerArea = style.getStandardVBox();
                    TextField answerField = style.getAnswerTextField("Answer here...", 8d/10, 4d/10);
                    VBox.setMargin(answerField, style.getTopMargin());
                    Button submitButton = style.getRegularButton("Submit", 8d/10, 1d/10);
                    submitButton.setOnMouseReleased(mouseEvent -> {
                        addCommandToBackEnd(203, new String[] {Long.toString(question.getQuestionID()), answerField.getText()}, 0);
                        baseClient.setGuiStage(new WaitingAfterQuestionScreen(baseClient, asHost));
                    });

                    answerField.setAlignment(Pos.CENTER);
                    submitButton.setAlignment(Pos.CENTER);

                    HBox answerPanel = style.getStandardHBox();
                    Region leftPadding = new Region();
                    Region rightPadding = new Region();
                    HBox.setHgrow(leftPadding, Priority.ALWAYS);
                    HBox.setHgrow(rightPadding, Priority.ALWAYS);

                    answerArea.getChildren().addAll(answerField, submitButton);
                    answerPanel.getChildren().addAll(leftPadding, answerArea, rightPadding);
                    BorderPane.setAlignment(answerPanel, Pos.CENTER);
                    questionViewRoot.setCenter(answerPanel);
                    break;
            }
        }


        super.setRoot(questionViewRoot);

//        switch (questionType){
//            case IMAGE:
//                try {
//                    ImageView imageView = new ImageView(new Image(new FileInputStream(question.getMediaPath())));
//                    HBox imageBox = new HBox(imageView);
//                    imageBox.setPadding(new Insets(15));
//                    imageBox.setAlignment(Pos.CENTER);
//
//                    imageView.setPreserveRatio(true);
//                    imageView.fitWidthProperty().bind(imageBox.widthProperty());
//
//                    mainBox.getChildren().add(imageBox);
//                } catch (FileNotFoundException e) {
//                    // Handle this situation.
//                    LOG.error("File not found.");
//                }
//                break;
//            case AUDIO:
//                // play music
//                break;
//            case VIDEO:
//                // play video
//                Rectangle videoBox = new Rectangle(300,200);
//                mainBox.getChildren().add(videoBox);
//                break;
//        }

        // add answer GUI
//        AnswerType answerType = question.getAnswerType();
//        if (answerType == AnswerType.CHOICE) addMultipleChoiceAnswer(mainBox, question.getAnswerList(), baseClient);
//        if (answerType == AnswerType.FREEFORM) addFreeFormAnswer(mainBox, question.getAnswerList(), baseClient);
//
//        scroller.setContent(mainBox);

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
