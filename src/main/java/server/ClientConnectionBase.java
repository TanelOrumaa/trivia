package server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import configuration.Config;
import database.DatabaseConnection;
import database.UsersDatabaseLayer;
import exceptions.IncorrectLoginInformationException;
import exceptions.LobbyDoesNotExistException;
import general.Lobby;
import general.LobbySerializer;
import general.User;
import general.UserSerializer;
import general.commands.LobbyUpdateBase;
import general.commands.NewUserConnectedUpdate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;

public class ClientConnectionBase implements Runnable {

    // Logger
    static final Logger LOG = LoggerFactory.getLogger(PlayerClientConnection.class);

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
                            }
                        } else {
                            // If no updates from lobby either, sleep for a small amount of time to save CPU resources.
                            Thread.sleep(Config.SOCKET_POLL_INTERVAL);
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

    protected void handleIncoming(int code) throws IOException, InterruptedException {

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

                case 201: // Request a question
                    // The client requests a question from the server
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
        if (update.getUserId() != user.getId()) {
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

    private void createALobby() throws IOException {

        LOG.debug(clientId + "Sent a lobby creation message.");
        String lobbyName = dataInputStream.readUTF();
        LOG.debug("Creating a lobby with name \"" + lobbyName + "\" for user: " + user.getUsername());
        Lobby lobby = new Lobby(lobbyName, this.user);

        Gson gson = new GsonBuilder().registerTypeAdapter(Lobby.class, new LobbySerializer()).create();
        String lobbyAsJson = gson.toJson(lobby);

        dataOutputStream.writeInt(134); // Connection to lobby successful.
        dataOutputStream.writeUTF(this.hash);
        dataOutputStream.writeUTF(lobbyAsJson);

        currentLobby = lobby;
        LOG.debug(clientId + "Connection to lobby (" + lobby.getCode() + ") succesful.");

    }

    private void connectToLobby() throws IOException {
        LOG.debug(clientId + "Sent a lobby connection message.");


        int userSubmittedLobbyCode = dataInputStream.readInt();
        try {
            if (Server.isLobbyAvailable(userSubmittedLobbyCode)) {
                Lobby lobby = Server.addUserToLobby(this.user, userSubmittedLobbyCode);

                Gson gson = new GsonBuilder().registerTypeAdapter(Lobby.class, new LobbySerializer()).create();
                String lobbyAsJson = gson.toJson(lobby);

                dataOutputStream.writeInt(132); // Connection to lobby successful.
                dataOutputStream.writeUTF(this.hash);
                dataOutputStream.writeUTF(lobbyAsJson);
                LOG.debug(clientId + "Connection to lobby (" + userSubmittedLobbyCode + ") succesful.");

                currentLobby = lobby;

                // Updating lobby for all other clients.
                LOG.debug("Adding a new lobby update for lobby " + currentLobby.getCode() + ".");
                currentLobby.addNewLobbyUpdate(new NewUserConnectedUpdate(user.getId()));
            } else {
                LOG.warn(clientId + "This lobby (" + userSubmittedLobbyCode + ") is full.");
                dataOutputStream.writeInt(434); // Lobby is full.
            }

        } catch (LobbyDoesNotExistException e) {
            LOG.warn(clientId + "This lobby (" + userSubmittedLobbyCode + ") does not exist.");
            dataOutputStream.writeInt(432); // Lobby does not exist.
        }

    }

    private void login() throws IOException {
        LOG.debug(clientId + "Sent a login message.");


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

    private void sync() throws IOException {
        LOG.debug(clientId + "Sent a sync message.");


        long serverTime = System.currentTimeMillis();

        dataOutputStream.writeInt(112); // Sync message 2
        dataOutputStream.writeUTF(this.hash);
        dataOutputStream.writeLong(serverTime);

        LOG.debug("Replied to the sync message for" + clientId);

    }
}