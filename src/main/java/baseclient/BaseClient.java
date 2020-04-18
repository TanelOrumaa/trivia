package baseclient;

import general.Command;
import general.CommandQueue;
import general.questions.Question;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Popup;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Scanner;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class BaseClient extends Application {

    static final Logger LOG = LoggerFactory.getLogger(BaseClient.class);

    protected ClientType type;
    protected static Stage guiStage;
    protected BlockingQueue<Question> questions = new ArrayBlockingQueue<>(10);
    protected BaseClientBackEnd baseClientBackEnd;

    protected CommandQueue incomingCommands = new CommandQueue();


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


        this.baseClientBackEnd = new BaseClientBackEnd(this, questions);
        Thread backEndThread = new Thread(baseClientBackEnd);
        backEndThread.start();



        //Already have logInScreen, lobbyEntryScreen, lobbyScreen, registerScreen, questionChoiceScreen, questionTextAreaScreen, waitingAfterQuestionScreen

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
                    guiStage.setScene(LobbyFX.change(this, command.args));
                });
                break;
            case 134:
                Platform.runLater(() -> {
                    LOG.debug("Switching scene to LobbyFX");
                    guiStage.setScene(LobbyFX.change(this, command.args));
                });
                break;
            case 135:
                Platform.runLater(() -> {
                    LOG.debug("Switching scene to LogIn");
                    guiStage.setScene(LogInScreen.change(this));
                });
                break;
            case 136:
                Platform.runLater(() -> {
                    LOG.debug("Switching scene to Register");
                    guiStage.setScene(RegistrationScreen.change(this));
                });
                break;
            case 137:
                Platform.runLater(() -> {
                    LOG.debug("Switching scene to WaitingAfterQuestion");
                    guiStage.setScene(WaitingAfterQuestionScreen.change(this));
                });
                break;
            case 138:
                Platform.runLater(() -> {
                    LOG.debug("Switching scene to QuestionFX");
                    guiStage.setScene(QuestionScene.change(this, questions.poll()));
                });
            case 203:
                Platform.runLater(() -> {
                    LOG.debug("Received next question. Will wait for server's message to display the question");

                });
            case 423:
                Platform.runLater(() -> {
                    LOG.debug("User registration failed - username already exists");
                    Popup errorMessage = RegistrationFailedPopUp.getPopup();
                    errorMessage.show(guiStage);
                });
        }
    }

    public void addCommandAndInvoke(Command command) {
        incomingCommands.add(command);
        this.listenEvent();

    }

}
