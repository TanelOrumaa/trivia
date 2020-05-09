package view;

import baseclient.BaseClient;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import triviaset.TriviaSet;
import triviaset.TriviaSetSerializerFull;

import static baseclient.BaseClientBackEnd.addCommandToBackEnd;

public class TriviaSetFinalOverview extends Scene {

    private static final Font textFont = Font.font("Berlin Sans FB Demi", 20);

    public TriviaSetFinalOverview(BaseClient baseClient, TriviaSet triviaSet) {
        super(new BorderPane(), baseClient.getWidth(), baseClient.getHeight());
        double width = baseClient.getWidth();
        double height = baseClient.getHeight();

        BorderPane root = new BorderPane();
        VBox mainBox = new VBox(20);
        mainBox.setStyle("-fx-background-color: ROYALBLUE;");

        final Label usernameLabel = new Label(baseClient.getUsername());
        usernameLabel.setFont(textFont);

        HBox usernameLabelArea = new HBox(usernameLabel);
        usernameLabelArea.setAlignment(Pos.TOP_RIGHT);
        usernameLabelArea.setPadding(new Insets(width * 0.05, width * 0.05, width * 0.05, width * 0.05));


        VBox questionsBox = new VBox();
        triviaSet.getQuestionMap().forEach(((integer, question) -> {
            questionsBox.getChildren().add(new Label(integer + ". " + question.getQuestion()));
        }));

        ScrollPane questionPane = new ScrollPane();
        questionPane.setContent(questionsBox);

        Button finishButton = new Button("Finish adding questions");
        finishButton.setOnMouseReleased(mouseEvent -> {
            //Finish adding questions.
            Gson gson = new GsonBuilder().registerTypeAdapter(TriviaSet.class, new TriviaSetSerializerFull()).create();
            String triviaSetAsJson = gson.toJson(triviaSet);
            addCommandToBackEnd(215, new String[]{triviaSetAsJson}, 0);
        });

        mainBox.getChildren().addAll(usernameLabelArea, questionPane, finishButton);
        root.setCenter(mainBox);

        super.setRoot(root);
    }
}
