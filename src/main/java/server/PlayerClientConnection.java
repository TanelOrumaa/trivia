package server;

import Exceptions.IncorrectLoginInformationException;
import Exceptions.LobbyDoesNotExistException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import database.DatabaseConnection;
import database.UsersDatabaseLayer;
import general.Lobby;
import general.LobbySerializer;
import general.User;
import general.UserSerializer;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;

public class PlayerClientConnection extends ServerRunnableBase {

    private DatabaseConnection databaseConnection;
    private Socket socket;
    private String hash;
    private User user;

    public PlayerClientConnection(Socket socket, String hash) {
        this.socket = socket;
        this.hash = hash;
    }

    @Override
    public void run() {
        try {
            this.databaseConnection = new DatabaseConnection();

            try (
                    DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
                    DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream())) {

                // First acknowledge that we received the hello message and assign them a unique hash.
                dataOutputStream.writeInt(102);
                dataOutputStream.writeUTF(this.hash);

                int code = 0;

                // And then accept any other incoming messages.
                while (code != 199) { // Quit message
                    code = dataInputStream.readInt();

                    switch (code) {
                        case 111: // Sync message 1
                            // The logic here is that the server receives the client message and immediately answers with
                            // server time. Client receives server time and calculates ping (from 111 message send time to
                            // 112 message receive time and finds time delta with clientTime - serverTime - ping / 2.
                            // This sync should happen periodically, but with some sort of decent logic, so messages with
                            // lower ping would be considered more trustworthy (so we don't overwrite the delta with more
                            // incorrect delta).

                            System.out.println("Client sent a sync message.");

                            // Check that the hash matches.
                            if (dataInputStream.readUTF().equals(this.hash)) {
                                long serverTime = System.currentTimeMillis();

                                dataOutputStream.writeInt(112); // Sync message 2
                                dataOutputStream.writeUTF(this.hash);
                                dataOutputStream.writeLong(serverTime);

                                // Sent sync

                            } else {
                                dataOutputStream.writeInt(404); // Invalid hash
                            }
                            break;

                        case 121: // Login message
                            // The client sends a log in message with username and password. Server validates the logins and
                            // responds with 122 for successful login or 422 for invalid login data.

                            System.out.println("Client sent a login message.");

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
                                } catch (IncorrectLoginInformationException e) {
                                    dataOutputStream.writeInt(422); // Incorrect login information.
                                    dataOutputStream.writeUTF(this.hash);
                                }
                            } else {
                                dataOutputStream.writeInt(404); // Invalid hash.
                            }
                            break;

                        case 131: // Connecting to lobby message.
                            // Reads the lobby code and checks if lobby exists and if it does, can the user join the
                            // lobby. Returns 132 if lobby exists and the user has joined it, 432 if the lobby does not
                            // exist and 434 if the lobby is full.

                            System.out.println("Client sent a lobby connection message.");

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

                                        // TODO: Notify all users of the new person connecting to the lobby.
                                    } else {
                                        dataOutputStream.writeInt(434); // Lobby is full.
                                    }

                                } catch (LobbyDoesNotExistException e) {
                                    dataOutputStream.writeInt(432); // Lobby does not exist.
                                }

                            } else {
                                dataOutputStream.writeInt(404); // Invalid hash.
                            }
                            break;

                        case 201: // Request a question
                            // The client requests a question from the server
                            break;

                    }

                }

            } catch (IOException e) {
                System.out.println("Unable to establish input/outputstreams with the client " + socket.getInetAddress());
                e.printStackTrace();
            } finally {
                // Close the socket and connection
                try {
                    socket.close();
                    databaseConnection.closeDatabaseConnection();
                } catch (IOException e) {
                    System.out.println("Unable to close socket.");
                }
            }
        } catch (SQLException e) {
            System.out.println("Unable to create database connection.");
        }
    }
}
