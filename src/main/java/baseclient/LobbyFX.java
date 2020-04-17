package baseclient;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.util.ArrayList;

public class LobbyFX {

    private static final Font textFont = Font.font("Berlin Sans FB Demi", 20);

    public static Scene change(BaseClient frontEnd, String[] names) {
        double width = frontEnd.getWidth();
        double height = frontEnd.getHeight();

        //Lobby scene, where player is waiting for start.
        BorderPane lobbyRoot = new BorderPane();
        final Scene lobbyScreen = new Scene(lobbyRoot, width, height);
        final VBox lobby = new VBox(10);
        lobby.setStyle("-fx-background-color: ROYALBLUE; -fx-font-family: Berlin Sans FB Demi");

        Label lobbyStart = new Label("THE GAME WILL START SOON ;)");
        lobbyStart.setFont(textFont);

        Label lobbyPlayers = new Label("Players that are already ready:");
        System.out.println(javafx.scene.text.Font.getFamilies());

        //How to update, when new players join?
        final ObservableList<String> participants = FXCollections.observableArrayList(names);
        final ArrayList<Label> participantsLabels = new ArrayList<>();
        for (String participant : participants) {
            participantsLabels.add(new Label(participant));
        }

        participants.addListener((ListChangeListener<String>) observable -> {
            lobby.getChildren().removeAll(participantsLabels);
            participantsLabels.clear();
            for (String participant : participants) {
                participantsLabels.add(new Label(participant));
            }
            lobby.getChildren().addAll(participantsLabels);
        });

        lobby.setAlignment(Pos.CENTER);
        lobby.getChildren().addAll(lobbyStart, lobbyPlayers);
        lobby.getChildren().addAll(participantsLabels);

        // For testing purposes

        // For sending outgoing commands.
        final VBox testOutgoingCommands = new VBox();
        Label outgoingCommand = new Label("Outgoing command");
        outgoingCommand.setFont(textFont);
        TextField command = new TextField("Code");
        TextField params = new TextField("Params");
        Button sendCommand = new Button("Send command");

        // For displaying incoming commands.
        final VBox receivedCommands = new VBox();
        Label incomingCommand = new Label("Incoming command");
        incomingCommand.setFont(textFont);
        TextField incomingCommandsList = new TextField("");

        // Add to VBox elements.
        testOutgoingCommands.getChildren().addAll(outgoingCommand, command, params, sendCommand);
        receivedCommands.getChildren().addAll(incomingCommand, incomingCommandsList);

        // Event for sending out commands.
        sendCommand.setOnMousePressed(event -> {
            int code = Integer.parseInt(command.getText());
            String[] parameters = params.getText().split(" ");
            BaseClientBackEnd.addCommand(code, parameters, 0);
        });

        lobbyRoot.setTop(lobby);
        lobbyRoot.setCenter(testOutgoingCommands);
        lobbyRoot.setBottom(receivedCommands);

        return lobbyScreen;
    }
}
