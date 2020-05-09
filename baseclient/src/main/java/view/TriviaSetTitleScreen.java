package view;

import baseclient.BaseClient;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import popup.ErrorMessage;
import triviaset.TriviaSet;

import java.util.ArrayList;

public class TriviaSetTitleScreen extends Scene {

    private static final Font textFont = Font.font("Berlin Sans FB Demi", 20);

    public TriviaSetTitleScreen(BaseClient baseClient) {
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

        Label titleLabel = new Label("Enter trivia set title");
        TextField titleInput = new TextField("");

        Button nextButton = new Button("Next");
        nextButton.setOnMouseReleased(mouseEvent -> {
            //Start adding questions.
            if (titleInput.getText().length() > 2) {
                baseClient.setGuiStage(new TriviaSetAddQuestion(baseClient, new TriviaSet(-1, titleInput.getText(), new ArrayList<>())));
            } else {
                ErrorMessage.popUp("Title should be longer than 2 characters");
            }
        });

        Button backButton = new Button("Back");
        backButton.setOnMouseReleased(mouseEvent -> {
            //Go back to previous scene.
            baseClient.setGuiStage(new TriviaSetMenu(baseClient));
        });

        VBox buttonsArea = new VBox(titleLabel, titleInput, nextButton, backButton);

        mainBox.getChildren().addAll(usernameLabelArea, buttonsArea);

        root.setCenter(mainBox);

        super.setRoot(root);

    }
}
