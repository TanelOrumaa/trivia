package baseclient;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import command.Command;
import command.CommandQueue;
import configuration.Configuration;
import exception.MixedServerMessageException;
import lobby.Lobby;
import lobby.LobbyDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import question.Question;
import question.QuestionDeserializer;
import question.QuestionQueue;
import user.User;
import user.UserDeserializer;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class BaseClientBackEnd implements Runnable {

    static final Logger LOG = LoggerFactory.getLogger(BaseClientBackEnd.class);

    protected BaseClient frontEnd;
    protected ClientType clientType;
    protected QuestionQueue questionQueue;

    private DataInputStream dataInputStream;
    private DataOutputStream dataOutputStream;

    static CommandQueue commandQueue;

    // Client properties.
    private static String hash;
    private static User user;
    private static Lobby currentLobby;

    // Sync variables.
    private static long serverTimeDelta;
    private static long smallestPing;

    public BaseClientBackEnd(BaseClient frontEnd, QuestionQueue questionQueue) {
        this.frontEnd = frontEnd;
        this.questionQueue = questionQueue;
        this.clientType = frontEnd.type;

        smallestPing = Long.MAX_VALUE;
        commandQueue = new CommandQueue();
    }

    public void run() {
        // Create connection with backend.
        try (Socket socket = new Socket(Configuration.SERVER_IP_ADDRESS, Configuration.SERVER_LISTENING_PORT)) {
            this.dataInputStream = new DataInputStream(socket.getInputStream());
            this.dataOutputStream = new DataOutputStream(socket.getOutputStream());

            dataOutputStream.writeInt(101); // Hello message.
            switch (this.clientType) { // Type of client
                case PRESENTER:
                    dataOutputStream.writeInt(1);
                    break;
                case PLAYER:
                    dataOutputStream.writeInt(2);
                    break;
            }

            LOG.debug("Sent the hello message.");

            // Receive acknowledgement + hash
            int code = dataInputStream.readInt();
            if (code == 102) {
                hash = dataInputStream.readUTF();
                LOG.debug("Server ackowledged our \"Hello\" message. Our hash is now " + hash);
            } else {
                handleError(code);
            }

            // Do the first sync
            try {
                sync();
            } catch (IOException e) {
                LOG.warn("Sync with server failed.");
            }

            LOG.debug("Back-end started");

            // Run in loop until quit command is sent.
            Command command;
            try {
                while (true) {
                    if (commandQueue.size() > 0 && (command = commandQueue.poll(now())) != null) {
                        LOG.debug("Received a new command from front-end: " + command);
                        if (command.code == 199) {
                            break;
                        } else {
                            handleCommand(command);
                        }
                    } else {
                        LOG.trace("No commands right now. Trying to fetch messages from server.");
                        // See if server has sent any messages.
                        if (dataInputStream.available() > 0) {
                            LOG.debug("There's a message incoming.");
                            code = dataInputStream.readInt();
                            handleIncoming(code);
                        } else {
                            // Otherwise wait for a small amount of time to save CPU resources.
                            Thread.sleep(Configuration.SOCKET_POLL_INTERVAL);
                        }
                    }
                }
            } catch (InterruptedException e) {
                LOG.debug("Thread was interrupted mid-sleep.");
            }
        } catch (IOException e) {
            // TODO: Let the client know that connection to the server failed.
        } finally {
            try {
                dataInputStream.close();
                dataOutputStream.close();
            } catch (IOException e) {
                LOG.warn("Unable to close input/output streams.");
            }
        }
    }

    public static void addCommandToBackEnd(int code, String[] commands, int delay) {
        commandQueue.add(new Command(code, commands, now() + delay));
        LOG.debug("Added a new command " + code + " to queue.");
    }

    private void handleCommand(Command command) throws IOException {
        int commandCode = command.code;
        switch (commandCode) {
            case 111: // Sync command.
                sync();
                break;
            case 121: // Login command
                if (command.args != null && command.args.length == 2) {
                    login(command.args[0], command.args[1]);
                }
                break;
            case 123: // Register new user
                if (command.args != null && command.args.length == 3) {
                    registerUser(command.args[0], command.args[1], command.args[2]);

                }
            case 131: // Connect to lobby
                if (command.args != null && command.args.length == 1) {
                    connectToLobby(Integer.parseInt(command.args[0]));
                }
                break;
            case 133: // Create lobby
                if (command.args != null && command.args.length == 1) {
                    int triviasetId = Integer.parseInt(command.args[0]);
                    createLobby(triviasetId);
                }
                break;
            case 139: // Start game.
                startGame();
                break;
            case 191: // Leave lobby
                if (command.args != null && command.args.length == 1) {
                    leaveLobby(Integer.parseInt(command.args[0]));
                }
                break;
            case 201: // Request a question from server.
                if (command.args != null && command.args.length == 1) {
                    requestQuestion(Long.parseLong(command.args[0]));
                }
                break;
            case 203: // Send question answer to server.
                if (command.args != null && command.args.length == 2) {
                    sendAnswer(Integer.parseInt(command.args[0]), command.args[1]);
                }
                break;
            case 211: // Request triviaset list from server.
                if (command.args != null && command.args.length == 0) {
                    fetchTriviasets();
                }
                break;
            case 213: // Fetch one full triviaset from server and display it.
                if (command.args != null && command.args.length == 1) {
                    fetchFullTriviaSet(Long.parseLong(command.args[0]), false);
                }
                break;
            case 215: // Register new triviaset
                if (command.args != null && command.args.length == 1) {
                    sendTriviaSet(command.args[0]);
                    System.out.println("BaseClientBackEnd " + command.args[0]);
                }
                break;


            default:
                // For testing.
                LOG.debug("Unknown command " + command + ". Sending to server.");
                dataOutputStream.writeInt(commandCode);
                dataOutputStream.writeInt(command.args.length);
                for (int i = 0; i < command.args.length; i++) {
                    dataOutputStream.writeUTF(command.args[i]);
                }
                LOG.debug("Sent command to backend");
                int incomingCode = dataInputStream.readInt();
                LOG.debug("Incoming code was " + incomingCode);
                int argsAmount = dataInputStream.readInt();
                LOG.debug("Server will reply with " + argsAmount + " pieces of data.");

                String[] incomingParams = new String[argsAmount];
                for (int i = 0; i < argsAmount; i++) {
                    incomingParams[i] = dataInputStream.readUTF();
                }
                LOG.debug("Received all pieces of data.");
                LOG.debug("Server sent us:");
                for (int i = 0; i < incomingParams.length; i++) {
                    LOG.debug(incomingParams[i]);
                }
                LOG.debug("Command finished.");
        }
    }

    private void handleIncoming(int commandCode) throws IOException {
        // TODO: WIP
        LOG.debug("Incoming message code: " + commandCode);

        String receivedHash = dataInputStream.readUTF();
        if (hash.equals(receivedHash)) {
            switch (commandCode) {
                case 126:
                    frontEnd.addCommandToFrontEnd(new Command(126, new String[0]));
                    break;
                case 136:
                    updateLobby();
                    break;
                case 140: // Display next question
                    displayNextQuestion();
                    break;
                case 142: // Everyone has answered.
                    everyoneAnswered();
                    break;
                case 194:
                    int lobbyCode = dataInputStream.readInt();
                    leaveLobbyUnwillingly(lobbyCode);
                    break;
                default:
                    // Just for testing.
                    int paramsAmount = dataInputStream.readInt();
                    String[] params = new String[paramsAmount];
                    for (int i = 0; i < paramsAmount; i++) {
                        params[i] = dataInputStream.readUTF();
                    }
                    this.frontEnd.incomingCommands.add(new Command(commandCode, params));
                    LOG.debug("Added a new command to incoming queue.");
                    this.frontEnd.listenEvent();
            }
        } else {
            throw new MixedServerMessageException(hash, receivedHash);
        }
    }

    private void handleError(int errorCode) throws IOException {
        System.out.println("ERROR: " + errorCode);

        String receivedHash = dataInputStream.readUTF();
        if (hash.equals(receivedHash)) {
            switch (errorCode) {
                case 402:
                    LOG.debug("Unexpected message code.");
                    break;
                case 404:
                    LOG.debug("We sent an incorrect hash.");
                    break;
                case 422:
                    LOG.debug("Invalid login data.");
                    this.frontEnd.addCommandToFrontEnd(new Command(422, new String[0]));
                    break;
                case 424:
                    LOG.debug("New user registration failed because username already exists");
                    // Send error code to front-end to display error to user
                    this.frontEnd.addCommandToFrontEnd(new Command(424, new String[0]));
                    break;
                case 432:
                    LOG.debug("Lobby does not exist.");
                    this.frontEnd.addCommandToFrontEnd(new Command(432, new String[0]));
                    break;
                case 434:
                    LOG.debug("Lobby is full.");
                    this.frontEnd.addCommandToFrontEnd(new Command(434, new String[0]));
                    break;
                case 436:
                    LOG.debug("Server failed to send us next question");
                    break;
                case 440:
                    LOG.debug("Server failed to send full trivia set");
                    break;
            }
        } else {
            throw new MixedServerMessageException(hash, receivedHash);
        }

    }

    private void sync() throws IOException {
        // Send sync intialization message.
        LOG.debug("Sending sync message to server.");
        long syncStart = System.currentTimeMillis();
        dataOutputStream.writeInt(111);
        dataOutputStream.writeUTF(hash);

        int code = dataInputStream.readInt();
        if (code == 112) {
            LOG.debug("Server accepted our sync message and responded.");
            String resonseHash = dataInputStream.readUTF();
            if (hash.equals(resonseHash)) {
                long serverTime = dataInputStream.readLong();
                long syncEnd = System.currentTimeMillis();
                long ping = syncEnd - syncStart;
                if (ping <= smallestPing) {
                    serverTimeDelta = syncEnd - serverTime - ping / 2;
                    smallestPing = ping;
                }
                LOG.debug("Sync complete. New ping was " + ping + " and smallestPing is " + smallestPing);
            } else {
                throw new MixedServerMessageException(hash, resonseHash);
            }
        } else if (code >= 400 && code < 500) {
            handleError(code);
        } else {
            handleIncoming(code);
        }

        // Queue next sync
        addCommandToBackEnd(111, new String[0], 60000);
    }

    private void login(String username, String password) throws IOException {
        // Send login message.
        LOG.debug("Sending a login message to server.");
        dataOutputStream.writeInt(121);
        dataOutputStream.writeUTF(hash);
        dataOutputStream.writeUTF(username);
        dataOutputStream.writeUTF(password);

        // Receive response.
        int responseCode = dataInputStream.readInt();
        if (responseCode == 122) {
            LOG.debug("Server accepted our login message.");
            // Login was successful. Check if hash matches.
            String responseHash = dataInputStream.readUTF();
            if (hash.equals(responseHash)) {
                // Read and deserialize the user object.
                String userJson = dataInputStream.readUTF();
                Gson gson = new GsonBuilder().registerTypeAdapter(User.class, new UserDeserializer()).create();
                user = gson.fromJson(userJson, User.class);

                // Add the command to front end.
                this.frontEnd.addCommandToFrontEnd(new Command(122, new String[]{userJson}));

            } else {
                throw new MixedServerMessageException(hash, responseHash);
            }
        } else if (responseCode >= 400 && responseCode < 500) {
            handleError(responseCode);
        } else {
            handleIncoming(responseCode);
        }
    }


    private void connectToLobby(int lobbyCode) throws IOException {
        // Send connect to lobby message.
        LOG.debug("Sending a connect to lobby message with code " + lobbyCode);
        dataOutputStream.writeInt(131);
        dataOutputStream.writeUTF(hash);
        dataOutputStream.writeInt(lobbyCode);

        // Read the response
        int responseCode = dataInputStream.readInt();

        if (responseCode == 132) {
            LOG.debug("Server accepted the lobby connection message.");
            // Connecting to lobby was successful. Check if hash matches.
            String responseHash = dataInputStream.readUTF();
            if (hash.equals(responseHash)) {
                // Read the lobby object and deserialize it.
                String lobbyJson = dataInputStream.readUTF();

                Gson gson = new GsonBuilder().registerTypeAdapter(Lobby.class, new LobbyDeserializer()).create();
                currentLobby = gson.fromJson(lobbyJson, Lobby.class);
                LOG.debug("Connected to lobby " + currentLobby.getCode());

                this.frontEnd.addCommandToFrontEnd(new Command(132, currentLobby.getLobbyCodeAndConnectedUserNamesAsArray()));

                // Request the questions.
                fetchFullTriviaSet(currentLobby.getTriviaSetId(), true);
            } else {
                throw new MixedServerMessageException(hash, responseHash);
            }
        } else if (responseCode >= 400 && responseCode < 500) {
            handleError(responseCode);
        } else {
            handleIncoming(responseCode);
        }
    }

    private void createLobby(int triviasetId) throws IOException {
        // Send lobby creation message.
        LOG.debug("Sending a create lobby message.");
        dataOutputStream.writeInt(133);
        dataOutputStream.writeUTF(hash);
        dataOutputStream.writeInt(triviasetId);
        LOG.debug("Create lobby message sent.");
        // Read the response.
        int responseCode = dataInputStream.readInt();
        if (responseCode == 134) {
            String responseHash = dataInputStream.readUTF();
            if (hash.equals(responseHash)) {
                // Read the lobby object and deserialize it.
                String lobbyAsJson = dataInputStream.readUTF();

                Gson gson = new GsonBuilder().registerTypeAdapter(Lobby.class, new LobbyDeserializer()).create();
                currentLobby = gson.fromJson(lobbyAsJson, Lobby.class);
                LOG.debug("Created lobby with code " + currentLobby.getCode());

                this.frontEnd.addCommandToFrontEnd(new Command(134, currentLobby.getLobbyCodeAndConnectedUserNamesAsArray()));

                // Request the questions.
                fetchFullTriviaSet(currentLobby.getTriviaSetId(), true);
            } else {
                throw new MixedServerMessageException(hash, responseHash);
            }
        } else if (responseCode >= 400 && responseCode < 500) {
            handleError(responseCode);
        } else {
            handleIncoming(responseCode);
        }
    }

    private void startGame() throws IOException {
        // Send "Start game" message to server.
        LOG.debug("Sending \"Start game\" message to server.");
        dataOutputStream.writeInt(139);
        dataOutputStream.writeUTF(hash);

        LOG.debug("Start game message sent.");
        int responseCode = dataInputStream.readInt();
        if (responseCode == 138) { // Game started for everyone.
            String responseHash = dataInputStream.readUTF();
            if (!responseHash.equals(hash)) {
                throw new MixedServerMessageException(hash, responseHash);
            }
        } else if (responseCode >= 400 && responseCode < 500) {
            handleError(responseCode);
        } else {
            handleIncoming(responseCode);
        }
    }

    public int getLobbyCode() {
        return currentLobby.getCode();
    }

    private void registerUser(String username, String password, String nickname) throws IOException {
        // Send register new user message.
        LOG.debug("Sending a register new user message");
        dataOutputStream.writeInt(123);
        dataOutputStream.writeUTF(hash);
        dataOutputStream.writeUTF(username);
        dataOutputStream.writeUTF(password);
        dataOutputStream.writeUTF(nickname);

        //Receive response
        int responseCode = dataInputStream.readInt();
        if (responseCode == 124) {
            LOG.debug("Server responded positively - registration successful");
            String responseHash = dataInputStream.readUTF();
            if (hash.equals(responseHash)) {
                this.frontEnd.addCommandToFrontEnd(new Command(124, new String[0]));

            } else {
                throw new MixedServerMessageException(hash, responseHash);
            }

        } else if (responseCode >= 400 && responseCode < 500) {
            handleError(responseCode);
        } else {
            handleIncoming(responseCode);
        }
    }

    private void updateLobby() throws IOException {
        String lobbyAsJson = dataInputStream.readUTF();
        Gson gson = new GsonBuilder().registerTypeAdapter(Lobby.class, new LobbyDeserializer()).create();
        currentLobby = gson.fromJson(lobbyAsJson, Lobby.class);

        LOG.debug("New lobby version received " + currentLobby.getCode());
        this.frontEnd.addCommandToFrontEnd(new Command(136, currentLobby.getLobbyCodeAndConnectedUserNamesAsArray()));

        dataOutputStream.writeInt(137);
        dataOutputStream.writeUTF(hash);
    }

    private void displayNextQuestion() throws IOException {
        Long questionId = dataInputStream.readLong();
        LOG.debug("Server said to display next question with id " + questionId);
        frontEnd.addCommandToFrontEnd(new Command(140, new String[]{String.valueOf(questionId), Boolean.toString(currentLobby.getLobbyOwnerId() == user.getId())}));

        // Respond to server.
        dataOutputStream.writeInt(141);
        dataOutputStream.writeUTF(hash);
    }

    private void everyoneAnswered() throws IOException {
        frontEnd.addCommandToFrontEnd(new Command(142, new String[0]));
        dataOutputStream.writeInt(143);
        dataOutputStream.writeUTF(hash);
    }

    private void requestQuestion(long previousQuestionId) throws IOException {
        // Send request question message
        LOG.debug("Sending a request next question message");
        dataOutputStream.writeInt(201);
        dataOutputStream.writeUTF(hash);
        dataOutputStream.writeLong(previousQuestionId);

        // Read the response
        int responseCode = dataInputStream.readInt();
        if (responseCode == 202) {
            LOG.debug("Server responded positively and sent next question");
            String responseHash = dataInputStream.readUTF();
            if (hash.equals(responseHash)) {
                // Read the Question object and deserialize it
                String questionJson = dataInputStream.readUTF();
                Gson gson = new GsonBuilder().registerTypeAdapter(Question.class, new QuestionDeserializer()).create();
                Question nextQuestion = gson.fromJson(questionJson, Question.class);

                // Add the question to question queue but wait for server's message before displaying it
                questionQueue.addQuestion(nextQuestion);

                LOG.debug("Question with id " + nextQuestion.getQuestionID() + " received from server.");

            } else {
                throw new MixedServerMessageException(hash, responseHash);
            }

        } else if (responseCode >= 400 && responseCode < 500) {
            handleError(responseCode);
        } else {
            handleIncoming(responseCode);
        }
    }

    private void sendAnswer(int questionId, String answer) throws IOException {
        // If answer type is choice, then String answer is answe id as String,
        // and if answer type is freeform, then String answer is user's entered answer
        LOG.debug("Sending client's answer to server");
        dataOutputStream.writeInt(203);
        dataOutputStream.writeUTF(hash);
        dataOutputStream.writeInt(questionId);
        dataOutputStream.writeUTF(answer);

        // Read the response
        int responseCode = dataInputStream.readInt();
        if (responseCode == 204){
            LOG.debug("Server received the answer successfully");
            String responseHash = dataInputStream.readUTF();
            if (hash.equals(responseHash)){
                // Display waiting screen in frontEnd
                frontEnd.addCommandToFrontEnd(new Command(204, new String[0]));

            }
        } else if (responseCode >= 400 && responseCode < 500) {
            handleError(responseCode);
        } else {
            handleIncoming(responseCode);
        }
    }

    private void fetchTriviasets() throws IOException {
        // Send fetch triviasets message.
        LOG.debug("Sending request triviasets message.");
        dataOutputStream.writeInt(211);
        dataOutputStream.writeUTF(hash);

        // Read the response
        int responseCode = dataInputStream.readInt();
        if (responseCode == 212) {
            LOG.debug("Server responded positively and sent us triviasets");
            String responseHash = dataInputStream.readUTF();
            if (hash.equals(responseHash)) {
                // Read the Triviasets list
                String triviasetListAsJson = dataInputStream.readUTF();

                frontEnd.addCommandToFrontEnd(new Command(212, new String[] {triviasetListAsJson}));

            } else {
                throw new MixedServerMessageException(hash, responseHash);
            }
        } else if (responseCode >= 400 && responseCode < 500) {
            handleError(responseCode);
        } else {
            handleIncoming(responseCode);
        }

    }

    private void fetchFullTriviaSet(long triviaSetId, boolean inTheBackground) throws IOException {
        LOG.debug("Sending full trivia set request.");
        dataOutputStream.writeInt(213);
        dataOutputStream.writeUTF(hash);
        dataOutputStream.writeLong(triviaSetId);

        // Read the response
        int responseCode = dataInputStream.readInt();
        if (responseCode == 214) {
            String responseHash = dataInputStream.readUTF();
            if (hash.equals(responseHash)) {
                String triviaSetAsJson = dataInputStream.readUTF();
                if (!inTheBackground) {
                    frontEnd.addCommandToFrontEnd(new Command(214, new String[]{triviaSetAsJson}, 0));
                } else {
                    frontEnd.addCommandToFrontEnd(new Command(218, new String[] {triviaSetAsJson}));
                }
            } else {
                throw new MixedServerMessageException(hash, responseHash);
            }


        } else if (responseCode >= 400 && responseCode < 500) {
            handleError(responseCode);
        } else {
            handleIncoming(responseCode);
        }

    }

    private void sendTriviaSet(String serializedTriviaSet) throws IOException {
        // Send register triviaset message
        LOG.debug("Sending register trivia set message");
        dataOutputStream.writeInt(215);
        dataOutputStream.writeUTF(hash);

        dataOutputStream.writeUTF(serializedTriviaSet);

        //Receive response
        int responseCode = dataInputStream.readInt();
        if (responseCode == 126){
            LOG.debug("Server responded positively - trivia set registration was successful");
            String responseHash = dataInputStream.readUTF();
            if (hash.equals(responseHash)) {
                // display trivia set registration successful screen
                frontEnd.addCommandToFrontEnd(new Command(124, new String[0], 0));
            } else {
                throw new MixedServerMessageException(hash, responseHash);
            }
        } else if (responseCode >= 400 && responseCode < 500) {
            handleError(responseCode);
        } else {
            handleIncoming(responseCode);
        }

    }

    private void leaveLobby(int lobbyCode) throws IOException {
        LOG.debug("Sending \"Leave lobby\" message for lobby " + lobbyCode);
        dataOutputStream.writeInt(191);
        dataOutputStream.writeUTF(hash);
        dataOutputStream.writeInt(lobbyCode);

        // Receive response
        int responseCode = dataInputStream.readInt();
        if (responseCode == 192) {
            String responseHash = dataInputStream.readUTF();
            if (hash.equals(responseHash)) {
                LOG.debug("Server removed us from the lobby.");
            } else {
                throw new MixedServerMessageException(hash, responseHash);
            }
        } else if (responseCode >= 400 && responseCode < 500) {
            handleError(responseCode);
        } else {
            handleIncoming(responseCode);
        }

    }

    private void leaveLobbyUnwillingly(int lobbyCode) throws IOException {
        LOG.debug("Leaving lobby since it was deleted.");

        frontEnd.addCommandToFrontEnd(new Command(194, new String[] {Integer.toString(lobbyCode)}));
        currentLobby = null;

        dataOutputStream.writeInt(195);
        dataOutputStream.writeUTF(hash);
    }

    private void sendGoodbye() throws IOException {
        // Send goodbye message
        if (dataOutputStream != null) {
            LOG.debug("Sending goodbye message.");
            dataOutputStream.writeInt(199);
            dataOutputStream.writeUTF(hash);
        }
    }

    private static long now() {
        return System.currentTimeMillis() + serverTimeDelta;
    }

    public void stopBackend() {
        try {
            sendGoodbye();
        } catch (IOException e) {
            LOG.warn("Could not notify server of shutting down.");
        }
    }
}
