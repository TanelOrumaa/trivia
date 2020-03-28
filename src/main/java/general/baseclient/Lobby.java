package general.baseclient;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.util.ArrayList;

public class Lobby {

    public static Scene change(Stage primaryStage, BaseClient frontEnd) {

        double width = frontEnd.getWidth();
        double height = frontEnd.getHeight();

        //Lobby scene, where player is waiting for start.
        BorderPane lobbyRoot = new BorderPane();
        final Scene lobbyScreen = new Scene(lobbyRoot, width, height);
        final VBox lobby = new VBox(10);
        lobby.setStyle("-fx-background-color: ROYALBLUE; -fx-font-family: Berlin Sans FB Demi");

        Label lobbyStart = new Label("THE GAME WILL START SOON ;)");
        lobbyStart.setFont(Font.font("Berlin Sans FB Demi", 20));

        Label lobbyPlayers = new Label("Players that are already ready:");
        System.out.println(javafx.scene.text.Font.getFamilies());

        //How to update, when new players join?
        final ObservableList<String> participants = FXCollections.observableArrayList("Taavi", "Mauno", "Gunnar", "Kaupo");
        final ArrayList<Label> participantsLabels = new ArrayList<Label>();
        for (String participant : participants) {
            participantsLabels.add(new Label(participant));
        }

        participants.addListener(new ListChangeListener<String>() {
            public void onChanged(Change observable) {
                lobby.getChildren().removeAll(participantsLabels);
                participantsLabels.clear();
                for (String participant : participants) {
                    participantsLabels.add(new Label(participant));
                }
                lobby.getChildren().addAll(participantsLabels);
            }
        });

        lobby.setAlignment(Pos.CENTER);
        lobby.getChildren().addAll(lobbyStart, lobbyPlayers);
        lobby.getChildren().addAll(participantsLabels);

        lobbyRoot.setCenter(lobby);

        return lobbyScreen;
    }
}
