package general.baseclient;

import exceptions.MixedServerMessageException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import configuration.Config;
import general.*;
import general.questions.Question;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;

public class BaseClientBackEnd implements Runnable {
    protected BaseClient frontEnd;
    protected ClientType clientType;
    protected BlockingQueue<Question> questions;

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

    public BaseClientBackEnd(ClientType clientType, BaseClient frontEnd, BlockingQueue<Question> questions) {
        this.frontEnd = frontEnd;
        this.questions = questions;
        this.clientType = clientType;

        smallestPing = Long.MAX_VALUE;
        commandQueue = new CommandQueue();
    }

    public void run() {
        // Create connection with backend.
        try (Socket socket = new Socket(Config.SERVER_IP_ADDRESS, Config.SERVER_LISTENING_PORT)) {
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
                case HOST:
                    dataOutputStream.writeInt(3);
                    break;
            }

            // Receive acknowledgement + hash
            int code = dataInputStream.readInt();
            if (code == 102) {
                hash = dataInputStream.readUTF();
                System.out.println("Server ackowledged our \"Hello\" message. Our hash is now " + hash);
            } else {
                handleError(code);
            }

            // Do the first sync
            try {
                sync();
            } catch (IOException e) {
                System.out.println("Sync with server failed.");
            }

            System.out.println("Back-end started");

            // Run in loop until quit command is sent.
            while (true) {
                Command command;
                if (commandQueue.size() > 0 && (command = commandQueue.poll(now())) != null) {

                    if (command.code == 199) {
                        break;
                    } else {
                        handleCommand(command);
                    }
                } else {
                    // Try to read commands from server for sleep time. Since this is internal, we don't need to use
                    // servertime here.
                    long sleepUntil = System.currentTimeMillis() + Config.POLL_INTERVAL_MS;
                    while (System.currentTimeMillis() < sleepUntil) {
                        if (dataInputStream.available() > 0) {
                            code = dataInputStream.readInt();
                            handleIncoming(code);
                        }
                    }
                }
            }
        } catch (IOException e) {
            // TODO: Let the client know that connection to the server failed.
        } finally {
            try {
                dataInputStream.close();
                dataOutputStream.close();
            } catch (IOException e) {
                System.out.println("Unable to close input/output streams.");
            }
        }
    }

    public static void addCommand(int code, String[] commands, int delay) {
        commandQueue.add(new Command(code, commands, now() + delay));
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
            case 131: // Connect to lobby
                if (command.args != null && command.args.length == 1) {
                    connectToLobby(Integer.parseInt(command.args[0]));
                }
        }
    }

    private void handleIncoming(int commandCode) {
        // TODO: WIP
        System.out.println("Incoming message code: " + commandCode);
    }

    private void handleError(int errorCode) {
        // TODO: WIP
        System.out.println("ERROR: " + errorCode);
        // TODO: Currently just for testing, need to implement actual events for different errors.
        switch (errorCode) {
            case 402:
                System.out.println("Unexepcted message code.");
                break;
            case 404:
                System.out.println("We sent an incorrect hash.");
                break;
            case 422:
                System.out.println("Invalid login data.");
                break;
            case 432:
                System.out.println("Lobby does not exist.");
                break;
            case 434:
                System.out.println("Lobby is full.");
                break;
        }
    }

    private void sync() throws IOException {
        // Send sync intialization message.
        System.out.println("Sending sync message to server.");
        long syncStart = System.currentTimeMillis();
        dataOutputStream.writeInt(111);
        dataOutputStream.writeUTF(hash);

        int code = dataInputStream.readInt();
        if (code == 112) {
            System.out.println("Server accepted our sync message and responded.");
            String resonseHash = dataInputStream.readUTF();
            if (hash.equals(resonseHash)) {
                long serverTime = dataInputStream.readLong();
                long syncEnd = System.currentTimeMillis();
                long ping = syncEnd - syncStart;
                if (ping <= smallestPing) {
                    serverTimeDelta = syncEnd - serverTime - ping / 2;
                }
            } else {
                throw new MixedServerMessageException(hash, resonseHash);
            }
        } else if (code >= 400 && code < 500) {
            handleError(code);
        } else {
            handleIncoming(code);
        }

        // Queue next sync
        addCommand(111, new String[0], 60000);
    }

    private void login(String username, String password) throws IOException {
        // Send login message.
        System.out.println("Sending a login message to server.");
        dataOutputStream.writeInt(121);
        dataOutputStream.writeUTF(hash);
        dataOutputStream.writeUTF(username);
        dataOutputStream.writeUTF(password);

        // Receive response.
        int responseCode = dataInputStream.readInt();
        if (responseCode == 122) {
            System.out.println("Server accepted our login message.");
            // Login was successful. Check if hash matches.
            String responseHash = dataInputStream.readUTF();
            if (hash.equals(responseHash)) {
                // Read and deserialize the user object.
                String userJson = dataInputStream.readUTF();
                Gson gson = new GsonBuilder().registerTypeAdapter(User.class, new UserDeserializer()).create();
                user = gson.fromJson(userJson, User.class);
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
        System.out.println("Sending a connect to lobby message with code " + lobbyCode);
        dataOutputStream.writeInt(131);
        dataOutputStream.writeUTF(hash);
        dataOutputStream.writeInt(lobbyCode);

        // Read the response
        int responseCode = dataInputStream.readInt();

        if (responseCode == 132) {
            System.out.println("Server accepted the lobby connection message.");
            // Connecting to lobby was successful. Check if hash matches.
            String responseHash = dataInputStream.readUTF();
            if (hash.equals(responseHash)) {
                // Read the lobby object and deserialize it.
                String lobbyJson = dataInputStream.readUTF();

                Gson gson = new GsonBuilder().registerTypeAdapter(LobbyFX.class, new LobbyDeserializer()).create();
                currentLobby = gson.fromJson(lobbyJson, Lobby.class);
                System.out.println("Connected to lobby " + currentLobby.getCode());
            } else {
                throw new MixedServerMessageException(hash, responseHash);
            }
        } else if (responseCode >= 400 && responseCode < 500) {
            handleError(responseCode);
        } else {
            handleIncoming(responseCode);
        }
    }

    private static long now() {
        return System.currentTimeMillis() + serverTimeDelta;
    }
}