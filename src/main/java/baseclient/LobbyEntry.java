package baseclient;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import static baseclient.BaseClientBackEnd.addCommandToBackEnd;

public class LobbyEntry {

    public static Scene change(BaseClient frontEnd) {

        double width = frontEnd.getWidth();
        double height = frontEnd.getHeight();

        //It is a scene, where player has to enter code to join a lobby.
        BorderPane lobbyEntryRoot = new BorderPane();
        final Scene lobbyEntryScreen = new Scene(lobbyEntryRoot, width, height);
        VBox lobbyEntry = new VBox(20);
        lobbyEntry.setStyle("-fx-background-color: ROYALBLUE;");

        final Label lobbyEntryError = new Label("");

        final TextField lobbyEntryCodeInput = new TextField("Enter the code");
        HBox lobbyEntryCode = new HBox(lobbyEntryCodeInput);


        Button lobbyEntryButton = new Button("Enter lobby");
        //Event handler: entering code to join a lobby. Check if the lobby with this code exists.
        lobbyEntryButton.setOnAction(actionEvent -> {
            addCommandToBackEnd(131, new String[]{lobbyEntryCodeInput.getText()}, 0);
        });


        HBox lobbyEntryButtons = new HBox(lobbyEntryButton);

        Button backButton = new Button("Back");
        backButton.setOnMouseReleased(mouseEvent -> {
            BaseClient.guiStage.setScene(UserMainPage.change(frontEnd));
        });

        HBox backButtons = new HBox(backButton);

        lobbyEntryButtons.setAlignment(Pos.CENTER);
        lobbyEntryCode.setAlignment(Pos.CENTER);
        lobbyEntry.setAlignment(Pos.CENTER);
        backButtons.setAlignment(Pos.CENTER);

        lobbyEntry.getChildren().addAll(lobbyEntryError, lobbyEntryCode, lobbyEntryButtons, backButtons);

        lobbyEntryRoot.setCenter(lobbyEntry);

        return lobbyEntryScreen;
    }


}
