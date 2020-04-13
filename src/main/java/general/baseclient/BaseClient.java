package general.baseclient;

import general.Command;
import general.CommandQueue;
import general.questions.Question;
import javafx.application.Application;
import javafx.application.Platform;
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
    protected BlockingQueue<Question> questions = new ArrayBlockingQueue<Question>(10);
    protected BaseClientBackEnd baseClientBackEnd;

    protected CommandQueue incomingCommands = new CommandQueue();


    public static double getWidth() {
        double width = 350;
        return width;
    }

    public static double getHeight() {
        double height = 561.5;
        return height;
    }




    @Override
    public void start(final Stage primaryStage) throws Exception {
        guiStage = primaryStage;

        try (Scanner in = new Scanner(System.in)) {
            System.out.println("Enter client type: (1 - Presenter, 2 - Player , 3 - Host) :");
            String answer = in.next();
            switch (answer) {
                case "1":
                    type = ClientType.PRESENTER;
                    break;
                case "2":
                    type = ClientType.PLAYER;
                    break;
                case "3":
                    type = ClientType.HOST;
                    break;
            }
        }


        this.baseClientBackEnd = new BaseClientBackEnd(this.type, this, questions);
        Thread backEndThread = new Thread(baseClientBackEnd);
        backEndThread.start();



        //Already have logInScreen, lobbyEntryScreen, lobbyScreen, registerScreen, questionChoiceScreen, questionTextAreaScreen, waitingAfterQuestionScreen

        guiStage.setScene(LogIn.change(guiStage, this));
        guiStage.setTitle("Base client");
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
                    guiStage.setScene(LobbyEntry.change(guiStage, this));
                });
                break;
            case 132:
                Platform.runLater(() -> {
                    LOG.debug("Switching scene to lobbyFX");
                    guiStage.setScene(LobbyFX.change(guiStage, this, command.args));
                });
                break;
            case 134:
                Platform.runLater(() -> {
                    LOG.debug("Switching scene to LobbyFX");
                    guiStage.setScene(LobbyFX.change(guiStage, this, command.args));
                });
        }
    }

    public void addCommandAndInvoke(Command command) {
        incomingCommands.add(command);
        this.listenEvent();

    }

}