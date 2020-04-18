package server;

import exceptions.IncorrectLoginInformationException;
import exceptions.LobbyDoesNotExistException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import database.DatabaseConnection;
import database.UsersDatabaseLayer;
import general.Lobby;
import general.LobbySerializer;
import general.User;
import general.UserSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;

public class PlayerClientConnection extends ServerRunnableBase {

    // Logger
    static final Logger LOG = LoggerFactory.getLogger(PlayerClientConnection.class);

    private DatabaseConnection databaseConnection;
    private Socket socket;
    private DataInputStream dataInputStream;
    private DataOutputStream dataOutputStream;
    private String hash;
    private String clientId;
    private User user;
    private Lobby currentLobby;

    public PlayerClientConnection(Socket socket, DataInputStream dataInputStream, String hash) {
        this.socket = socket;
        this.hash = hash;
        this.dataInputStream = dataInputStream;
        this.clientId = "[Client: " + socket.getInetAddress() + ":" + socket.getPort() + "] ";
    }

    @Override
    public void run() {
        try {
            // Subscribe to server updates
//            Server.subscribeToUpdates(this);

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

                int code = 0;

                // And then accept any other incoming messages.
                while (code != 199) { // Quit message
                    LOG.debug("Waiting for next message from " + clientId);
                    code = dataInputStream.readInt();
                    LOG.debug("Received message " + code + " from " + clientId);

                    switch (code) {
                        case 111: // Sync message 1
                            // The logic here is that the server receives the client message and immediately answers with
                            // server time. Client receives server time and calculates ping (from 111 message send time to
                            // 112 message receive time and finds time delta with clientTime - serverTime - ping / 2.
                            // This sync should happen periodically, but with some sort of decent logic, so messages with
                            // lower ping would be considered more trustworthy (so we don't overwrite the delta with more
                            // incorrect delta).

                            LOG.debug(clientId + "Sent a sync message.");

                            // Check that the hash matches.
                            if (dataInputStream.readUTF().equals(this.hash)) {
                                long serverTime = System.currentTimeMillis();

                                dataOutputStream.writeInt(112); // Sync message 2
                                dataOutputStream.writeUTF(this.hash);
                                dataOutputStream.writeLong(serverTime);

                                LOG.debug("Replied to the sync message for" + clientId);

                            } else {
                                LOG.debug(clientId + "Invalid hash from client.");
                                dataOutputStream.writeInt(404); // Invalid hash
                            }
                            break;

                        case 121: // Login message
                            // The client sends a log in message with username and password. Server validates the logins and
                            // responds with 122 for successful login or 422 for invalid login data.

                            LOG.debug(clientId + "Sent a login message.");

                            // First validate the hash.
                            if (dataInputStream.readUTF().equals(this.hash)) {
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
                            } else {
                                LOG.debug(clientId + "Invalid hash from client.");
                                dataOutputStream.writeInt(404); // Invalid hash.
                            }
                            break;

                        case 131: // Connecting to lobby message.
                            // Reads the lobby code and checks if lobby exists and if it does, can the user join the
                            // lobby. Returns 132 if lobby exists and the user has joined it, 432 if the lobby does not
                            // exist and 434 if the lobby is full.

                            LOG.debug(clientId + "Sent a lobby connection message.");

                            if (dataInputStream.readUTF().equals(this.hash)) {
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

                                        Server.sendLobbyUpdates(currentLobby);
                                    } else {
                                        LOG.warn(clientId + "This lobby (" + userSubmittedLobbyCode + ") is full.");
                                        dataOutputStream.writeInt(434); // Lobby is full.
                                    }

                                } catch (LobbyDoesNotExistException e) {
                                    LOG.warn(clientId + "This lobby (" + userSubmittedLobbyCode + ") does not exist.");
                                    dataOutputStream.writeInt(432); // Lobby does not exist.
                                }

                            } else {
                                LOG.debug(clientId + "Invalid hash from client.");
                                dataOutputStream.writeInt(404); // Invalid hash.
                            }
                            break;

                        case 133: // Creating a lobby message.
                            // Creates a new lobby and returns the lobby object to the user.

                            LOG.debug(clientId + "Sent a lobby creation message.");
                            if (dataInputStream.readUTF().equals(this.hash)) {
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

                            } else {
                                LOG.debug(clientId + "Invalid hash from client.");
                                dataOutputStream.writeInt(404); // Invalid hash.
                            }
                            
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
                }
            } catch (IOException e) {
                LOG.warn(clientId + "Unable to establish input/outputstreams with the client.");
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
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

    public void notifyClient(int messageCode) {
        try {
            LOG.debug("Notifying the user of lobby change.");
            Gson gson = new GsonBuilder().registerTypeAdapter(Lobby.class, new LobbySerializer()).create();
            String lobbyAsJson = gson.toJson(currentLobby);

            // Send the new lobby data.
            dataOutputStream.writeInt(136);
            dataOutputStream.writeUTF(hash);
            dataOutputStream.writeUTF(lobbyAsJson);

            // Wait for acknowledgement.
            int code = dataInputStream.readInt();
            if (code == 137) {
                if (dataInputStream.readUTF().equals(hash)) {
                    LOG.debug("Lobby updated for client.");
                } else {
                    LOG.warn(clientId + "Invalid hash from client.");
                }
            }
        } catch (IOException e) {
            LOG.error("Unable to notify client of lobby change.");
        }

    }

    public int getLobbyCode() {
        return currentLobby.getCode();
    }
}
