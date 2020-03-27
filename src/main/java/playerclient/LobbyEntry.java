package playerclient;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class LobbyEntry {

    public static Scene change(Stage primaryStage) {

        double width = PlayerClient.getWidth();
        double height = PlayerClient.getHeight();

        //It is a scene, where player has to enter code to join a lobby.
        BorderPane lobbyEntryRoot = new BorderPane();
        final Scene lobbyEntryScreen = new Scene(lobbyEntryRoot, width, height);
        VBox lobbyEntry = new VBox(20);
        lobbyEntry.setStyle("-fx-background-color: ROYALBLUE;");

        final Label lobbyEntryError = new Label("");

        final TextField lobbyEntryCodeInput = new TextField("Enter the code");
        HBox lobbyEntryCode = new HBox(lobbyEntryCodeInput);

        Button lobbyEntryButton = new Button("Enter to lobby");
        lobbyEntryButton.setOnAction(lobbyEntryButtonAction(lobbyEntryCodeInput.getText(), primaryStage, lobbyEntryError));
        HBox lobbyEntryButtons = new HBox(lobbyEntryButton);

        lobbyEntryButtons.setAlignment(Pos.CENTER);
        lobbyEntryCode.setAlignment(Pos.CENTER);
        lobbyEntry.setAlignment(Pos.CENTER);
        lobbyEntry.getChildren().addAll(lobbyEntryError, lobbyEntryCode, lobbyEntryButtons);

        lobbyEntryRoot.setCenter(lobbyEntry);

        return lobbyEntryScreen;
    }

    //Event handler: entering code to join a lobby. Check if the lobby with this code exists.
    static EventHandler<ActionEvent> lobbyEntryButtonAction(String lobbyCode, final Stage primaryStage, final Label lobbyEntryError) {
        return new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                //if exists lobby with this code
                if (true) {
                    primaryStage.setScene(Lobby.change(primaryStage));
                } else {
                    lobbyEntryError.setText("Something went wrong");
                }
            }
        };
    }

}
