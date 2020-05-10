package view;

import baseclient.BaseClient;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.*;
import style.Styles;

import static baseclient.BaseClientBackEnd.addCommandToBackEnd;

public class UserMainPage extends Scene {

    public UserMainPage(BaseClient baseClient) {
        super(new BorderPane(), baseClient.getWidth(), baseClient.getHeight());

        double width = baseClient.getWidth();
        double height = baseClient.getHeight();

        Styles style = new Styles(width, height);

        BorderPane userMainPageRoot = style.getStandardBorderPane();

        HBox header = style.getHeader(baseClient);

        Button joinGameButton = style.getRegularButton("Join a game", 8d/10, 1d/10);
        // User wants to join a game, display LobbyEntry scene.
        joinGameButton.setOnMouseReleased(actionEvent -> {
            baseClient.setGuiStage(new LobbyEntry(baseClient));
        });


        Button triviasetsButton = style.getRegularButton("My trivia sets", 8d/10, 1d/10);
        triviasetsButton.setOnMouseReleased(mouseEvent -> {
            // Fetch triviasets from server.
            addCommandToBackEnd(211, new String[0], 0);
            // Display the triviaset page.
            baseClient.setGuiStage(new UserTriviasetPage(baseClient));
        });


        Region topVerticalPadding = new Region();
        topVerticalPadding.setMaxHeight(height * 0.08);
        Region midVerticalPadding = new Region();
        midVerticalPadding.setMaxHeight(height * 0.08);
        Region bottomVerticalPadding = new Region();
        VBox.setVgrow(topVerticalPadding, Priority.ALWAYS);
        VBox.setVgrow(midVerticalPadding, Priority.ALWAYS);
        VBox.setVgrow(bottomVerticalPadding, Priority.ALWAYS);

        VBox buttonsArea = new VBox();
        buttonsArea.getChildren().addAll(topVerticalPadding, joinGameButton, midVerticalPadding, triviasetsButton, bottomVerticalPadding);

        buttonsArea.setAlignment(Pos.CENTER);

        userMainPageRoot.setTop(header);
        userMainPageRoot.setCenter(buttonsArea);

        super.setRoot(userMainPageRoot);
    }


}
