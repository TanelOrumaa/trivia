package view;

import baseclient.BaseClient;
import baseclient.ClientType;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import style.Styles;

import static baseclient.BaseClientBackEnd.addCommandToBackEnd;

public class LobbyEntry extends Scene {

    public LobbyEntry(BaseClient baseClient) {
        super(new BorderPane(), baseClient.getWidth(), baseClient.getHeight());

        double width = baseClient.getWidth();
        double height = baseClient.getHeight();

        Styles style = new Styles(width, height);

        BorderPane lobbyEntryRoot = style.getStandardBorderPane();
        HBox lobbyEntryPanel = style.getStandardHBox();
        VBox lobbyEntry = style.getStandardVBox();

        TextField lobbyEntryCodeInput = style.getRegularTextField("Enter lobby code", 8d/10, 2d/15);
        Button lobbyEntryButton = style.getRegularButton("Join", 8d/10, 1d/10);
        //Event handler: entering code to join a lobby. Check if the lobby with this code exists.
        lobbyEntryButton.setOnAction(actionEvent -> {
            addCommandToBackEnd(131, new String[]{lobbyEntryCodeInput.getText()}, 0);
        });

        lobbyEntryCodeInput.setAlignment(Pos.CENTER);

        if (baseClient.getType() == ClientType.PLAYER) {
            Button backButton = style.getNeutralButton("Back", 8d / 10, 1d / 10);
            backButton.setOnMouseReleased(mouseEvent -> {
                baseClient.setGuiStage(new UserMainPage(baseClient));
            });
            lobbyEntry.getChildren().addAll(lobbyEntryCodeInput, lobbyEntryButton, backButton);
        } else {
            lobbyEntry.getChildren().addAll(lobbyEntryCodeInput, lobbyEntryButton);
        }

        lobbyEntryPanel.setAlignment(Pos.CENTER);
        lobbyEntryPanel.getChildren().addAll(lobbyEntry);

        if (baseClient.getType() == ClientType.PLAYER) {
            // Header
            HBox header = style.getHeader(baseClient);
            lobbyEntryRoot.setTop(header);
        }

        lobbyEntryRoot.setCenter(lobbyEntryPanel);

        super.setRoot(lobbyEntryRoot);
    }


}
