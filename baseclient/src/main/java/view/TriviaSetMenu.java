package view;

import baseclient.BaseClient;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;


public class TriviaSetMenu extends Scene {

    private static final Font textFont = Font.font("Berlin Sans FB Demi", 20);

    public TriviaSetMenu (BaseClient baseClient) {
        super(new BorderPane(), baseClient.getWidth(), baseClient.getHeight());
        double width = baseClient.getWidth();
        double height = baseClient.getHeight();

        BorderPane triviaSetMenuRoot = new BorderPane();
        VBox triviaSetMenu = new VBox(20);
        triviaSetMenu.setStyle("-fx-background-color: ROYALBLUE;");

        final Label usernameLabel = new Label(baseClient.getUsername());
        usernameLabel.setFont(textFont);

        HBox usernameLabelArea = new HBox(usernameLabel);
        usernameLabelArea.setAlignment(Pos.TOP_RIGHT);
        usernameLabelArea.setPadding(new Insets(width * 0.05, width * 0.05, width * 0.05, width * 0.05));

        Button myTriviaSetsButton = new Button("My trivia sets");
        myTriviaSetsButton.setOnMouseReleased(mouseEvent -> {
            // Display list of triviasets.
            //baseClient.setGuiStage(new (baseClient));
        });

        Button newTriviaSetButton = new Button("Create new trivia set");
        newTriviaSetButton.setOnMouseReleased(mouseEvent -> {
            // Display triviaset menu.
            baseClient.setGuiStage(new TriviaSetTitleScreen(baseClient));
        });

        Button backButton = new Button("Back");
        backButton.setOnMouseReleased(mouseEvent -> {
            //Go back to previous scene.
            baseClient.setGuiStage(new UserMainPage(baseClient));
        });



        VBox.setMargin(newTriviaSetButton, new Insets(width * 0.05));
        VBox.setMargin(backButton, new Insets(width * 0.05));

        newTriviaSetButton.setFont(textFont);
        backButton.setFont(textFont);

        VBox buttonsArea = new VBox(myTriviaSetsButton, newTriviaSetButton, backButton);
        buttonsArea.setAlignment(Pos.CENTER);

        triviaSetMenu.getChildren().addAll(usernameLabelArea, buttonsArea);

        triviaSetMenuRoot.setCenter(triviaSetMenu);

        super.setRoot(triviaSetMenuRoot);


    }

}
