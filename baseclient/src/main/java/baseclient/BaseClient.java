package baseclient;

import command.Command;
import command.CommandQueue;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import popup.ErrorMessage;
import question.QuestionQueue;
import view.*;

import java.util.Scanner;

public class BaseClient extends Application {

    static final Logger LOG = LoggerFactory.getLogger(BaseClient.class);

    protected ClientType type;
    protected static Stage guiStage;
    protected BaseClientBackEnd baseClientBackEnd;

    protected static String username;
    protected static String[] triviasets = new String[0];

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


        guiStage.setScene(new LogInScreen(this));
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
                    username = command.args[0];
                    guiStage.setScene(new UserMainPage(this));
                });
                break;
            case 124:
                Platform.runLater(() -> {
                    LOG.debug("Switching scene to view.RegistrationSuccessfulScreen");
                    guiStage.setScene(RegistrationSuccessfulScreen.change(this));
                });
            case 132: case 134:
                Platform.runLater(() -> {
                    LOG.debug("Switching scene to lobbyFX");
                    // Since first argument is lobby code and next ones are the participants, we'll extract the first argument.
                    String[] names = new String[command.args.length - 1];
                    System.arraycopy(command.args, 1, names, 0, names.length);
                    guiStage.setScene(new LobbyFX(this, command.code == 134, command.args[0], names));
                });
                break;
            case 136:
                Platform.runLater(() -> {
                    LOG.debug("Updating participants list for lobby.");
                    // Since first argument is lobby code and next ones are the participants, we'll extract the first argument.
                    String[] names = new String[command.args.length - 1];
                    System.arraycopy(command.args, 1, names, 0, names.length);
                    LobbyFX.updateParticipantsList(names);
                });
                break;
            case 138: // Start game
                LOG.error("Where is this coming from?!?!");
                break;
            case 140: // Next question
                Platform.runLater(() -> {
                    LOG.debug("Switching scene to QuestionScene");
                    guiStage.setScene(new QuestionScene(this, questionQueue.getQuestion(Long.parseLong(command.args[0]))));
                });
                break;
            case 212:
                Platform.runLater(() -> {
                    LOG.debug("Updating triviasets list.");
                    triviasets = command.args;
                    UserTriviasetPage.updateTriviasetsList(this.triviasets);
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
                    LOG.debug("user.User registration failed - username already exists");
                    ErrorMessage.popUp("Registration failed - username already exists!");
                });
                break;
            case 432:
                Platform.runLater(() -> {
                    LOG.debug("lobby.Lobby does not exist");
                    ErrorMessage.popUp("lobby.Lobby with this code does not exist.");
                });
                break;
            case 434:
                Platform.runLater(() -> {
                   LOG.debug("lobby.Lobby server is full");
                   ErrorMessage.popUp("lobby.Lobby server is full.");
                });
                break;
        }
    }

    public void addCommandToFrontEnd(Command command) {
        incomingCommands.add(command);
        this.listenEvent();

    }

    public String getUsername() {
        return username;
    }

    public void setGuiStage(Scene scene) {
        guiStage.setScene(scene);
    }

    public ClientType getType() {
        return type;
    }

    public static String[] getTriviasets() {
        return triviasets;
    }
}
