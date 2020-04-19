package baseclient;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

public class LobbyFX {

    static final Logger LOG = LoggerFactory.getLogger(LobbyFX.class);

    private static final Font textFont = Font.font("Berlin Sans FB Demi", 20);
    private static ObservableList<String> participantsList;

    public static Scene change(BaseClient frontEnd, boolean asHost, String lobbyCode, String[] names) {
        double width = frontEnd.getWidth();
        double height = frontEnd.getHeight();

        //Lobby scene, where player is waiting for start.
        BorderPane lobbyRoot = new BorderPane();
        final Scene lobbyScreen = new Scene(lobbyRoot, width, height);
        final VBox lobby = new VBox(10);
        lobby.setStyle("-fx-background-color: ROYALBLUE; -fx-font-family: Berlin Sans FB Demi");

        Label lobbyStart = new Label("THE GAME WILL START SOON ;)");
        lobbyStart.setFont(textFont);

        Label lobbyCodeLabel = new Label("" + lobbyCode);
        lobbyCodeLabel.setFont(textFont);

        Label lobbyPlayers = new Label("Players that are already ready:");
        System.out.println(Font.getFamilies());

        //How to update, when new players join?
        participantsList = FXCollections.observableArrayList(names);
        final ArrayList<Label> participantsLabels = new ArrayList<>();
        for (String participant : participantsList) {
            participantsLabels.add(new Label(participant));
        }

        participantsList.addListener((ListChangeListener<String>) observable -> {
            lobby.getChildren().removeAll(participantsLabels);
            participantsLabels.clear();
            for (String participant : participantsList) {
                participantsLabels.add(new Label(participant));
            }
            lobby.getChildren().addAll(participantsLabels);
        });

        Button startGame = new Button("Start game");

        // Add event listener.
        startGame.setOnMouseReleased(mouseEvent -> {
            BaseClientBackEnd.addCommandToBackEnd(139, new String[0], 0);
        });

        lobby.setAlignment(Pos.CENTER);
        lobby.getChildren().addAll(lobbyStart, lobbyCodeLabel, lobbyPlayers);
        lobby.getChildren().addAll(participantsLabels);

        if (asHost) {
            lobby.getChildren().add(startGame);
        }

        lobbyRoot.setTop(lobby);

        return lobbyScreen;
    }

    public static void updateParticipantsList(String[] list) {
        participantsList.clear();
        participantsList.addAll(list);
        LOG.debug("Participants list updated.");
    }
}
