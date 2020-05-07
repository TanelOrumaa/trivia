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

import static baseclient.BaseClientBackEnd.addCommandToBackEnd;

public class UserMainPage extends Scene {

    private static final Font textFont = Font.font("Berlin Sans FB Demi", 20);

    public UserMainPage(BaseClient baseClient) {
        super(new BorderPane(), baseClient.getWidth(), baseClient.getHeight());
        double width = baseClient.getWidth();
        double height = baseClient.getHeight();

        //It is a scene, where player has to enter code to join a lobby.
        BorderPane userMainPageRoot = new BorderPane();
        VBox userMainPage = new VBox(20);
        userMainPage.setStyle("-fx-background-color: ROYALBLUE;");


        final Label usernameLabel = new Label(baseClient.getUsername());
        usernameLabel.setFont(textFont);

        HBox usernameLabelArea = new HBox(usernameLabel);
        usernameLabelArea.setAlignment(Pos.TOP_RIGHT);
        usernameLabelArea.setPadding(new Insets(width * 0.05, width * 0.05, width * 0.05, width * 0.05));

        Button joinGameButton = new Button("Join a game");
        // user.User wants to join a game, display view.LobbyEntry scene.
        joinGameButton.setOnMouseReleased(actionEvent -> {
            baseClient.setGuiStage(new LobbyEntry(baseClient));
        });


        Button hostGameButton = new Button("Host a game");
        hostGameButton.setOnMouseReleased(mouseEvent -> {
            // Fetch triviasets from server.
            addCommandToBackEnd(211, new String[0], 0);
            // Display the triviaset page.
            baseClient.setGuiStage(new UserTriviasetPage(baseClient));
        });

        joinGameButton.setPadding(new Insets(width * 0.02));
        hostGameButton.setPadding(new Insets(width * 0.02));
        VBox.setMargin(hostGameButton, new Insets(width * 0.05));
        VBox.setMargin(joinGameButton, new Insets(width * 0.05));

        joinGameButton.setFont(textFont);
        hostGameButton.setFont(textFont);

        VBox buttonsArea = new VBox(joinGameButton, hostGameButton);
        buttonsArea.setAlignment(Pos.CENTER);

        userMainPage.getChildren().addAll(usernameLabelArea, buttonsArea);

        userMainPageRoot.setCenter(userMainPage);

        super.setRoot(userMainPageRoot);
    }


}
