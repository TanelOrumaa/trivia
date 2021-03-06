package server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import command.*;
import configuration.Configuration;
import database.DatabaseConnection;
import database.QuestionsDatabaseLayer;
import database.TriviaSetsDatabaseLayer;
import database.UsersDatabaseLayer;
import exception.*;
import jdk.jshell.spi.ExecutionControl;
import lobby.Lobby;
import lobby.LobbySerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import question.*;
import triviaset.TriviaSet;
import triviaset.TriviaSetDeserializerFull;
import triviaset.TriviaSetSerializerFull;
import triviaset.TriviaSetsSerializerBasic;
import user.User;
import user.UserSerializer;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.List;

public class ClientConnectionBase implements Runnable {

    // Logger
    static final Logger LOG = LoggerFactory.getLogger(ClientConnectionBase.class);

    // Class variables related to socket and database connections.
    private DatabaseConnection databaseConnection;
    private Socket socket;
    private DataInputStream dataInputStream;
    private DataOutputStream dataOutputStream;
    private String hash;
    private String clientId;

    // User for this client.
    private User user;

    // Class variables related to lobby.
    private Lobby currentLobby;
    private int lastLobbyUpdateId;
    private TriviaSet triviaSet;
    private int previousQuestionIndex;

    public ClientConnectionBase(Socket socket, DataInputStream dataInputStream, String hash, String clientId) {
        this.socket = socket;
        this.dataInputStream = dataInputStream;
        this.hash = hash;
        this.clientId = clientId;
        this.lastLobbyUpdateId = 0;
    }

    @Override
    public void run() {
        try {

            LOG.debug(clientId + "Starting thread.");
            this.databaseConnection = new DatabaseConnection();
            LOG.debug("Database connection established for " + clientId);

            try {
                // Create the dataoutputstream for the client.
                dataOutputStream = new DataOutputStream(socket.getOutputStream());

                // First acknowledge that we received the hello message and assign them a unique hash.
                dataOutputStream.writeInt(102);
                dataOutputStream.writeUTF(this.hash);
                LOG.debug("Replied to hello message for " + clientId);

                int messageCode = 0;

                // And then accept any other incoming messages.
                while (messageCode != 199) { // Quit message
                    LOG.trace("Trying to read next message from client.");

                    // Check if there are any messages available from client.
                    if (dataInputStream.available() == 0) {
                        // Check if current Lobby wants to send an update to client.
                        if (currentLobby != null && currentLobby.newUpdateAvailable(lastLobbyUpdateId)) {
                            LOG.debug("New update available for lobby.");
                            lastLobbyUpdateId++;

                            LobbyUpdateBase lobbyUpdateBase = currentLobby.getLobbyUpdate(lastLobbyUpdateId);
                            if (lobbyUpdateBase instanceof NewUserConnectedUpdate) {
                                updateLobby((NewUserConnectedUpdate) lobbyUpdateBase);
                                LOG.debug("Updated lobby for this client.");
                            } else if (lobbyUpdateBase instanceof DisplayNextQuestionUpdate) {
                                sendNextQuestionToLobbyClients((DisplayNextQuestionUpdate) lobbyUpdateBase);
                            } else if (lobbyUpdateBase instanceof LobbyDeletedUpdate) {
                                closeLobby((LobbyDeletedUpdate) lobbyUpdateBase);
                            } else if (lobbyUpdateBase instanceof EveryoneAnswered) {
                                everyoneAnswered((EveryoneAnswered) lobbyUpdateBase);
                            }
                        } else {
                            // If no updates from lobby either, sleep for a small amount of time to save CPU resources.
                            Thread.sleep(Configuration.SOCKET_POLL_INTERVAL);
                        }
                    } else {
                        LOG.debug("Reading the next message from " + clientId);
                        messageCode = dataInputStream.readInt();
                        LOG.debug("Received message " + messageCode + " from " + clientId);
                        handleIncoming(messageCode);
                    }


                }
            } catch (IOException e) {
                LOG.warn(clientId + "Unable to establish input/outputstreams with the client.");
                e.printStackTrace();
            } catch (InterruptedException e) {
                throw new RuntimeException("Unexpected interrupt.", e);
            } catch (ExecutionControl.NotImplementedException e) {
                throw new RuntimeException("Something is not implemented yet.", e);
            } finally {
                // Close the socket and connection
                try {
                    socket.close(); // Should also close both streams.
                    databaseConnection.closeDatabaseConnection();
                } catch (IOException e) {
                    LOG.warn(clientId + "Unable to close socket.");
                }
            }
        } catch (SQLException e) {
            LOG.warn(clientId + "Unable to create database connection.");
        }
    }

