package baseclient;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import command.Command;
import command.CommandQueue;
import configuration.Configuration;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import popup.ErrorMessage;
import question.QuestionQueue;
import triviaset.TriviaSet;
import triviaset.TriviaSetDeserializerFull;
import triviaset.TriviaSetsDeserializerBasic;
import user.User;
import user.UserSerializer;
import view.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BaseClient extends Application {

    static final Logger LOG = LoggerFactory.getLogger(BaseClient.class);

    protected ClientType type;
    protected Stage guiStage;
    protected BaseClientBackEnd baseClientBackEnd;

    protected User user;
    protected List<TriviaSet> basicTriviaSets = new ArrayList<>();

    protected CommandQueue incomingCommands = new CommandQueue();
    protected QuestionQueue questionQueue = new QuestionQueue();


    public BaseClient(ClientType type) {
        this.type = type;

    }

    @Override
    public void start(final Stage primaryStage) {
        // First read the configuration.
        try {
            Configuration.readConfiguration();
        } catch (IOException e) {
            throw new RuntimeException("Unable to read configuration file, make sure you have one in resources/sensitive", e);
        }

        guiStage = primaryStage;

        switch (type) {
            case PRESENTER:
                guiStage.setTitle("Presenter client");
                break;
            case PLAYER:
                guiStage.setTitle("Player client");
                break;
        }


        this.baseClientBackEnd = new BaseClientBackEnd(this, questionQueue);
        Thread backEndThread = new Thread(baseClientBackEnd);
        backEndThread.start();

        switch (type) {
            case PRESENTER:
                guiStage.setScene(new LobbyEntry(this));
                break;
            case PLAYER:
                guiStage.setScene(new LogInScreen(this));
//                this.username = "AwesomeZebra";
//                guiStage.setScene(new LobbyFX(this, true, "124341", new String[] {"Pelmo", "Jaanus", "Jäänus", "Kallemar"}));
                break;
        }

        primaryStage.setOnCloseRequest(event -> {
            baseClientBackEnd.stopBackend();
            backEndThread.interrupt();
        });

        guiStage.show();

    }

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

    public void listenEvent() {
        LOG.debug("Listened to an event!");
        Command command = incomingCommands.poll(System.currentTimeMillis());
        LOG.debug("Listened to an event! " + command.toString());
        switch (command.code) {
            case 122:
                Platform.runLater(() -> {
                    LOG.debug("Switching scene to user main page");
                    if (command.args != null && command.args.length == 1) {
                        String userAsJson = command.args[0];
                        Gson gson = new GsonBuilder().registerTypeAdapter(User.class, new UserSerializer()).create();
                        this.user = gson.fromJson(userAsJson, User.class);
                        guiStage.setScene(new UserMainPage(this));
                    }
                });
                break;
            case 124:
                Platform.runLater(() -> {
                    LOG.debug("Switching scene to RegistrationSuccessfulScreen (user)");
                    guiStage.setScene(new RegistrationSuccessfulScreen(this, "user"));
                });
                break;
            case 126:
                Platform.runLater(() -> {
                    LOG.debug("Switching scene to RegistrationSuccessfulScreen (trivia set)");
                    guiStage.setScene(new RegistrationSuccessfulScreen(this, "trivia set"));
                });
                break;
            case 132:
            case 134:
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
            case 194: // Lobby deleted
                Platform.runLater(() -> {
                    LOG.debug("Lobby was deleted.");
                    guiStage.setScene(new LobbyEntry(this));
                });
            case 212:
                Platform.runLater(() -> {
                    LOG.debug("Updating triviasets list.");
                    List<TriviaSet> basicTriviaSetList = new ArrayList<>();
                    if (command.args != null && command.args.length == 1) {
                        String basicTrivaSetsJson = command.args[0];
                        Gson gson = new GsonBuilder().registerTypeAdapter(List.class, new TriviaSetsDeserializerBasic()).create();
                        basicTriviaSetList = gson.fromJson(basicTrivaSetsJson, List.class);
                        this.basicTriviaSets = basicTriviaSetList;
                    }
                    UserTriviasetPage.updateTriviasetsList(basicTriviaSetList);
                });
                break;
            case 214:
                Platform.runLater(() -> {
                    LOG.debug("Received full trivia set from backend.");
                    if (command.args != null && command.args.length == 1) {
                        Gson gson = new GsonBuilder().registerTypeAdapter(TriviaSet.class, new TriviaSetDeserializerFull()).create();
                        TriviaSet triviaSet = gson.fromJson(command.args[0], TriviaSet.class);
                        replaceTriviaSet(triviaSet);
                        setGuiStage(new TriviaSetDetailView(this, triviaSet));
                    }
                });
                break;
            case 218: // Triviaset fetched in the background
                if (command.args != null && command.args.length == 1) {
                    questionQueue.clearQueue();
                    Gson gson = new GsonBuilder().registerTypeAdapter(TriviaSet.class, new TriviaSetDeserializerFull()).create();
                    TriviaSet triviaSet = gson.fromJson(command.args[0], TriviaSet.class);
                    triviaSet.getQuestionMap().forEach((id, question) -> {
                        questionQueue.addQuestion(question);
                    });
                    LOG.debug("Updated question queue.");
                }
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

    public String getUsername() {
        return user.getUsername();
    }

    public long getUserId() {
        return user.getId();
    }

    public void setGuiStage(Scene scene) {
        guiStage.setScene(scene);
    }

    public ClientType getType() {
        return type;
    }

    public List<TriviaSet> getBasicTriviaSets() {
        return basicTriviaSets;
    }

    public void logout() {
        this.user = null;
        this.incomingCommands.clear();
        this.questionQueue.clearQueue();
    }

    private void replaceTriviaSet(TriviaSet triviaSet) {
        int index = getTriviaSetIndex(triviaSet.getId());
        if (index != -1) {
            basicTriviaSets.set(index, triviaSet);
        } else {
            basicTriviaSets.add(triviaSet);
        }
    }

    private int getTriviaSetIndex(long triviaSetId) {
        for (int i = 0; i < basicTriviaSets.size(); i++) {
            if (basicTriviaSets.get(i).getId() == triviaSetId) {
                return i;
            }
        }
        return -1;
    }
}
