package view;

import baseclient.BaseClient;
import baseclient.BaseClientBackEnd;
import baseclient.ClientType;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import style.Styles;

public class LobbyFX extends Scene {

    static final Logger LOG = LoggerFactory.getLogger(LobbyFX.class);

    private static ObservableList<String> participantsList;

    private static Button startGame;
    Label participantsLabel;

    public LobbyFX(BaseClient baseClient, boolean asHost, String lobbyCode, String[] names) {
        super(new BorderPane(), baseClient.getWidth(), baseClient.getHeight());


        double width = baseClient.getWidth();
        double height = baseClient.getHeight();
        System.out.println(width + ", " + height);

        Styles style = new Styles(width, height);



        // Lobby scene, where player is waiting for game start.
        BorderPane lobbyRoot = style.getStandardBorderPane();
        final VBox lobby = style.getStandardVBox();

        Label lobbyStart = style.getH1TitleLabel(new String[] {"The game will ", "start soon!"}, 8d/10, 2d/15);
        lobbyStart.setAlignment(Pos.CENTER);

        Label lobbyPlayers = style.getH4TitleLabel(new String[] {"Players already in lobby:"}, 8d/10, 1d/10);
        lobbyPlayers.setAlignment(Pos.CENTER);

        participantsList = FXCollections.observableArrayList(names);

        participantsLabel = new Label();
        updateParticipantsLabel(style);

        participantsLabel.setAlignment(Pos.CENTER);

        participantsList.addListener((ListChangeListener<String>) observable -> {
            lobby.getChildren().remove(participantsLabel);
            updateParticipantsLabel(style);
            participantsLabel.setAlignment(Pos.CENTER);
            lobby.getChildren().addAll(participantsLabel);
        });

        lobby.setAlignment(Pos.CENTER);

        if (baseClient.getType() == ClientType.PRESENTER || asHost) {
            Label lobbyCodeLabelText = style.getH4TitleLabel(new String[] {"Join this game with code:"}, 8d / 10, 1d / 10);
            Label lobbyCodeLabel = style.getH1TitleLabel(new String[] {lobbyCode}, 8d / 10, 1d / 10);
            lobbyCodeLabelText.setAlignment(Pos.CENTER);
            lobbyCodeLabel.setAlignment(Pos.CENTER);
            lobby.getChildren().addAll(lobbyStart, lobbyCodeLabelText, lobbyCodeLabel, lobbyPlayers);
        } else {
            lobby.getChildren().addAll(lobbyStart, lobbyPlayers);
        }

        lobby.getChildren().addAll(participantsLabel);

        VBox buttonsArea = style.getStandardVBox();

        if (baseClient.getType() == ClientType.PLAYER && asHost) {
            startGame = style.getRegularButton("Start game", 8d/10, 1d/10);

            startGame.setDisable(true);

            // Add event listener.
            startGame.setOnMouseReleased(mouseEvent -> {
                BaseClientBackEnd.addCommandToBackEnd(139, new String[0], 0);
            });

            buttonsArea.getChildren().add(startGame);
        }

        Button leaveLobby = style.getNeutralButton("Leave lobby", 8d/10, 1d/10);
        leaveLobby.setOnMouseReleased(mouseEvent -> {
            BaseClientBackEnd.addCommandToBackEnd(191, new String[] {lobbyCode}, 0);
            baseClient.setGuiStage(new LobbyEntry(baseClient));
        });
        buttonsArea.getChildren().add(leaveLobby);

        // Alignment.
        buttonsArea.setAlignment(Pos.CENTER);
        lobby.setAlignment(Pos.TOP_CENTER);

        // Margins for footer.
        BorderPane.setMargin(buttonsArea, style.getThinTopAndBottomMargin());

        // Header
        if (baseClient.getType() == ClientType.PLAYER) {
            HBox header = style.getHeader(baseClient);
            lobbyRoot.setTop(header);
        }

        lobbyRoot.setCenter(lobby);
        lobbyRoot.setBottom(buttonsArea);

        super.setRoot(lobbyRoot);
    }

    public static void updateParticipantsList(String[] list) {
        participantsList.clear();
        participantsList.addAll(list);
        LOG.debug("Participants list updated.");

        // There has to be at least 2 players in addition to lobby host.
        allowStartGame(participantsList.size() >= 2); //TODO: Should be 3
    }

    private void updateParticipantsLabel(Styles style) {
        participantsLabel = style.getH4TitleLabel(participantsList.toArray(String[]::new), 8d/10, participantsList.size()/6d);
    }

    public static void allowStartGame(boolean allowStart) {
        if (startGame != null) {
            startGame.setDisable(!allowStart);
        }

    }
}