    protected void handleIncoming(int code) throws IOException, InterruptedException, ExecutionControl.NotImplementedException {

        // Check that the hash matches.
        if (dataInputStream.readUTF().equals(this.hash)) {

            switch (code) {
                case 111: // Sync message 1
                    // The logic here is that the server receives the client message and immediately answers with
                    // server time. Client receives server time and calculates ping (from 111 message send time to
                    // 112 message receive time and finds time delta with clientTime - serverTime - ping / 2.
                    // This sync should happen periodically, but with some sort of decent logic, so messages with
                    // lower ping would be considered more trustworthy (so we don't overwrite the delta with more
                    // incorrect delta).

                    sync();
                    break;

                case 121: // Login message
                    // The client sends a log in message with username and password. Server validates the logins and
                    // responds with 122 for successful login or 422 for invalid login data.

                    login();
                    break;

                case 123: // Register new user message
                    registerUser();
                    break;

                case 131: // Connecting to lobby message.
                    // Reads the lobby code and checks if lobby exists and if it does, can the user join the
                    // lobby. Returns 132 if lobby exists and the user has joined it, 432 if the lobby does not
                    // exist and 434 if the lobby is full.

                    connectToLobby();
                    break;

                case 133: // Creating a lobby message.
                    // Creates a new lobby and returns the lobby object to the user.

                    createALobby();
                    break;

                case 139: // Start game for this lobby
                    // Notifies all clients in currentLobby that game started and sends the first question id.

                    startGameForLobby();
                    break;

                case 145: // Display next question for all players
                    nextQuestionForPlayers();
                    break;
                case 191:
                    removePlayerFromLobby();
                    break;

                case 201: // Request a question
                    // The client requests a question from the server
                    sendQuestion();
                    break;
                case 203:
                case 205:// User answered question
                    checkAnswer(code == 203);
                    break;
                case 211: // Fetching list of triviasets for user
                    sendTriviasets();
                    break;
                case 213: // Fetching requested triviaset for user
                    sendFullTriviaSet();
                    break;
                case 215: // Registering a new triviaset
                    registerTriviaSet();
                    break;
                default:
                    // For testing different messages.
                    int paramsAmount = dataInputStream.readInt();
                    LOG.debug(clientId + " Will send " + paramsAmount + " pieces of data.");
                    String[] params = new String[paramsAmount];
                    for (int i = 0; i < paramsAmount; i++) {
                        params[i] = dataInputStream.readUTF();
                    }
                    LOG.debug("Message received. Responding.");
                    dataOutputStream.writeInt(code + 1);
                    dataOutputStream.writeInt(paramsAmount);
                    for (int i = paramsAmount - 1; i >= 0; i--) {
                        dataOutputStream.writeUTF(params[i]);
                    }
                    // Send a request after 5 seconds. This is to test client accepting a message with origin
                    // from server.
                    if (code == 233) {
                        Thread.sleep(5000);
                        dataOutputStream.writeInt(234);
                        dataOutputStream.writeInt(2);
                        dataOutputStream.writeUTF("Next question.");
                        dataOutputStream.writeUTF("Please.");
                    }
            }
        } else {
            LOG.debug(clientId + "Invalid hash from client.");
            dataOutputStream.writeInt(404); // Invalid hash.
        }
    }

