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

        Button lobbyEntryButton = new Button("Enter to lobby");
        //Event handler: entering code to join a lobby. Check if the lobby with this code exists.
        lobbyEntryButton.setOnAction(actionEvent -> {
            addCommandToBackEnd(131, new String[]{lobbyEntryCodeInput.getText()}, 0);
            //if exists lobby with this code
//                if (true) {
//                    primaryStage.setScene(LobbyFX.change(primaryStage, frontEnd));
//                } else {
//                    lobbyEntryError.setText("Something went wrong");
//                }
        });

        Button lobbyCreationButton = new Button("Create lobby");
        lobbyCreationButton.setOnMouseReleased(mouseEvent -> {
            addCommandToBackEnd(133, new String[] {"Test lobby name"}, 0);
        });

        HBox lobbyEntryButtons = new HBox(lobbyEntryButton);

        lobbyEntryButtons.setAlignment(Pos.CENTER);
        lobbyEntryCode.setAlignment(Pos.CENTER);
        lobbyEntry.setAlignment(Pos.CENTER);
        lobbyEntry.getChildren().addAll(lobbyEntryError, lobbyEntryCode, lobbyEntryButtons, lobbyCreationButton);

        lobbyEntryRoot.setCenter(lobbyEntry);

        return lobbyEntryScreen;
    }


}
