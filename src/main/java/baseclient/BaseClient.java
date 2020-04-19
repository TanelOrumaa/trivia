package baseclient;

import general.commands.Command;
import general.commands.CommandQueue;
import general.questions.Question;
import general.questions.QuestionQueue;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Scanner;

public class BaseClient extends Application {

    static final Logger LOG = LoggerFactory.getLogger(BaseClient.class);

    protected ClientType type;
    protected static Stage guiStage;
    protected BaseClientBackEnd baseClientBackEnd;

    protected CommandQueue incomingCommands = new CommandQueue();
    protected QuestionQueue questionQueue = new QuestionQueue();



    public double getWidth() {
        if (type == ClientType.PRESENTER) {
            return 1280;
        }
        return 350;
    }

    public double getHeight() {
        if (type == ClientType.PRESENTER) {
            return 720;
        }
        return 580;
    }

    @Override
    public void start(final Stage primaryStage) {
        guiStage = primaryStage;

        try (Scanner in = new Scanner(System.in)) {
            System.out.println("Enter client type: (1 - Presenter, 2 - Player , 3 - Host) :");
            String answer = in.next();
            switch (answer) {
                case "1":
                    type = ClientType.PRESENTER;
                    guiStage.setTitle("Presenter client");
                    break;
                case "2":
                    type = ClientType.PLAYER;
                    guiStage.setTitle("Player client");
                    break;
                case "3":
                    type = ClientType.HOST;
                    guiStage.setTitle("Host client");
                    break;
            }
        }


        this.baseClientBackEnd = new BaseClientBackEnd(this, questionQueue);
        Thread backEndThread = new Thread(baseClientBackEnd);
        backEndThread.start();


        guiStage.setScene(LogInScreen.change(this));
        guiStage.show();

    }

    public static void main(String[] args) {
        launch(args);
    }

    public void listenEvent() {
        LOG.debug("Listened to an event!");
        Command command = incomingCommands.poll(System.currentTimeMillis());
        LOG.debug("Listened to an event! " + command.toString());
        switch (command.code) {
            case 122:
                Platform.runLater(() -> {
                    LOG.debug("Switching scene to lobbyEntry");
                    guiStage.setScene(LobbyEntry.change(this));
                });
                break;
            case 124:
                Platform.runLater(() -> {
                    LOG.debug("Switching scene to RegistrationSuccessfulScreen");
                    guiStage.setScene(RegistrationSuccessfulScreen.change(this));
                });
            case 132:
                Platform.runLater(() -> {
                    LOG.debug("Switching scene to lobbyFX");
                    String[] names = new String[command.args.length - 1];
                    System.arraycopy(command.args, 1, names, 0, names.length);
                    guiStage.setScene(LobbyFX.change(this, false, command.args[0], names));
                });
                break;
            case 134:
                Platform.runLater(() -> {
                    LOG.debug("Switching scene to LobbyFX");
                    String[] names = new String[command.args.length - 1];
                    System.arraycopy(command.args, 1, names, 0, names.length);
                    guiStage.setScene(LobbyFX.change(this, true, command.args[0], names));
                });
                break;
            case 136:
                Platform.runLater(() -> {
                    LOG.debug("Updating participants list for lobby.");
                    LobbyFX.updateParticipantsList(command.args);
                });
                break;
            case 138: // Start game
            case 140: // Next question
                Platform.runLater(() -> {
                    LOG.debug("Switching scene to QuestionScene");
                    guiStage.setScene(QuestionScene.change(this, questionQueue.getQuestion(Long.getLong(command.args[0]))));
                });
                break;
            case 422:
                Platform.runLater(() -> {
                   LOG.debug("Invalid login data");
                   ErrorMessage.popUp("Incorrect username or password.");
                });
                break;
            case 424:
                Platform.runLater(() -> {
                    LOG.debug("User registration failed - username already exists");
                    ErrorMessage.popUp("Registration failed - username already exists!");
                });
                break;
            case 432:
                Platform.runLater(() -> {
                    LOG.debug("Lobby does not exist");
                    ErrorMessage.popUp("Lobby with this code does not exist.");
                });
                break;
            case 434:
                Platform.runLater(() -> {
                   LOG.debug("Lobby server is full");
                   ErrorMessage.popUp("Lobby server is full.");
                });
                break;
        }
    }

    public void addCommandToFrontEnd(Command command) {
        incomingCommands.add(command);
        this.listenEvent();

    }

}