    private void updateLobby(NewUserConnectedUpdate update) throws IOException {
        LOG.debug("Updating lobby for client " + clientId);
        // We'll send update only if this user isn't the one who connected to the lobby.
        if (super.getClass() == PresenterClientConnection.class || update.getUserId() != user.getId()) {
            Gson gson = new GsonBuilder().registerTypeAdapter(Lobby.class, new LobbySerializer()).create();
            String lobbyAsJson = gson.toJson(currentLobby);

            dataOutputStream.writeInt(136); // Update Lobby message
            dataOutputStream.writeUTF(hash);
            dataOutputStream.writeUTF(lobbyAsJson);

            LOG.debug(clientId + "received a lobby update.");

            // Wait for the response from client.
            int responseCode = dataInputStream.readInt();
            if (responseCode == 137 && dataInputStream.readUTF().equals(hash)) {
                LOG.debug(clientId + "updated lobby successfully.");
            } else {
                LOG.error(clientId + "was unable to update lobby with error code " + responseCode);
            }
        }
    }

    private void sendNextQuestionToLobbyClients(DisplayNextQuestionUpdate update) throws IOException {
        LOG.debug("Sending next question message for client " + clientId);

        long questionId = update.getQuestionId();
        dataOutputStream.writeInt(140);
        dataOutputStream.writeUTF(hash);
        dataOutputStream.writeLong(questionId);

        LOG.debug(clientId + "received next question message.");

        // Wait for the response from the client.
        int responseCode = dataInputStream.readInt();
        if (responseCode == 141 && dataInputStream.readUTF().equals(hash)) {
            LOG.debug(clientId + "received next question message.");
        } else {
            LOG.error(clientId + "was not able to show next question with error code " + responseCode);
        }
    }

    private void createALobby() throws IOException {

        LOG.debug(clientId + "Sent a lobby creation message.");
        int triviasetId = dataInputStream.readInt();
        LOG.debug("Creating a lobby for triviaset with id: " + triviasetId + " for user: " + user.getUsername());
        Lobby lobby = new Lobby(triviasetId, this.user, Server.generateLobbyCode());
        Server.addLobby(lobby);

        Gson gson = new GsonBuilder().registerTypeAdapter(Lobby.class, new LobbySerializer()).create();
        String lobbyAsJson = gson.toJson(lobby);

        dataOutputStream.writeInt(134); // Connection to lobby successful.
        dataOutputStream.writeUTF(this.hash);
        dataOutputStream.writeUTF(lobbyAsJson);

        currentLobby = lobby;

        // Fetch triviaset.
        triviaSet = TriviaSetsDatabaseLayer.readFullTriviaSet(databaseConnection, currentLobby.getTriviaSetId());

        LOG.debug(clientId + "Connection to lobby (" + lobby.getCode() + ") succesful.");

    }

    private void connectToLobby() throws IOException {
        LOG.debug(clientId + "Sent a lobby connection message.");


        int userSubmittedLobbyCode = dataInputStream.readInt();
        try {
            if (Server.isLobbyAvailable(userSubmittedLobbyCode, super.getClass() == PresenterClientConnection.class)) {
                Lobby lobby;
                if (super.getClass() == PlayerClientConnection.class) {
                    lobby = Server.addUserToLobby(this.user, userSubmittedLobbyCode);
                } else {
                    lobby = Server.addPresenterToLobby(userSubmittedLobbyCode);
                }

                Gson gson = new GsonBuilder().registerTypeAdapter(Lobby.class, new LobbySerializer()).create();
                String lobbyAsJson = gson.toJson(lobby);

                dataOutputStream.writeInt(132); // Connection to lobby successful.
                dataOutputStream.writeUTF(this.hash);
                dataOutputStream.writeUTF(lobbyAsJson);
                LOG.debug(clientId + "Connection to lobby (" + userSubmittedLobbyCode + ") succesful.");

                currentLobby = lobby;

                // Fetch triviaset.
                triviaSet = TriviaSetsDatabaseLayer.readFullTriviaSet(databaseConnection, currentLobby.getTriviaSetId());

                // Updating lobby for all other clients.
                LOG.debug("Adding a new lobby update for lobby " + currentLobby.getCode() + "");
                if (super.getClass() == PlayerClientConnection.class) {
                    currentLobby.addNewLobbyUpdate(new NewUserConnectedUpdate(user.getId()));
                }
            } else {
                LOG.warn(clientId + "This lobby (" + userSubmittedLobbyCode + ") is full.");
                dataOutputStream.writeInt(434); // Lobby is full.
                dataOutputStream.writeUTF(hash);
            }

        } catch (LobbyDoesNotExistException e) {
            LOG.warn(clientId + "This lobby (" + userSubmittedLobbyCode + ") does not exist.");
            dataOutputStream.writeInt(432); // Lobby does not exist.
            dataOutputStream.writeUTF(hash);
        }

    }

