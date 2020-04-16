package general.baseclient;

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

import static general.baseclient.BaseClientBackEnd.addCommand;

public class LobbyEntry {

    public static Scene change(Stage primaryStage, BaseClient frontEnd) {

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
        lobbyEntryButton.setOnAction(new EventHandler<ActionEvent>() {
            //Event handler: entering code to join a lobby. Check if the lobby with this code exists.
            @Override
            public void handle(ActionEvent actionEvent) {
                addCommand(131, new String[] {lobbyEntryCodeInput.getText()}, 0);
                //if exists lobby with this code
//                if (true) {
//                    primaryStage.setScene(LobbyFX.change(primaryStage, frontEnd));
//                } else {
//                    lobbyEntryError.setText("Something went wrong");
//                }
            }
        });

        Button lobbyCreationButton = new Button("Create lobby");
        lobbyCreationButton.setOnMouseReleased(mouseEvent -> {
            addCommand(133, new String[] {"Test lobby name"}, 0);
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