    private void removePlayerFromLobby() throws IOException, ExecutionControl.NotImplementedException {
        LOG.debug(clientId + "wants to leave current lobby.");
        int userSubmittedLobbyCode = dataInputStream.readInt();
        if (userSubmittedLobbyCode == currentLobby.getCode()) {
            if (super.getClass() != PresenterClientConnection.class) {
                if (currentLobby.getLobbyOwnerId() == user.getId()) {
                    // Lobby owner is leaving.
                    currentLobby.addNewLobbyUpdate(new LobbyDeletedUpdate(currentLobby.getCode()));
                    Server.removeLobby(currentLobby);
                } else {
                    // Some user is leaving the lobby.
                    currentLobby.removeUserFromLobby(user);
                    currentLobby.addNewLobbyUpdate(new NewUserConnectedUpdate(user.getId()));
                    currentLobby = null;
                }
            }
            dataOutputStream.writeInt(192);
            dataOutputStream.writeUTF(hash);
        } else {
            throw new ExecutionControl.NotImplementedException("User trying to leave lobby which isn't current.");
        }

    }

    private void startGameForLobby() throws IOException {
        LOG.debug(clientId + "sent a start game message.");

        currentLobby.addNewLobbyUpdate(new DisplayNextQuestionUpdate(triviaSet.getNextQuestion(-1).getQuestionID()));
        previousQuestionIndex++;

        // Send response to client.
        dataOutputStream.writeInt(138);
        dataOutputStream.writeUTF(hash);
        LOG.debug("Sent response to " + clientId);
    }

    private void nextQuestionForPlayers() throws IOException {
        long nextQuestionId = dataInputStream.readLong();
        currentLobby.addNewLobbyUpdate(new DisplayNextQuestionUpdate(nextQuestionId));
        dataOutputStream.writeInt(146);
        dataOutputStream.writeUTF(hash);
        LOG.debug("Registered lobby update event to diplay next question for everyone.");
    }

    private void closeLobby(LobbyDeletedUpdate lobbyDeletedUpdate) throws IOException {
        LOG.debug(clientId + "will receive \"lobby closed\" message");
        dataOutputStream.writeInt(194);
        dataOutputStream.writeUTF(hash);
        dataOutputStream.writeInt(lobbyDeletedUpdate.getLobbyCode());


        // Read the response
        int incomingCode = dataInputStream.readInt();
        if (incomingCode == 195) {
            if (dataInputStream.readUTF().equals(hash)) {
                if (currentLobby.getCode() == lobbyDeletedUpdate.getLobbyCode()) {
                    currentLobby = null;
                }
            } else {
                LOG.error("Invalid hash from client " + clientId);
            }
        } else {
            LOG.error("Client response was " + incomingCode);
        }

    }

    private void login() throws IOException {
        LOG.debug(clientId + "sent a login message.");

        String username = dataInputStream.readUTF();
        String password = dataInputStream.readUTF();

        try {
            this.user = UsersDatabaseLayer.validateUser(databaseConnection, username, password);

            // Serialize the user to return it.
            Gson gson = new GsonBuilder().registerTypeAdapter(User.class, new UserSerializer()).create();
            String userAsJson = gson.toJson(user);

            // Acknowledge and send the user.
            dataOutputStream.writeInt(122); // Login successful.
            dataOutputStream.writeUTF(this.hash);
            dataOutputStream.writeUTF(userAsJson);
            LOG.debug("Login successful, returned user object to " + clientId);
        } catch (IncorrectLoginInformationException e) {
            LOG.warn(clientId + "Incorrect login data.");
            dataOutputStream.writeInt(422); // Incorrect login information.
            dataOutputStream.writeUTF(this.hash);
        }
    }

    private void registerUser() throws IOException {
        LOG.debug(clientId + " wants to register new user");

        String username = dataInputStream.readUTF();
        String password = dataInputStream.readUTF();
        String nickname = dataInputStream.readUTF();

        try {
            UsersDatabaseLayer.registerUser(databaseConnection, username, password, nickname);
            dataOutputStream.writeInt(124);
            dataOutputStream.writeUTF(this.hash);

        } catch (UserRegistrationError e) {
            LOG.warn(clientId + "Failed to register user");
            dataOutputStream.writeInt(426); // User registration failed - try again
            dataOutputStream.writeUTF(this.hash);
        } catch (UserAlreadyExistsError e) {
            LOG.warn(clientId + "Failed to register user because username already exists in database");
            dataOutputStream.writeInt(424); // User already exists error code
            dataOutputStream.writeUTF(this.hash);
        }
    }

    private void registerTriviaSet() throws IOException {
        LOG.debug(clientId + " wants to register new triviaset");

        String triviaSetAsJson = dataInputStream.readUTF();
        LOG.debug("Trivia sets as Json received: " + triviaSetAsJson);
        Gson gsonReceive = new GsonBuilder().registerTypeAdapter(TriviaSet.class, new TriviaSetDeserializerFull()).create();
        LOG.debug("gsonReceive done");
        TriviaSet triviaSet = gsonReceive.fromJson(triviaSetAsJson, TriviaSet.class);
        LOG.debug("triviaset done");

        try {

            int triviaSetId = TriviaSetsDatabaseLayer.registerTriviaSet(databaseConnection, triviaSet, user.getId());
            LOG.debug("Triviaset ID " + triviaSetId + " successfully registered");
            LinkedHashMap<Integer, Question> questionList = triviaSet.getQuestionMap();
            questionList.forEach(((integer, question) -> QuestionsDatabaseLayer.registerQuestion(databaseConnection, question, triviaSetId)));
            LOG.debug("Questions successfully registered.");
            dataOutputStream.writeInt(126);
            dataOutputStream.writeUTF(this.hash);

        } catch (AnswerRegistrationError e) {

            LOG.warn(clientId + "Failed to register answer");
            dataOutputStream.writeInt(446); // Answer registration failed
            dataOutputStream.writeUTF(this.hash);

        } catch (QuestionRegistrationError e) {

            LOG.warn(clientId + "Failed to register question");
            dataOutputStream.writeInt(444); // Question registration failed
            dataOutputStream.writeUTF(this.hash);

        } catch (TriviaSetRegistrationError e) {

            LOG.warn(clientId + "Failed to register triviaset");
            dataOutputStream.writeInt(442); // Triviaset registration failed
            dataOutputStream.writeUTF(this.hash);

        }

    }

    private void sendQuestion() throws IOException {
        LOG.debug(clientId + " requests next question");
        long previousQuestionId = dataInputStream.readLong();

        if (previousQuestionId == -1) { // There was no previous question.
            // Return first question from trivia set.
        }

        try {
            // For testing
            TextQuestion testQuestion = new TextQuestion(AnswerType.FREEFORM, 0, false, "Nimeta riik Lõuna-Ameerikas", List.of(new Answer(2L, "Aafrika", false), new Answer(2L, "Ameerika", true)), 1000, 60);

            Gson gson = new GsonBuilder().registerTypeAdapter(Question.class, new QuestionSerializer()).create();
            String questionAsJson = gson.toJson(testQuestion);

            dataOutputStream.writeInt(202);
            dataOutputStream.writeUTF(this.hash);
            dataOutputStream.writeUTF(questionAsJson);
        } catch (IOException e) {
            // TODO: FetchingQuestionFromDatabaseError or something
            LOG.warn(clientId + "Failed to send next question");
            dataOutputStream.writeInt(436);
            dataOutputStream.writeUTF(this.hash);
        }
    }

    private void checkAnswer(boolean isFreeForm) throws IOException {
        LOG.debug(clientId + "sent user's answer to the question");

        long questionId = dataInputStream.readLong();

        String answer;
        if (isFreeForm) {
            answer = dataInputStream.readUTF();
        } else {
            Long answerId = dataInputStream.readLong();
            answer = triviaSet.getQuestionAnswer(questionId, answerId);
        }

        try {
            // Store user's answer
            boolean lastAnswer = Server.addUsersAnswerToLobby(user, currentLobby, questionId, answer);
            if (isFreeForm) {
                dataOutputStream.writeInt(204);
            } else {
                dataOutputStream.writeInt(206);
            }
            dataOutputStream.writeUTF(this.hash);

            if (lastAnswer) {
                currentLobby.addNewLobbyUpdate(new EveryoneAnswered(triviaSet.getNextQuestion(previousQuestionIndex).getQuestionID(), currentLobby.getLobbyOwnerId()));
            }
        } catch (AnswerStoringError e){
            LOG.warn(clientId + "Failed to store user's answer");
            dataOutputStream.writeInt(448);
            dataOutputStream.writeUTF(this.hash);
        }
    }

    private void everyoneAnswered(EveryoneAnswered everyoneAnswered) throws IOException {
        LOG.debug("Everyone has answered.");
        if (everyoneAnswered.getOwnerId() == user.getId()) {
            dataOutputStream.writeInt(142);
            dataOutputStream.writeUTF(hash);
            dataOutputStream.writeLong(everyoneAnswered.getNextQuestionId());

            int responseCode = dataInputStream.readInt();
            if (responseCode == 143) {
                if (!hash.equals(dataInputStream.readUTF())) {
                    LOG.error("Invalid hash returned.");
                }
            }
        }
    }

    private void sendTriviasets() throws IOException {
        LOG.debug(clientId + "requests trivia sets.");

        try {
            List<TriviaSet> usersTriviaSets = TriviaSetsDatabaseLayer.readUsersTriviaSets(databaseConnection, user.getId());
            LOG.debug("User's trivia sets successfully read from database");
            Gson gson = new GsonBuilder().registerTypeAdapter(List.class, new TriviaSetsSerializerBasic()).create();
            LOG.debug("Gson element done");
            String serializedTriviaSetList = gson.toJson(usersTriviaSets);
            LOG.debug("Trivia sets serialized: " + serializedTriviaSetList);

            dataOutputStream.writeInt(212);
            dataOutputStream.writeUTF(this.hash);
            dataOutputStream.writeUTF(serializedTriviaSetList);

        } catch (TriviaSetsFetchingError e){
            LOG.warn(clientId + "Failed to send trivia sets to user");
            dataOutputStream.writeInt(438);
            dataOutputStream.writeUTF(this.hash);
        }


    }


    private void sendFullTriviaSet() throws IOException {
        LOG.debug(clientId + "requests full trivia set.");

        Long triviaSetId = dataInputStream.readLong();

        try {
            TriviaSet triviaSet = TriviaSetsDatabaseLayer.readFullTriviaSet(databaseConnection, triviaSetId);
            LOG.debug("Successfully fetched trivia set from database");

            Gson gson = new GsonBuilder().registerTypeAdapter(TriviaSet.class, new TriviaSetSerializerFull()).create();
            String serializedTriviaSet = gson.toJson(triviaSet);
            LOG.debug("Serialized trivia set");

            dataOutputStream.writeInt(214);
            dataOutputStream.writeUTF(this.hash);
            dataOutputStream.writeUTF(serializedTriviaSet);

        } catch (QuestionsFetchingError e){
            LOG.debug("Failed to fetch questions for the requested trivia set");
            dataOutputStream.writeInt(440);
            dataOutputStream.writeUTF(this.hash);
        }
    }

    private void sync() throws IOException {
        LOG.debug(clientId + "Sent a sync message.");


        long serverTime = System.currentTimeMillis();

        dataOutputStream.writeInt(112); // Sync message 2
        dataOutputStream.writeUTF(this.hash);
        dataOutputStream.writeLong(serverTime);

        LOG.debug("Replied to the sync message for" + clientId);

    }
}
